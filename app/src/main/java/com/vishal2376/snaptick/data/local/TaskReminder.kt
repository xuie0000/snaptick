package com.vishal2376.snaptick.data.local

import androidx.room.Entity

/**
 * Per-task reminder offset. Multiple rows per task = multiple reminders
 * (e.g., on-time, 5 min before, 10 min before). Primary key is composite so
 * the same offset can't be inserted twice for the same task.
 */
@Entity(
	tableName = "task_reminders",
	primaryKeys = ["uuid", "offsetMinutes"],
)
data class TaskReminder(
	val uuid: String,
	val offsetMinutes: Int,
)
