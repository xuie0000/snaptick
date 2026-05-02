package com.vishal2376.snaptick.presentation.pomodoro_screen.state

data class PomodoroState(
	val taskId: Int = -1,
	val taskTitle: String = "",
	val totalTime: Long = 0L,
	val timeLeft: Long = 0L,
	val isPaused: Boolean = false,
	val isReset: Boolean = false,
	val isCompleted: Boolean = false,
	/** When non-null, a confirm dialog asks the user if it's OK to stop the
	 *  currently-running pomodoro and start this one instead. The pair is
	 *  (current running task title, requested new task id). */
	val pendingReplace: Pair<String, Int>? = null,
)
