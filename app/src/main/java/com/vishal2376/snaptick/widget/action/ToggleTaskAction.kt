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

// Optimistic widget push first; DB write deferred so the click returns instantly.
class ToggleTaskAction : ActionCallback {

	companion object {
		val TaskIdKey = ActionParameters.Key<Int>("task_id")

		// Survives after the suspend callback returns.
		private val backgroundScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
	}

	override suspend fun onAction(
		context: Context,
		glanceId: GlanceId,
		parameters: ActionParameters
	) {
		val taskId = parameters[TaskIdKey] ?: return

		val current = WidgetStateDefinition.getDataStore(context, "snaptick_widget_state")
			.data.first()
		val nextTasks = current.tasks.filterNot { it.id == taskId }
		if (nextTasks.size != current.tasks.size) {
			WidgetStateDefinition.updateState(context, current.copy(tasks = nextTasks))
			TaskAppWidget().update(context, glanceId)
		}

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
