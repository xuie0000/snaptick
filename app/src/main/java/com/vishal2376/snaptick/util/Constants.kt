package com.vishal2376.snaptick.util

object Constants {

	const val EMAIL = "vishalsingh2376@gmail.com"
	const val GITHUB = "https://github.com/vishal2376"
	const val GITHUB_LATEST_RELEASE_API =
		"https://api.github.com/repos/vishal2376/snaptick/releases/latest"
	const val UPDATE_CHECK_THROTTLE_MILLIS = 24L * 60 * 60 * 1000  // 24 hours
	const val LINKEDIN = "https://www.linkedin.com/in/vishal2376"
	const val TWITTER = "https://twitter.com/vishal2376"
	const val INSTAGRAM = "https://www.instagram.com/vishal_2376/"

	const val LIST_ANIMATION_DELAY = 100
	const val MIN_ALLOWED_DURATION = 5L // 5 min
	const val MIN_VALID_POMODORO_SESSION = 1L // 1 min
	const val SECONDS_IN_DAY = 86400

	// DATA STORE KEYS
	const val SETTINGS_KEY = "settings_key"

	// WORK MANAGER DATA KEYS
	const val TASK_ID = "task_id"
	const val TASK_UUID = "task_uuid"
	const val TASK_TITLE = "task_title"
	const val TASK_TIME = "task_time"
}