package com.vishal2376.snaptick.data.repositories

import android.content.Context
import androidx.room.withTransaction
import com.vishal2376.snaptick.data.calendar.CalendarPusher
import com.vishal2376.snaptick.data.local.TaskCompletion
import com.vishal2376.snaptick.data.local.TaskCompletionDao
import com.vishal2376.snaptick.data.local.TaskDao
import com.vishal2376.snaptick.data.local.TaskDatabase
import com.vishal2376.snaptick.data.local.TaskReminder
import com.vishal2376.snaptick.data.local.TaskReminderDao
import com.vishal2376.snaptick.domain.model.BackupCompletion
import com.vishal2376.snaptick.domain.model.BackupData
import com.vishal2376.snaptick.domain.model.BackupReminder
import com.vishal2376.snaptick.domain.model.Task
import com.vishal2376.snaptick.service.PomodoroService
import com.vishal2376.snaptick.util.ReminderScheduler
import com.vishal2376.snaptick.widget.worker.WidgetUpdateWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import java.time.LocalDate

class TaskRepository(
	private val dao: TaskDao,
	private val completionDao: TaskCompletionDao,
	private val reminderDao: TaskReminderDao,
	private val database: TaskDatabase,
	private val context: Context,
	private val calendarPusher: CalendarPusher,
	private val reminderScheduler: ReminderScheduler,
) {
	suspend fun insertTask(task: Task, reminderOffsets: List<Int> = defaultOffsets(task)) {
		dao.insertTask(task)
		val saved = dao.getTaskByUuid(task.uuid) ?: task
		writeReminderOffsets(saved.uuid, reminderOffsets)
		reminderScheduler.schedule(saved, offsets = reminderOffsets)
		calendarPusher.pushInsert(saved)
		WidgetUpdateWorker.enqueueWorker(context)
	}

	suspend fun deleteTask(task: Task) {
		reminderScheduler.cancel(task.id)
		PomodoroService.stopIfRunningFor(context, task.id)
		completionDao.deleteAllForTask(task.uuid)
		reminderDao.deleteAllForTask(task.uuid)
		dao.deleteTask(task)
		calendarPusher.pushDelete(task)
		WidgetUpdateWorker.enqueueWorker(context)
	}

	suspend fun updateTask(task: Task, reminderOffsets: List<Int>? = null) {
		reminderScheduler.cancel(task.id)
		// Completing a task should kill its timer + clear the FG notification.
		// Skip when the caller is the pomodoro service itself wiping
		// pomodoroTimer back to -1 (still incomplete) so we don't recurse.
		if (task.isCompleted) PomodoroService.stopIfRunningFor(context, task.id)
		dao.updateTask(task)
		val effective = reminderOffsets ?: reminderDao.offsetsForTask(task.uuid).ifEmpty { defaultOffsets(task) }
		if (reminderOffsets != null) writeReminderOffsets(task.uuid, reminderOffsets)
		reminderScheduler.schedule(task, offsets = effective)
		calendarPusher.pushUpdate(task)
		WidgetUpdateWorker.enqueueWorker(context)
	}

	suspend fun getReminderOffsets(uuid: String): List<Int> = reminderDao.offsetsForTask(uuid)

	private suspend fun writeReminderOffsets(uuid: String, offsets: List<Int>) {
		reminderDao.deleteAllForTask(uuid)
		if (offsets.isNotEmpty()) {
			reminderDao.insertAll(offsets.map { TaskReminder(uuid = uuid, offsetMinutes = it) })
		}
	}

	private fun defaultOffsets(task: Task): List<Int> =
		if (task.reminder) listOf(0) else emptyList()

	suspend fun getTaskById(id: Int): Task? {
		return dao.getTaskById(id)
	}

	suspend fun deleteAllTasks() {
		// Wipe takes the running timer with it.
		val active = PomodoroService.runningTaskId
		if (active > 0) PomodoroService.stopIfRunningFor(context, active)
		dao.deleteAllTasks()
		WidgetUpdateWorker.enqueueWorker(context)
	}

	fun getTasksByDate(selectedDate: LocalDate): Flow<List<Task>> {
		return dao.getTasksByDate(selectedDate.toString())
	}

	fun getTodayTasks(): Flow<List<Task>> {
		return dao.getTasksByDate(LocalDate.now().toString())
	}

	/**
	 * Today's tasks with per-date completion state merged in. The list is the
	 * union of one-off tasks dated today and repeat templates whose creation
	 * date is on or before today and whose weekday matches today. Repeat
	 * templates have their `isCompleted` flag flipped to `true` when a row
	 * exists in `task_completions` for `(uuid, today)`.
	 */
	fun getTodayTasksWithCompletions(): Flow<List<Task>> {
		val today = LocalDate.now()
		val todayIso = today.toString()
		return combine(
			dao.getTasksByDate(todayIso),
			dao.getActiveRepeats(todayIso),
			completionDao.completedUuidsOn(todayIso),
		) { dated, repeats, completedUuids ->
			val completedSet = completedUuids.toHashSet()
			val seen = HashSet<Int>()
			val out = ArrayList<Task>(dated.size + repeats.size)
			for (task in dated) {
				if (seen.add(task.id) && task.shouldOccurOn(today)) out += task
			}
			for (task in repeats) {
				if (task.id in seen) continue
				if (!task.shouldOccurOn(today)) continue
				val merged = if (task.uuid in completedSet) task.copy(isCompleted = true) else task
				out += merged
				seen += task.id
			}
			out
		}
	}

	fun getAllTasks(): Flow<List<Task>> {
		return dao.getAllTasks().onEach {
			WidgetUpdateWorker.enqueueWorker(context)
		}
	}

	suspend fun getAllTasksSnapshot(): List<Task> = dao.getAllTasksSnapshot()

	suspend fun snapshotBackup(): BackupData {
		val tasks = dao.getAllTasksSnapshot()
		val completions = completionDao.getAllSnapshot()
			.map { BackupCompletion(uuid = it.uuid, date = it.date) }
		val reminders = reminderDao.getAllSnapshot()
			.map { BackupReminder(uuid = it.uuid, offsetMinutes = it.offsetMinutes) }
		return BackupData(tasks = tasks, completions = completions, reminders = reminders)
	}

	/**
	 * Atomically replaces the database contents with [data]. Wipes both
	 * `task_table` and `task_completions`, then inserts the provided rows
	 * inside a single transaction so a mid-restore failure rolls back to the
	 * pre-restore state. Reminder rearm + widget refresh happen after commit.
	 */
	suspend fun restoreFromBackup(data: BackupData) {
		database.withTransaction {
			dao.deleteAllTasks()
			completionDao.deleteAll()
			reminderDao.deleteAll()
			for (task in data.tasks) dao.insertTask(task)
			if (data.completions.isNotEmpty()) {
				completionDao.insertAll(
					data.completions.map { TaskCompletion(uuid = it.uuid, date = it.date) }
				)
			}
			val explicitReminders = data.reminders.map {
				TaskReminder(uuid = it.uuid, offsetMinutes = it.offsetMinutes)
			}
			val backfilled = if (explicitReminders.isEmpty()) {
				// v1 backups had no reminders payload; recreate (uuid, 0) for any
				// task with reminder = true so the existing on-time behavior is
				// preserved across the format bump.
				data.tasks.filter { it.reminder }
					.map { TaskReminder(uuid = it.uuid, offsetMinutes = 0) }
			} else {
				explicitReminders
			}
			if (backfilled.isNotEmpty()) reminderDao.insertAll(backfilled)
		}
		val saved = dao.getAllTasksSnapshot()
		reminderScheduler.rescheduleAll(saved.map { task ->
			task to reminderDao.offsetsForTask(task.uuid)
		})
		WidgetUpdateWorker.enqueueWorker(context)
	}

	/**
	 * Records a completion for the given repeating task on the given date.
	 * One-off task completion still flows through `updateTask`. After writing,
	 * we re-arm the reminder so the next-fire instant skips today and points
	 * at the following scheduled occurrence.
	 */
	suspend fun markCompletedForDate(uuid: String, date: LocalDate) {
		completionDao.insert(TaskCompletion(uuid = uuid, date = date.toString()))
		dao.getTaskByUuid(uuid)?.let { task ->
			// Repeat template was completed for today: any pomodoro for this
			// task is considered done too.
			if (date == LocalDate.now()) {
				PomodoroService.stopIfRunningFor(context, task.id)
			}
			val offsets = reminderDao.offsetsForTask(uuid)
			reminderScheduler.cancel(task.id)
			reminderScheduler.schedule(task, offsets = offsets, skipToday = date == LocalDate.now())
		}
		WidgetUpdateWorker.enqueueWorker(context)
	}

	suspend fun unmarkCompletedForDate(uuid: String, date: LocalDate) {
		completionDao.delete(uuid, date.toString())
		dao.getTaskByUuid(uuid)?.let { task ->
			val offsets = reminderDao.offsetsForTask(uuid)
			reminderScheduler.cancel(task.id)
			reminderScheduler.schedule(task, offsets = offsets)
		}
		WidgetUpdateWorker.enqueueWorker(context)
	}

	suspend fun isCompletedOn(uuid: String, date: LocalDate): Boolean {
		return completionDao.isCompleted(uuid, date.toString())
	}

	/** Pushes every task that doesn't yet have a calendar event id to the
	 *  selected device calendar. Cheap no-op when sync is disabled. */
	suspend fun syncAllTasksNow() {
		val all = dao.getAllTasksSnapshot()
		calendarPusher.pushAllUnmirrored(all)
	}

	/**
	 * Removes every device-calendar event Snaptick has ever pushed and clears
	 * each task's `calendarEventId`. Called when the user turns calendar sync
	 * off so previously-mirrored events don't keep living in their Google
	 * Calendar. Returns the number of events actually deleted.
	 */
	suspend fun deletePushedCalendarEvents(): Int {
		val all = dao.getAllTasksSnapshot()
		return calendarPusher.deleteAllPushedEvents(all)
	}

	/** Used by the boot-recovery worker to re-arm every active reminder. */
	suspend fun rescheduleAllReminders() {
		val all = dao.getAllTasksSnapshot().filter { it.reminder && !it.isCompleted }
		reminderScheduler.rescheduleAll(
			all.map { task ->
				val stored = reminderDao.offsetsForTask(task.uuid)
				task to stored.ifEmpty { defaultOffsets(task) }
			}
		)
	}
}
