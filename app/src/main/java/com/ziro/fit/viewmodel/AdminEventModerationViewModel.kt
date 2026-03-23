package com.ziro.fit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziro.fit.data.remote.ZiroApi
import com.ziro.fit.model.EventModerationActionRequest
import com.ziro.fit.model.EventModerationDetailResponse
import com.ziro.fit.model.PendingEvent
import com.ziro.fit.model.EventTrainerSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AdminEventsUiState {
    object Loading : AdminEventsUiState()
    data class Success(val events: List<PendingEvent>) : AdminEventsUiState()
    data class Error(val message: String) : AdminEventsUiState()
}

@HiltViewModel
class AdminEventModerationViewModel @Inject constructor(
    private val api: ZiroApi
) : ViewModel() {

    private val _uiState = MutableStateFlow<AdminEventsUiState>(AdminEventsUiState.Loading)
    val uiState: StateFlow<AdminEventsUiState> = _uiState.asStateFlow()

    private val _events = MutableStateFlow<List<PendingEvent>>(emptyList())
    val events: StateFlow<List<PendingEvent>> = _events.asStateFlow()

    private val _selectedEvent = MutableStateFlow<PendingEvent?>(null)
    val selectedEvent: StateFlow<PendingEvent?> = _selectedEvent.asStateFlow()

    private val _isActionLoading = MutableStateFlow(false)
    val isActionLoading: StateFlow<Boolean> = _isActionLoading.asStateFlow()

    private val _actionError = MutableStateFlow<String?>(null)
    val actionError: StateFlow<String?> = _actionError.asStateFlow()

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess.asStateFlow()

    init {
        loadPendingEvents()
    }

    fun loadPendingEvents() {
        viewModelScope.launch {
            _uiState.update { AdminEventsUiState.Loading }
            try {
                val response = api.getPendingEvents()
                if (response.success == true) {
                    val events = response.data?.events ?: emptyList()
                    _events.update { events }
                    _uiState.update { AdminEventsUiState.Success(events) }
                } else {
                    _uiState.update { AdminEventsUiState.Error(response.message ?: "Failed to load events") }
                }
            } catch (e: Exception) {
                _uiState.update { AdminEventsUiState.Error(e.message ?: "An error occurred") }
            }
        }
    }

    fun selectEvent(event: PendingEvent) {
        _selectedEvent.update { event }
    }

    fun clearSelection() {
        _selectedEvent.update { null }
    }

    fun approveEvent(eventId: String) {
        viewModelScope.launch {
            _isActionLoading.update { true }
            _actionError.update { null }
            try {
                val response = api.moderateEvent(eventId, EventModerationActionRequest("approve"))
                if (response.success == true) {
                    _events.update { events -> events.filter { it.id != eventId } }
                    _uiState.update {
                        AdminEventsUiState.Success(_events.value)
                    }
                    _selectedEvent.update { null }
                    _isSuccess.update { true }
                } else {
                    _actionError.update { response.message ?: "Failed to approve event" }
                }
            } catch (e: Exception) {
                _actionError.update { e.message ?: "An error occurred" }
            } finally {
                _isActionLoading.update { false }
            }
        }
    }

    fun rejectEvent(eventId: String, reason: String) {
        viewModelScope.launch {
            _isActionLoading.update { true }
            _actionError.update { null }
            try {
                val response = api.moderateEvent(
                    eventId,
                    EventModerationActionRequest("reject", reason.ifBlank { null })
                )
                if (response.success == true) {
                    _events.update { events -> events.filter { it.id != eventId } }
                    _uiState.update {
                        AdminEventsUiState.Success(_events.value)
                    }
                    _selectedEvent.update { null }
                    _isSuccess.update { true }
                } else {
                    _actionError.update { response.message ?: "Failed to reject event" }
                }
            } catch (e: Exception) {
                _actionError.update { e.message ?: "An error occurred" }
            } finally {
                _isActionLoading.update { false }
            }
        }
    }

    fun clearActionError() {
        _actionError.update { null }
    }

    fun clearSuccess() {
        _isSuccess.update { false }
    }
}

val AdminEventsUiState.isLoading: Boolean
    get() = this is AdminEventsUiState.Loading

val AdminEventsUiState.error: String?
    get() = (this as? AdminEventsUiState.Error)?.message

val AdminEventsUiState.isSuccess: Boolean
    get() = this is AdminEventsUiState.Success
