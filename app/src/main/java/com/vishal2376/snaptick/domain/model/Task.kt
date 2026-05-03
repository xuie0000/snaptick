package com.vishal2376.snaptick.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.vishal2376.snaptick.domain.converters.LocalDateConverter
import com.vishal2376.snaptick.domain.converters.LocalTimeConverter
import com.vishal2376.snaptick.util.Constants
import java.time.LocalDate
import java.time.LocalTime

@Entity(tableName = "task_table")
@TypeConverters(
	LocalTimeConverter::class,
	LocalDateConverter::class
)
data class Task(
	@PrimaryKey(autoGenerate = true) val id: Int = 0,
	val uuid: String,
	val title: String = "",
	val isCompleted: Boolean = false,
	val startTime: LocalTime = LocalTime.now(),
	val endTime: LocalTime = LocalTime.now(),
	val reminder: Boolean = false,
	val isRepeated: Boolean = false,
	val repeatWeekdays: String = "",
	val pomodoroTimer: Int = -1,
	val date: LocalDate = LocalDate.now(),
	val priority: Int = 0,
	val calendarEventId: Long? = null,
) {
	fun isAllDayTaskEnabled(): Boolean {
		return startTime == endTime
	}

	fun getRepeatWeekList(): List<Int> {
		return if (repeatWeekdays.isEmpty())
			emptyList()
		else
			repeatWeekdays.split(",")
				.map { it.toInt() }
	}

	fun shouldOccurOn(target: LocalDate): Boolean {
		if (!isRepeated) return date == target
		if (target < date) return false
		val weekdayIndex = target.dayOfWeek.value - 1
		return getRepeatWeekList().contains(weekdayIndex)
	}

	fun isValidPomodoroSession(timeLeft: Long): Boolean {
		return (getDuration() - timeLeft) >= Constants.MIN_VALID_POMODORO_SESSION * 60
	}

	fun getDuration(checkPastTask: Boolean = false): Long {
		val startSec = startTime.toSecondOfDay()
		val endSec = endTime.toSecondOfDay()
		val crossesMidnight = endSec < startSec
		val fullDuration =
			if (crossesMidnight) endSec + Constants.SECONDS_IN_DAY - startSec else endSec - startSec

		if (!checkPastTask) return fullDuration.coerceAtLeast(0).toLong()

		val nowSec = LocalTime.now().toSecondOfDay()
		return if (crossesMidnight) {
			when {
				nowSec >= startSec -> (endSec + Constants.SECONDS_IN_DAY - nowSec).toLong()
				nowSec <= endSec -> (endSec - nowSec).toLong()
				else -> fullDuration.toLong()
			}
		} else {
			when {
				nowSec > endSec -> 0L
				nowSec in (startSec + 1)..<endSec -> (endSec - nowSec).toLong()
				else -> fullDuration.coerceAtLeast(0).toLong()
			}
		}
	}
}
