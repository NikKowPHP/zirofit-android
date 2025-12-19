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
    val bookingSuccess: Boolean = false,
    val isLinking: Boolean = false,
    val linkError: String? = null,
    val linkSuccess: Boolean = false,
    val isUnlinking: Boolean = false,
    val unlinkError: String? = null,
    val unlinkSuccess: Boolean = false,
    val linkedTrainerId: String? = null,
    val isCheckingOut: Boolean = false,
    val checkoutUrl: String? = null,
    val checkoutError: String? = null
)

data class TimeSlot(
    val startTime: String, // ISO-8601 datetime
    val endTime: String    // ISO-8601 datetime
)


@HiltViewModel
class TrainerPublicProfileViewModel @Inject constructor(
    private val trainerRepository: TrainerRepository,
    private val bookingsRepository: com.ziro.fit.data.repository.BookingsRepository,
    private val billingRepository: com.ziro.fit.data.repository.BillingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrainerPublicProfileUiState())
    val uiState: StateFlow<TrainerPublicProfileUiState> = _uiState.asStateFlow()

    fun loadTrainerProfile(trainerId: String) {
        checkLinkStatus()
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

    fun linkWithTrainer(username: String) {
        val currentProfileId = _uiState.value.profile?.id
        val previousLinkedId = _uiState.value.linkedTrainerId
        
        viewModelScope.launch {
            // Optimistic update
            _uiState.value = _uiState.value.copy(
                isLinking = true, 
                linkError = null, 
                linkSuccess = false,
                linkedTrainerId = currentProfileId
            )
            
            val result = trainerRepository.linkTrainer(username)
            result.onSuccess {
                _uiState.value = _uiState.value.copy(isLinking = false, linkSuccess = true)
                // Rehydrate to ensure we have the latest true state
                checkLinkStatus()
            }.onFailure { error ->
                // Revert optimistic update
                _uiState.value = _uiState.value.copy(
                    isLinking = false, 
                    linkError = error.message,
                    linkedTrainerId = previousLinkedId
                )
            }
        }
    }

    fun unlinkFromTrainer() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUnlinking = true, unlinkError = null, unlinkSuccess = false)
            val result = trainerRepository.unlinkTrainer()
            result.onSuccess {
                _uiState.value = _uiState.value.copy(isUnlinking = false, unlinkSuccess = true)
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(isUnlinking = false, unlinkError = error.message)
            }
        }
    }

    fun resetLinkState() {
        _uiState.value = _uiState.value.copy(
            linkSuccess = false,
            linkError = null,
            unlinkSuccess = false,
            unlinkError = null
        )
    }

    private fun checkLinkStatus() {
        viewModelScope.launch {
            trainerRepository.getLinkedTrainer()
                .onSuccess { trainer ->
                    _uiState.value = _uiState.value.copy(
                        linkedTrainerId = trainer?.id
                    )
                }
        }
    }

    fun purchasePackage(packageId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCheckingOut = true, checkoutError = null, checkoutUrl = null)
            
            billingRepository.createCheckoutSession(packageId)
                .onSuccess { url ->
                    _uiState.value = _uiState.value.copy(isCheckingOut = false, checkoutUrl = url)
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(isCheckingOut = false, checkoutError = e.message)
                }
        }
    }

    fun onCheckoutLaunched() {
        _uiState.value = _uiState.value.copy(checkoutUrl = null)
    }
}
