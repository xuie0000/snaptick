package com.vishal2376.snaptick.widget.action

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import com.vishal2376.snaptick.widget.TaskAppWidget
import com.vishal2376.snaptick.widget.di.WidgetEntryPoint
import com.vishal2376.snaptick.widget.state.WidgetStateDefinition
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * Toggles task completion from the widget. Two phases:
 *
 *  1. Foreground (this coroutine): drop the row from the widget's visible
 *     list and push the new state to *this widget instance only*. Returning
 *     fast lets Glance hand the click back to the launcher with no perceived
 *     lag. Single-instance update beats `updateAll` because Glance can skip
 *     re-rendering every other host.
 *  2. Background (process-lifetime scope): do the repository write. The
 *     repo's own side-effects already enqueue a [WidgetUpdateWorker] run
 *     for full reconciliation, so we don't kick a refresh from here — that
 *     would just do the same work twice and risk a brief re-render flicker.
 */
class ToggleTaskAction : ActionCallback {

	companion object {
		val TaskIdKey = ActionParameters.Key<Int>("task_id")

		// Process-lifetime scope so DB writes survive after this suspending
		// callback returns. Using a viewmodel/worker-bound scope would tie
		// the lifetime to a host that's already gone.
		private val backgroundScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
	}

	override suspend fun onAction(
		context: Context,
		glanceId: GlanceId,
		parameters: ActionParameters
	) {
		val taskId = parameters[TaskIdKey] ?: return

		// Phase 1: optimistic visual update on this instance.
		val current = WidgetStateDefinition.getDataStore(context, "snaptick_widget_state")
			.data.first()
		val nextTasks = current.tasks.filterNot { it.id == taskId }
		if (nextTasks.size != current.tasks.size) {
			WidgetStateDefinition.updateState(context, current.copy(tasks = nextTasks))
			TaskAppWidget().update(context, glanceId)
		}

		// Phase 2: defer DB write so we don't keep the action coroutine alive
		// across reminder rescheduling, calendar push, and worker enqueueing.
		backgroundScope.launch {
			val entry = EntryPointAccessors
				.fromApplication(context.applicationContext, WidgetEntryPoint::class.java)
			val repo = entry.taskRepository()
			val task = repo.getTaskById(taskId) ?: return@launch
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
		}
	}
}
