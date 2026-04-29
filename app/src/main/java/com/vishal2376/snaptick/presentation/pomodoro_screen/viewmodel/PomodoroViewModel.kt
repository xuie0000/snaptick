package com.vishal2376.snaptick.presentation.pomodoro_screen.viewmodel

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vishal2376.snaptick.presentation.pomodoro_screen.action.PomodoroAction
import com.vishal2376.snaptick.presentation.pomodoro_screen.events.PomodoroEvent
import com.vishal2376.snaptick.presentation.pomodoro_screen.state.PomodoroState
import com.vishal2376.snaptick.service.PomodoroBinder
import com.vishal2376.snaptick.service.PomodoroService
import com.vishal2376.snaptick.service.ServiceTimerState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Thin wrapper around [PomodoroService]. The service is the source of truth
 * for timer state; this VM binds to it on init, mirrors its StateFlow into
 * its own [state], and forwards user actions back into the service. The
 * service also drives the foreground notification, so when this VM is gone
 * (user navigated away), the timer keeps ticking and the notification
 * remains the visible UI.
 */
@HiltViewModel
class PomodoroViewModel @Inject constructor(
	@ApplicationContext private val context: Context,
	savedStateHandle: SavedStateHandle,
) : ViewModel() {

	private val _state = MutableStateFlow(PomodoroState())
	val state = _state.asStateFlow()

	private val _events = MutableSharedFlow<PomodoroEvent>(extraBufferCapacity = 1)
	val events = _events.asSharedFlow()

	private var boundService: PomodoroService? = null
	private var observerJob: Job? = null
	private val taskId: Int = savedStateHandle.get<Int>("id") ?: -1

	private val connection = object : ServiceConnection {
		override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
			val pb = binder as? PomodoroBinder ?: return
			val svc = pb.service
			boundService = svc
			if (taskId > 0 && (svc.state.value.taskId != taskId || svc.state.value.timeLeft <= 0)) {
				svc.startForTask(taskId)
				viewModelScope.launch { _events.emit(PomodoroEvent.ResumingPreviousSession) }
			}
			observerJob?.cancel()
			observerJob = viewModelScope.launch { svc.state.collect { sync(it) } }
		}

		override fun onServiceDisconnected(name: ComponentName?) {
			boundService = null
		}
	}

	init {
		if (taskId > 0) {
			PomodoroService.startForTask(context, taskId)
			val intent = Intent(context, PomodoroService::class.java)
			context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
		}
	}

	fun onAction(action: PomodoroAction) {
		val svc = boundService
		when (action) {
			is PomodoroAction.TogglePause -> svc?.togglePause()
			is PomodoroAction.Reset -> svc?.reset()
			is PomodoroAction.MarkCompleted -> {
				svc?.markCompleted()
				viewModelScope.launch { _events.emit(PomodoroEvent.TaskMarkedCompleted) }
			}
		}
	}

	private fun sync(svcState: ServiceTimerState) {
		_state.value = PomodoroState(
			taskId = svcState.taskId,
			taskTitle = svcState.taskTitle,
			totalTime = svcState.totalTime,
			timeLeft = svcState.timeLeft,
			isPaused = svcState.isPaused,
			isReset = false,
			isCompleted = svcState.isCompleted,
		)
		if (svcState.isCompleted && svcState.totalTime > 0) {
			viewModelScope.launch { _events.emit(PomodoroEvent.TimerCompleted) }
		}
	}

	override fun onCleared() {
		super.onCleared()
		observerJob?.cancel()
		runCatching { context.unbindService(connection) }
	}
}
