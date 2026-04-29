package com.vishal2376.snaptick.presentation.analytics_screen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vishal2376.snaptick.data.repositories.AnalyticsRepository
import com.vishal2376.snaptick.presentation.analytics_screen.state.AnalyticsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
	private val repository: AnalyticsRepository,
) : ViewModel() {

	private val _state = MutableStateFlow(AnalyticsState())
	val state = _state.asStateFlow()

	init {
		refresh(rangeDays = 30)
	}

	fun refresh(rangeDays: Int) {
		viewModelScope.launch {
			_state.update { it.copy(isLoading = true) }
			val snap = repository.snapshot(rangeDays)
			_state.update { it.copy(snapshot = snap, isLoading = false) }
		}
	}
}
