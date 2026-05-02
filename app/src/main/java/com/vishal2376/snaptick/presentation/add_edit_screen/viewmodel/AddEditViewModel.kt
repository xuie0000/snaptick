package com.vishal2376.snaptick.presentation.add_edit_screen.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vishal2376.snaptick.data.repositories.TaskRepository
import com.vishal2376.snaptick.util.Constants
import com.vishal2376.snaptick.presentation.add_edit_screen.action.AddEditAction
import com.vishal2376.snaptick.presentation.add_edit_screen.events.AddEditEvent
import com.vishal2376.snaptick.presentation.add_edit_screen.state.AddEditState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditViewModel @Inject constructor(
	private val repository: TaskRepository,
	savedStateHandle: SavedStateHandle,
) : ViewModel() {

	private val _state = MutableStateFlow(AddEditState())
	val state = _state.asStateFlow()

	private val _events = MutableSharedFlow<AddEditEvent>(extraBufferCapacity = 1)
	val events = _events.asSharedFlow()

	init {
		val taskId: Int = savedStateHandle.get<Int>("id") ?: -1
		if (taskId > 0) {
			_state.update { it.copy(isLoaded = false) }
			viewModelScope.launch {
				repository.getTaskById(taskId)?.let { task ->
					val offsets = repository.getReminderOffsets(task.uuid)
					_state.value = AddEditState.fromTask(task).copy(reminderOffsets = offsets)
				}
			}
		}
	}

	fun onAction(action: AddEditAction) {
		when (action) {
			is AddEditAction.UpdateTitle -> _state.update { it.copy(title = action.title) }
			is AddEditAction.UpdateStartTime -> _state.update {
				val oldStartSec = it.startTime.toSecondOfDay()
				val oldEndSec = it.endTime.toSecondOfDay()
				val gapSec = if (oldEndSec < oldStartSec) {
					oldEndSec + Constants.SECONDS_IN_DAY - oldStartSec
				} else {
					oldEndSec - oldStartSec
				}
				val gapMin = (gapSec / 60L).coerceAtLeast(0L)
				it.copy(
					startTime = action.time,
					endTime = action.time.plusMinutes(gapMin),
					duration = gapMin,
					timeUpdateTick = it.timeUpdateTick + 1
				)
			}
			is AddEditAction.UpdateEndTime -> _state.update {
				val startSec = it.startTime.toSecondOfDay()
				val endSec = action.time.toSecondOfDay()
				val gapSec = if (endSec < startSec) {
					endSec + Constants.SECONDS_IN_DAY - startSec
				} else {
					endSec - startSec
				}
				val gapMin = (gapSec / 60L).coerceAtLeast(0L)
				it.copy(endTime = action.time, duration = gapMin)
			}
			is AddEditAction.UpdateDate -> _state.update { it.copy(date = action.date) }
			is AddEditAction.UpdateReminder -> _state.update {
				val nextOffsets = if (action.enabled && it.reminderOffsets.isEmpty())
					listOf(0, 5)
				else it.reminderOffsets
				it.copy(
					reminder = action.enabled,
					reminderOffsets = if (action.enabled) nextOffsets else emptyList(),
				)
			}
			is AddEditAction.RemoveReminderOffset -> _state.update {
				val next = it.reminderOffsets - action.offsetMinutes
				it.copy(reminderOffsets = next, reminder = next.isNotEmpty())
			}
			is AddEditAction.AddReminderOffset -> _state.update {
				val current = it.reminderOffsets
				if (action.offsetMinutes in current || current.size >= 4) return@update it
				it.copy(
					reminderOffsets = (current + action.offsetMinutes).sorted(),
					reminder = true,
				)
			}
			is AddEditAction.UpdateAllDay -> _state.update {
				it.copy(isAllDay = action.enabled, endTime = if (action.enabled) it.startTime else it.endTime)
			}
			is AddEditAction.UpdateRepeated -> _state.update { it.copy(isRepeated = action.enabled) }
			is AddEditAction.UpdateRepeatWeekDays -> _state.update { it.copy(repeatWeekdays = action.weekDays) }
			is AddEditAction.UpdatePriority -> _state.update { it.copy(priority = action.priority) }
			is AddEditAction.UpdateDurationMinutes -> _state.update {
				it.copy(
					duration = action.minutes,
					endTime = it.startTime.plusMinutes(action.minutes),
					timeUpdateTick = it.timeUpdateTick + 1,
					pomodoroTimer = -1
				)
			}
			is AddEditAction.SaveTask -> saveTask()
			is AddEditAction.UpdateTask -> updateTask()
			is AddEditAction.DeleteTask -> deleteTask()
		}
	}

	private fun saveTask() {
		viewModelScope.launch {
			val s = _state.value
			val task = s.toTask()
			val offsets = if (s.reminder) s.reminderOffsets.ifEmpty { listOf(0) } else emptyList()
			repository.insertTask(task, reminderOffsets = offsets)
			_events.emit(AddEditEvent.TaskSaved)
		}
	}

	private fun updateTask() {
		viewModelScope.launch {
			val s = _state.value
			val task = s.toTask()
			val offsets = if (s.reminder) s.reminderOffsets.ifEmpty { listOf(0) } else emptyList()
			repository.updateTask(task, reminderOffsets = offsets)
			_events.emit(AddEditEvent.TaskUpdated)
		}
	}

	private fun deleteTask() {
		viewModelScope.launch {
			val task = _state.value.toTask()
			repository.deleteTask(task)
			_events.emit(AddEditEvent.TaskDeleted)
		}
	}
}
