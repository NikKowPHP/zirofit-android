package com.ziro.fit.ui.calendar

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziro.fit.data.repository.CalendarRepository
import com.ziro.fit.data.repository.ClientRepository
import com.ziro.fit.model.Client
import com.ziro.fit.model.CreateSessionRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class CreateSessionUiState(
    val clients: List<Client> = emptyList(),
    val selectedClient: Client? = null,
    val selectedDate: LocalDate = LocalDate.now(),
    val startTime: LocalTime = LocalTime.of(9, 0),
    val endTime: LocalTime = LocalTime.of(10, 0),
    val notes: String = "",
    val isRecurring: Boolean = false,
    val repeatWeeks: Int = 4,
    val selectedDays: Set<Int> = setOf(), // 1=Monday, 7=Sunday
    val isLoading: Boolean = false,
    val isLoadingClients: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

@HiltViewModel
class CreateSessionViewModel @Inject constructor(
    private val calendarRepository: CalendarRepository,
    private val clientRepository: ClientRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateSessionUiState())
    val uiState: StateFlow<CreateSessionUiState> = _uiState.asStateFlow()

    init {
        loadClients()
    }

    fun setInitialDate(date: LocalDate) {
        _uiState.value = _uiState.value.copy(selectedDate = date)
    }

    private fun loadClients() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingClients = true)
            clientRepository.getClients()
                .onSuccess { clients ->
                    _uiState.value = _uiState.value.copy(
                        clients = clients,
                        isLoadingClients = false
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        error = e.message ?: "Failed to load clients",
                        isLoadingClients = false
                    )
                }
        }
    }

    fun selectClient(client: Client) {
        _uiState.value = _uiState.value.copy(selectedClient = client, error = null)
    }

    fun setDate(date: LocalDate) {
        _uiState.value = _uiState.value.copy(selectedDate = date)
    }

    fun setStartTime(time: LocalTime) {
        _uiState.value = _uiState.value.copy(startTime = time)
    }

    fun setEndTime(time: LocalTime) {
        _uiState.value = _uiState.value.copy(endTime = time)
    }

    fun setNotes(notes: String) {
        _uiState.value = _uiState.value.copy(notes = notes)
    }

    fun toggleRecurring(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(isRecurring = enabled)
    }

    fun setRepeatWeeks(weeks: Int) {
        _uiState.value = _uiState.value.copy(repeatWeeks = weeks)
    }

    fun toggleDay(dayOfWeek: Int) {
        val currentDays = _uiState.value.selectedDays.toMutableSet()
        if (currentDays.contains(dayOfWeek)) {
            currentDays.remove(dayOfWeek)
        } else {
            currentDays.add(dayOfWeek)
        }
        _uiState.value = _uiState.value.copy(selectedDays = currentDays)
    }

    fun createSession() {
        val state = _uiState.value
        
        // Validation
        if (state.selectedClient == null) {
            _uiState.value = state.copy(error = "Please select a client")
            return
        }
        
        if (state.endTime <= state.startTime) {
            _uiState.value = state.copy(error = "End time must be after start time")
            return
        }

        if (state.isRecurring && state.selectedDays.isEmpty()) {
            _uiState.value = state.copy(error = "Please select at least one day for recurring sessions")
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, error = null)

            // Combine date and time to create ISO 8601 string
            val startDateTime = state.selectedDate.atTime(state.startTime)
            val endDateTime = state.selectedDate.atTime(state.endTime)
            val formatter = DateTimeFormatter.ISO_DATE_TIME

            val startTimeStr = startDateTime.atZone(ZoneId.systemDefault()).format(formatter)
            val endTimeStr = endDateTime.atZone(ZoneId.systemDefault()).format(formatter)

            val request = CreateSessionRequest(
                clientId = state.selectedClient.id,
                startTime = startTimeStr,
                endTime = endTimeStr,
                notes = if (state.notes.isNotBlank()) state.notes else null,
                templateId = null, // Can be extended later
                repeats = state.isRecurring,
                repeatWeeks = if (state.isRecurring) state.repeatWeeks else null,
                repeatDays = if (state.isRecurring && state.selectedDays.isNotEmpty()) {
                    state.selectedDays.sorted().joinToString(",")
                } else null
            )

            calendarRepository.createSession(request)
                .onSuccess {
                    _uiState.value = state.copy(
                        isLoading = false,
                        success = true,
                        error = null
                    )
                }
                .onFailure { e ->
                    _uiState.value = state.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to create session"
                    )
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun resetSuccess() {
        _uiState.value = _uiState.value.copy(success = false)
    }
}
