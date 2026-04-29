package com.vishal2376.snaptick.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskReminderDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertAll(reminders: List<TaskReminder>)

	@Query("DELETE FROM task_reminders WHERE uuid = :uuid")
	suspend fun deleteAllForTask(uuid: String)

	@Query("DELETE FROM task_reminders")
	suspend fun deleteAll()

	@Query("SELECT offsetMinutes FROM task_reminders WHERE uuid = :uuid ORDER BY offsetMinutes")
	suspend fun offsetsForTask(uuid: String): List<Int>

	@Query("SELECT offsetMinutes FROM task_reminders WHERE uuid = :uuid ORDER BY offsetMinutes")
	fun offsetsForTaskFlow(uuid: String): Flow<List<Int>>

	@Query("SELECT * FROM task_reminders")
	suspend fun getAllSnapshot(): List<TaskReminder>
}
