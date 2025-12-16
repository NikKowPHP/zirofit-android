package com.ziro.fit.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziro.fit.data.repository.CalendarRepository
import com.ziro.fit.model.CalendarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class CalendarUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val currentWeekOffset: Int = 0, // 0 = current week, -1 = previous week, +1 = next week
    val events: List<CalendarEvent> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val selectedEvent: CalendarEvent? = null, // Track selected event for bottom sheet
    val isCreatingSession: Boolean = false,
    val createSessionSuccess: Boolean = false,
    val createSessionError: String? = null
) {
    // Derived property: Filter events for the selected date on the UI side
    // This makes the UI snappy as switching days doesn't always need a network call
    val selectedDateEvents: List<CalendarEvent>
        get() = events.filter { 
            it.startTime.toLocalDate().isEqual(selectedDate) 
        }.sortedBy { it.startTime }
    
    // Get the start of the current week being displayed
    val currentWeekStart: LocalDate
        get() {
            val today = LocalDate.now()
            val startOfThisWeek = today.minusDays(today.dayOfWeek.value.toLong() - 1)
            return startOfThisWeek.plusWeeks(currentWeekOffset.toLong())
        }
}

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val repository: CalendarRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    private var fetchJob: Job? = null

    init {
        fetchEvents()
    }

    fun onDateSelected(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date) }
        // Optional: If date is outside current cache range, fetch more
        // For now, we fetch every time date changes or we could optimize
        // to only fetch if close to the edge of the buffer
        fetchEvents() 
    }
    
    fun onWeekChanged(weekOffset: Int) {
        _uiState.update { it.copy(currentWeekOffset = weekOffset) }
        // Optionally fetch events for the new week range
        fetchEvents()
    }
    
    fun navigateToNextWeek() {
        _uiState.update { it.copy(currentWeekOffset = it.currentWeekOffset + 1) }
        fetchEvents()
    }
    
    fun navigateToPreviousWeek() {
        _uiState.update { it.copy(currentWeekOffset = it.currentWeekOffset - 1) }
        fetchEvents()
    }

    fun onEventSelected(event: CalendarEvent) {
        _uiState.update { it.copy(selectedEvent = event) }
    }

    fun onEventDismissed() {
        _uiState.update { it.copy(selectedEvent = null) }
    }
    
    fun onStartSession(event: CalendarEvent) {
        // TODO: Navigate to live workout screen with event.id
        println("Starting session for ${event.title}")
    }

    fun onUpdateSession(event: CalendarEvent) {
        // TODO: Open edit dialog
        println("Updating session ${event.id}")
    }

    fun retry() {
        refresh()
    }

    fun refresh(isPullToRefresh: Boolean = false) {
        fetchJob?.cancel()

        fetchJob = viewModelScope.launch {
            if (isPullToRefresh) {
                _uiState.update { it.copy(isRefreshing = true, error = null) }
            } else {
                // Do NOT set isLoading = true if we already have events (prevents flickering)
                if (_uiState.value.events.isEmpty()) {
                    _uiState.update { it.copy(isLoading = true, error = null) }
                }
            }
            
            repository.getEvents(_uiState.value.selectedDate)
                .onSuccess { fetchedEvents ->
                    _uiState.update { it.copy(events = fetchedEvents, isLoading = false, isRefreshing = false) }
                }
                .onFailure { error ->
                    if (isActive) {
                        _uiState.update { it.copy(error = error.message, isLoading = false, isRefreshing = false) }
                    }
                }
        }
    }

    private fun fetchEvents() {
        refresh()
    }

    suspend fun createSession(request: com.ziro.fit.model.CreateSessionRequest): Result<String> {
        _uiState.update { it.copy(isCreatingSession = true, createSessionError = null, createSessionSuccess = false) }
        
        val result = repository.createSession(request)
        
        result.onSuccess { message ->
            _uiState.update { 
                it.copy(
                    isCreatingSession = false, 
                    createSessionSuccess = true,
                    createSessionError = null
                ) 
            }
            // Refresh calendar events after successful creation
            refresh()
        }.onFailure { error ->
            _uiState.update { 
                it.copy(
                    isCreatingSession = false, 
                    createSessionSuccess = false,
                    createSessionError = error.message ?: "Failed to create session"
                ) 
            }
        }
        
        return result
    }

    fun dismissCreateSuccess() {
        _uiState.update { it.copy(createSessionSuccess = false, createSessionError = null) }
    }
}
      