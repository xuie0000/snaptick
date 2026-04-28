package com.vishal2376.snaptick.widget.worker

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.updateAll
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.vishal2376.snaptick.BuildConfig
import com.vishal2376.snaptick.data.repositories.TaskRepository
import com.vishal2376.snaptick.presentation.common.AppTheme
import com.vishal2376.snaptick.util.SettingsStore
import com.vishal2376.snaptick.widget.TaskAppWidget
import com.vishal2376.snaptick.widget.model.WidgetState
import com.vishal2376.snaptick.widget.state.WidgetStateDefinition
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

private const val TAG = "WidgetUpdateWorker"

/**
 * Worker that syncs widget data with the app's tasks and settings.
 * Fetches today's incomplete tasks, time format, and theme preferences,
 * then updates the widget state and triggers a widget refresh.
 */
@HiltWorker
class WidgetUpdateWorker @AssistedInject constructor(
	@Assisted private val context: Context,
	@Assisted params: WorkerParameters,
	private val taskRepository: TaskRepository,
	private val settingsStore: SettingsStore
) : CoroutineWorker(context, params) {

	override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
		try {
			val count = refreshNow(context, taskRepository, settingsStore)
			Result.success(workDataOf(SUCCESS_KEY to "Widget updated with $count tasks"))
		} catch (e: Exception) {
			Log.e(TAG, "Widget data sync failed", e)
			Result.failure(workDataOf(ERROR_KEY to e.message))
		}
	}

	companion object {
		private const val WORKER_NAME = "widget_update_worker"
		private const val PERIODIC_WORKER_NAME = "widget_periodic_update_worker"
		const val SUCCESS_KEY = "widget_update_success"
		const val ERROR_KEY = "widget_update_error"

		/**
		 * Runs the widget refresh inline on the caller's coroutine. Used by
		 * Glance actions (toggle, etc.) where WorkManager dispatch latency
		 * makes the widget feel laggy. Returns the count of incomplete tasks
		 * pushed to widget state.
		 */
		suspend fun refreshNow(
			context: Context,
			taskRepository: TaskRepository,
			settingsStore: SettingsStore,
		): Int {
			if (BuildConfig.DEBUG) Log.d(TAG, "Starting inline widget data sync")

			val allTasks = taskRepository.getTodayTasksWithCompletions().first()
			val dayOfWeek = LocalDate.now().dayOfWeek.value - 1
			val incompleteTasks = allTasks.filter { task ->
				if (task.isRepeated) task.getRepeatWeekList().contains(dayOfWeek) else true
			}.filter { !it.isCompleted }

			val is24HourFormat = settingsStore.timeFormatKey.first()
			val themeOrdinal = settingsStore.themeKey.first()
			val theme = AppTheme.entries.getOrElse(themeOrdinal) { AppTheme.Amoled }
			val useDynamicTheme = settingsStore.dynamicThemeKey.first()

			val newState = WidgetState(
				tasks = incompleteTasks,
				is24HourFormat = is24HourFormat,
				theme = theme,
				useDynamicTheme = useDynamicTheme,
			)
			WidgetStateDefinition.updateState(context, newState)
			TaskAppWidget().updateAll(context)
			return incompleteTasks.size
		}

		/**
		 * Enqueue a one-time widget update.
		 * Use this when tasks or settings change.
		 */
		fun enqueueWorker(
			context: Context,
			policy: ExistingWorkPolicy = ExistingWorkPolicy.REPLACE
		) {
			val workRequest = OneTimeWorkRequestBuilder<WidgetUpdateWorker>()
				.addTag(WORKER_NAME)
				.build()

			WorkManager.getInstance(context)
				.enqueueUniqueWork(WORKER_NAME, policy, workRequest)
		}

		/**
		 * Enqueue a periodic widget update that runs daily at midnight.
		 * Use this when the widget is first enabled.
		 */
		fun enqueuePeriodicWorker(
			context: Context,
			policy: ExistingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.KEEP
		) {
			// Calculate delay until midnight
			val now = LocalTime.now()
			val midnight = LocalTime.MAX
			val delayUntilMidnight = Duration.between(now, midnight)
				.plus(Duration.ofSeconds(2)) // Small buffer after midnight

			val periodicRequest = PeriodicWorkRequestBuilder<WidgetUpdateWorker>(
				Duration.ofDays(1)
			)
				.addTag(PERIODIC_WORKER_NAME)
				.setInitialDelay(delayUntilMidnight)
				.build()

			WorkManager.getInstance(context)
				.enqueueUniquePeriodicWork(PERIODIC_WORKER_NAME, policy, periodicRequest)
		}

		/**
		 * Cancel the periodic widget update worker.
		 * Use this when the widget is disabled.
		 */
		fun cancelPeriodicWorker(context: Context) {
			WorkManager.getInstance(context)
				.cancelUniqueWork(PERIODIC_WORKER_NAME)
		}

		/**
		 * Cancel the one-time widget update worker.
		 */
		fun cancelWorker(context: Context) {
			WorkManager.getInstance(context)
				.cancelUniqueWork(WORKER_NAME)
		}
	}
}
