package com.ziro.fit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziro.fit.data.repository.TrainerRepository
import com.ziro.fit.model.PublicTrainerProfileResponse
import com.ziro.fit.model.TrainerScheduleResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TrainerPublicProfileUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val profile: PublicTrainerProfileResponse? = null,
    val schedule: TrainerScheduleResponse? = null,
    val isLoadingSchedule: Boolean = false,
    val scheduleError: String? = null,
    val selectedDate: String? = null, // Selected date in YYYY-MM-DD format
    val selectedTimeSlot: TimeSlot? = null,
    val isCreatingBooking: Boolean = false,
    val bookingError: String? = null,
    val bookingSuccess: Boolean = false
)

data class TimeSlot(
    val startTime: String, // ISO-8601 datetime
    val endTime: String    // ISO-8601 datetime
)


@HiltViewModel
class TrainerPublicProfileViewModel @Inject constructor(
    private val trainerRepository: TrainerRepository,
    private val bookingsRepository: com.ziro.fit.data.repository.BookingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrainerPublicProfileUiState())
    val uiState: StateFlow<TrainerPublicProfileUiState> = _uiState.asStateFlow()

    fun loadTrainerProfile(trainerId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = trainerRepository.getPublicTrainerProfile(trainerId)
            
            result.onSuccess { profile ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    profile = profile
                )
                // Auto-load schedule if username is available
                profile.username?.let { loadTrainerSchedule(it) }
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = error.message ?: "Failed to load trainer profile"
                )
            }
        }
    }

    fun loadTrainerSchedule(username: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingSchedule = true, scheduleError = null)
            val result = trainerRepository.getTrainerSchedule(username)
            
            result.onSuccess { schedule ->
                _uiState.value = _uiState.value.copy(
                    isLoadingSchedule = false,
                    schedule = schedule
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoadingSchedule = false,
                    scheduleError = error.message ?: "Failed to load schedule"
                )
            }
        }
    }

    fun selectDate(date: String) {
        _uiState.value = _uiState.value.copy(selectedDate = date, selectedTimeSlot = null)
    }

    fun selectTimeSlot(timeSlot: TimeSlot) {
        _uiState.value = _uiState.value.copy(selectedTimeSlot = timeSlot)
    }

    fun createBooking(
        trainerId: String,
        notes: String?
    ) {
        val timeSlot = _uiState.value.selectedTimeSlot ?: return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCreatingBooking = true, bookingError = null)
            
            val result = bookingsRepository.createBooking(
                trainerId = trainerId,
                startTime = timeSlot.startTime,
                endTime = timeSlot.endTime,
                clientName = null, // Will be filled from logged-in user
                clientEmail = null, // Will be filled from logged-in user
                clientNotes = notes
            )
            
            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isCreatingBooking = false,
                    bookingSuccess = true,
                    selectedTimeSlot = null
                )
                // Reload schedule to reflect new booking
                _uiState.value.profile?.username?.let { loadTrainerSchedule(it) }
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isCreatingBooking = false,
                    bookingError = error.message ?: "Failed to create booking"
                )
            }
        }
    }

    fun resetBookingState() {
        _uiState.value = _uiState.value.copy(
            bookingSuccess = false,
            bookingError = null,
            selectedTimeSlot = null
        )
    }
}
