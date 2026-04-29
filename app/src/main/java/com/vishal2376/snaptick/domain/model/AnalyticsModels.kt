package com.vishal2376.snaptick.domain.model

import java.time.LocalDate

data class DailyStats(
	val date: LocalDate,
	val completed: Int,
	val total: Int,
	val durationSeconds: Long,
) {
	val ratio: Float get() = if (total == 0) 0f else completed.toFloat() / total.toFloat()
}

data class StreakInfo(
	val current: Int,
	val longest: Int,
)

data class AnalyticsSnapshot(
	val rangeStart: LocalDate,
	val rangeEnd: LocalDate,
	val daily: Map<LocalDate, DailyStats>,
	val streak: StreakInfo,
	val pomodoroSessionsCompleted: Int,
	val averageTaskDurationSec: Long,
	val totalTimeInvestedSec: Long,
)
