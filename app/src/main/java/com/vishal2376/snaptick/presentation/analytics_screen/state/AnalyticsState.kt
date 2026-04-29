package com.vishal2376.snaptick.presentation.analytics_screen.state

import com.vishal2376.snaptick.domain.model.AnalyticsSnapshot

data class AnalyticsState(
	val snapshot: AnalyticsSnapshot? = null,
	val isLoading: Boolean = true,
)
