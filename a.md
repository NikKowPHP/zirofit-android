<response>
  <reasoning>
    I will address the timezone mismatch and race condition issues identified.
    1.  **Timezone Mismatch:** In `CalendarEvent.kt`, I will update the `startTime` and `endTime` helpers to parse the UTC string using `Instant` and convert it to the system default `ZoneId`. This ensures the time is displayed correctly in the user's local time.
    2.  **Race Condition:** In `CalendarViewModel.kt`, I will implement job cancellation logic. I'll add a `fetchJob` variable to track the active network request and cancel it before starting a new one in the `refresh` method. I will also optimize the loading state to prevent flickering when data is already present.
    3.  **Tasks:** I will add an entry to `TASKS.md` for this fix.
  </reasoning>
  <modifications>
    <file path="app/src/main/java/com/ziro/fit/model/CalendarEvent.kt">
      <![CDATA[
package com.ziro.fit.model

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

data class CalendarEvent(
    val id: String,
    val title: String,
    val start: String, // Keep as String for GSON/Serialization
    val end: String,
    val type: EventType,
    val clientName: String?,
    val notes: String?
) {
    // Helper for UI logic
    val startTime: LocalDateTime get() = try {
        Instant.parse(start)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
    } catch (e: Exception) {
        // Fallback for non-standard formats
        LocalDateTime.parse(start.removeSuffix("Z"))
    }

    val endTime: LocalDateTime get() = try {
        Instant.parse(end)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
    } catch (e: Exception) {
        LocalDateTime.parse(end.removeSuffix("Z"))
    }
}

data class CalendarResponse(
    val events: List<CalendarEvent>
)

enum class EventType {
    booking,
    session_planned,
    session_completed,
    session_in_progress
}
      ]]>
    </file>
    <file path="app/src/main/java/com/ziro/fit/ui/calendar/CalendarViewModel.kt">
      <![CDATA[
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
    val selectedEvent: CalendarEvent? = null // New: Track selected event for bottom sheet
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
}
      ]]>
    </file>
    <file path="TASKS.md">
      <![CDATA[
- [x] Implement event detail bottom sheet
- [x] Implement Live Workout Feature (API, Repository, ViewModel, UI, Navigation)
- [x] Fix mismatch between Android Live Workout implementation and Server API
- [x] Implement robust merging of Workout Template and Logs for Live Workout
- [x] Fix calendar timezone parsing and network race conditions
      ]]>
    </file>
  </modifications>
</response>