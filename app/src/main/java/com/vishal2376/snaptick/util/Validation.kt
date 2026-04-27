package com.vishal2376.snaptick.util

import com.vishal2376.snaptick.domain.model.Task
import java.time.LocalDate
import java.time.LocalTime

fun checkValidTask(
	task: Task,
	totalTasksDuration: Long = 0,
	isTaskAllDay: Boolean = false,
	sleepTime: LocalTime = LocalTime.MAX
): Pair<Boolean, String> {
	if (task.title.trim().isEmpty()) {
		return Pair(false, "Title can't be empty")
	}

	if ((task.getDuration() < Constants.MIN_ALLOWED_DURATION * 60) && !isTaskAllDay) {
		return Pair(false, "Task should be at least ${Constants.MIN_ALLOWED_DURATION} minutes.")
	}

	if (task.date < LocalDate.now()) {
		return Pair(false, "Past dates are not allowed")
	}

	if (task.date > LocalDate.now()) {
		return Pair(true, "Future Task")
	}

	return Pair(true, "Valid Task")
}
