package com.vishal2376.snaptick.domain.model

const val BACKUP_VERSION = 2

data class BackupData(
	val version: Int = BACKUP_VERSION,
	val tasks: List<Task> = emptyList(),
	val completions: List<BackupCompletion> = emptyList(),
	val reminders: List<BackupReminder> = emptyList(),
)

data class BackupCompletion(
	val uuid: String,
	val date: String
)

data class BackupReminder(
	val uuid: String,
	val offsetMinutes: Int,
)