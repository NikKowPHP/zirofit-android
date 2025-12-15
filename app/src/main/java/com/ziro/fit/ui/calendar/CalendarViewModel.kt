package com.ziro.fit.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziro.fit.data.repository.CalendarRepository
import com.ziro.fit.model.CalendarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class CalendarUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val events: List<CalendarEvent> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) {
    // Derived property: Filter events for the selected date on the UI side
    // This makes the UI snappy as switching days doesn't always need a network call
    val selectedDateEvents: List<CalendarEvent>
        get() = events.filter { 
            it.startTime.toLocalDate().isEqual(selectedDate) 
        }.sortedBy { it.startTime }
}

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val repository: CalendarRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

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

    fun retry() {
        fetchEvents()
    }

    private fun fetchEvents() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            repository.getEvents(_uiState.value.selectedDate)
                .onSuccess { fetchedEvents ->
                    _uiState.update { it.copy(events = fetchedEvents, isLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(error = error.message, isLoading = false) }
                }
        }
    }
}
      