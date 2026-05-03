package com.vishal2376.snaptick.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.vishal2376.snaptick.BuildConfig
import com.vishal2376.snaptick.MainActivity
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.service.PomodoroService

const val NOTIFICATION = "Notification"
const val CHANNEL_ID = "snaptick-notification"
const val CHANNEL_NAME = "Task Reminder"

class NotificationHelper(private val context: Context) {

	private val notificationManager =
		context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

	fun showNotification(taskId: Int, taskTitle: String, taskTime: String) {
		if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
			if (BuildConfig.DEBUG) {
				Log.w("NotificationHelper", "Notifications disabled; skipping reminder")
			}
			return
		}

		val openIntent = Intent(context, MainActivity::class.java).apply {
			flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
		}
		val openPending = PendingIntent.getActivity(
			context,
			taskId,
			openIntent,
			PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
		)

		val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
			.setContentTitle(taskTitle)
			.setContentText(taskTime)
			.setSmallIcon(R.drawable.ic_notification)
			.setStyle(NotificationCompat.BigTextStyle().bigText(taskTime))
			.setDefaults(NotificationCompat.DEFAULT_ALL)
			.setContentIntent(openPending)
			.setAutoCancel(true)
			.addAction(
				0,
				context.getString(R.string.start_pomodoro),
				startPomodoroPendingIntent(taskId),
			)

		try {
			notificationManager.notify(taskId, notificationBuilder.build())
		} catch (e: SecurityException) {
			Log.e("NotificationHelper", "notify() denied", e)
		}
	}

	/**
	 * Builds a PendingIntent that, when fired, kicks the foreground
	 * [PomodoroService] for [taskId]. Use a per-task request code so multiple
	 * outstanding reminder notifications don't collide on the same intent.
	 */
	private fun startPomodoroPendingIntent(taskId: Int): PendingIntent {
		val intent = Intent(context, PomodoroService::class.java).apply {
			action = PomodoroService.ACTION_START_FOR_TASK
			putExtra(PomodoroService.EXTRA_TASK_ID, taskId)
		}
		val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
		// getForegroundService is required when targeting API 26+; the notif
		// action is a user gesture so the FG-service start is allowed.
		return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			PendingIntent.getForegroundService(
				context,
				taskId * 10 + 1,
				intent,
				flags,
			)
		} else {
			PendingIntent.getService(context, taskId * 10 + 1, intent, flags)
		}
	}

	fun createNotificationChannel() {
		val mChannel =
			NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
		notificationManager.createNotificationChannel(mChannel)
	}
}
