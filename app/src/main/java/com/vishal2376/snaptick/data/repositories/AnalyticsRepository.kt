package com.vishal2376.snaptick.data.repositories

import com.vishal2376.snaptick.data.local.TaskCompletionDao
import com.vishal2376.snaptick.data.local.TaskDao
import com.vishal2376.snaptick.domain.model.AnalyticsSnapshot
import com.vishal2376.snaptick.domain.model.DailyStats
import com.vishal2376.snaptick.domain.model.StreakInfo
import com.vishal2376.snaptick.domain.model.Task
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Read-only stats derived from `task_table` and `task_completions`. No new
 * schema. All math is client-side; queries are bounded snapshots.
 */
@Singleton
class AnalyticsRepository @Inject constructor(
	private val dao: TaskDao,
	private val completionDao: TaskCompletionDao,
) {

	suspend fun snapshot(rangeDays: Int = 30): AnalyticsSnapshot {
		val today = LocalDate.now()
		val from = today.minusDays(rangeDays.toLong() - 1)
		val tasks = dao.getAllTasksSnapshot()
		val completionRows = completionDao.getAllSnapshot()

		val daily = (0 until rangeDays).map { offset ->
			val day = from.plusDays(offset.toLong())
			val occurring = tasks.filter { it.shouldOccurOn(day) && !it.isAllDayTaskEnabled() }
			val durationSec = occurring.sumOf { it.getDuration() }
			val completed = occurring.count { task ->
				if (task.isRepeated) {
					completionRows.any { it.uuid == task.uuid && it.date == day.toString() }
				} else {
					task.date == day && task.isCompleted
				}
			}
			day to DailyStats(
				date = day,
				completed = completed,
				total = occurring.size,
				durationSeconds = durationSec,
			)
		}.toMap()

		val streak = computeStreak(tasks, completionRows.map { it.uuid to it.date }, today)
		val pomodoroSessions = tasks.count { it.pomodoroTimer != -1 && it.pomodoroTimer > 0 }
		val totalDurationSec = tasks
			.filter { it.isCompleted && !it.isAllDayTaskEnabled() }
			.sumOf { it.getDuration() }
		val avgDurationSec = if (tasks.isNotEmpty())
			tasks.filter { !it.isAllDayTaskEnabled() }.let { list ->
				if (list.isEmpty()) 0L else list.sumOf { it.getDuration() } / list.size
			}
		else 0L

		return AnalyticsSnapshot(
			rangeStart = from,
			rangeEnd = today,
			daily = daily,
			streak = streak,
			pomodoroSessionsCompleted = pomodoroSessions,
			averageTaskDurationSec = avgDurationSec,
			totalTimeInvestedSec = totalDurationSec,
		)
	}

	private fun computeStreak(
		tasks: List<Task>,
		completions: List<Pair<String, String>>,
		today: LocalDate,
	): StreakInfo {
		val completionDates = completions.mapTo(HashSet()) { it.second }
		val oneOffCompletedDates = tasks.filter { !it.isRepeated && it.isCompleted }
			.mapTo(HashSet()) { it.date.toString() }

		fun anyCompletedOn(d: LocalDate): Boolean {
			val s = d.toString()
			return s in completionDates || s in oneOffCompletedDates
		}

		var current = 0
		var d = today
		while (anyCompletedOn(d)) {
			current++
			d = d.minusDays(1)
		}

		var longest = 0
		var run = 0
		val cursorEnd = today
		val cursorStart = today.minusDays(365)
		var cursor = cursorStart
		while (!cursor.isAfter(cursorEnd)) {
			if (anyCompletedOn(cursor)) {
				run++
				if (run > longest) longest = run
			} else {
				run = 0
			}
			cursor = cursor.plusDays(1)
		}

		return StreakInfo(current = current, longest = longest)
	}
}
