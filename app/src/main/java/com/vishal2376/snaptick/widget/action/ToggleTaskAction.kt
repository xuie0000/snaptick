package com.vishal2376.snaptick.widget.action

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.updateAll
import com.vishal2376.snaptick.widget.TaskAppWidget
import com.vishal2376.snaptick.widget.di.WidgetEntryPoint
import com.vishal2376.snaptick.widget.state.WidgetStateDefinition
import com.vishal2376.snaptick.widget.worker.WidgetUpdateWorker
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.time.LocalDate

/**
 * Toggles task completion from the widget. Two-phase for instant feedback:
 *
 * 1. Optimistic: read current widget state, drop the toggled task from the
 *    visible incomplete list, push that synthetic state immediately so the
 *    user sees the row disappear with no perceived lag.
 * 2. Authoritative: do the actual repository write (which cancels and
 *    reschedules reminders, pushes to calendar, etc.) and then refresh the
 *    widget state from the DB so any divergence (e.g., new repeat
 *    occurrence) reconciles.
 *
 * Routes the actual write through `TaskRepository` so the same per-date
 * completion semantics apply as in the home screen:
 *  - One-off task: flips `Task.isCompleted`.
 *  - Repeat template: writes (or removes) a row in `task_completions` for
 *    today, leaving the template untouched.
 */
class ToggleTaskAction : ActionCallback {

	companion object {
		val TaskIdKey = ActionParameters.Key<Int>("task_id")
	}

	override suspend fun onAction(
		context: Context,
		glanceId: GlanceId,
		parameters: ActionParameters
	) {
		val taskId = parameters[TaskIdKey] ?: return

		withContext(Dispatchers.IO) {
			val entry = EntryPointAccessors
				.fromApplication(context.applicationContext, WidgetEntryPoint::class.java)
			val repo = entry.taskRepository()
			val settings = entry.settingsStore()

			// Optimistic: drop the row from the widget's visible list immediately.
			val current = runCatching {
				WidgetStateDefinition.getDataStore(context, "snaptick_widget_state").data.first()
			}.getOrNull()
			if (current != null) {
				val nextTasks = current.tasks.filterNot { it.id == taskId }
				if (nextTasks.size != current.tasks.size) {
					WidgetStateDefinition.updateState(context, current.copy(tasks = nextTasks))
					TaskAppWidget().updateAll(context)
				}
			}

			// Authoritative: now do the real write + refresh.
			val task = repo.getTaskById(taskId) ?: return@withContext

			if (task.isRepeated) {
				val today = LocalDate.now()
				if (repo.isCompletedOn(task.uuid, today)) {
					repo.unmarkCompletedForDate(task.uuid, today)
				} else {
					repo.markCompletedForDate(task.uuid, today)
				}
			} else {
				repo.updateTask(task.copy(isCompleted = !task.isCompleted))
			}

			WidgetUpdateWorker.refreshNow(context, repo, settings)
		}
	}
}
