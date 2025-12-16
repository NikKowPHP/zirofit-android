package com.ziro.fit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziro.fit.data.repository.BookingsRepository
import com.ziro.fit.model.Booking
import com.ziro.fit.model.BookingStatus
import com.ziro.fit.model.UpdateBookingRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BookingsUiState(
    val isLoading: Boolean = false,
    val bookings: List<Booking> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class BookingsViewModel @Inject constructor(
    private val bookingsRepository: BookingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookingsUiState())
    val uiState: StateFlow<BookingsUiState> = _uiState.asStateFlow()

    init {
        loadBookings()
    }

    fun loadBookings() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = bookingsRepository.getBookings()
            result.fold(
                onSuccess = { bookings ->
                    _uiState.update { it.copy(isLoading = false, bookings = bookings) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
            )
        }
    }

    fun createBooking(
        trainerId: String,
        startTime: String,
        endTime: String,
        clientName: String?,
        clientEmail: String?,
        clientNotes: String?,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = bookingsRepository.createBooking(
                trainerId, startTime, endTime, clientName, clientEmail, clientNotes
            )
            result.fold(
                onSuccess = { newBooking ->
                    // Add to list locally or reload
                    val currentList = _uiState.value.bookings.toMutableList()
                    currentList.add(newBooking)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            bookings = currentList,
                            successMessage = "Booking created successfully"
                        )
                    }
                    onSuccess()
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
            )
        }
    }

    fun updateBooking(
        bookingId: String,
        request: UpdateBookingRequest,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = bookingsRepository.updateBooking(bookingId, request)
            result.fold(
                onSuccess = { updatedBooking ->
                    val currentList = _uiState.value.bookings.toMutableList()
                    val index = currentList.indexOfFirst { it.id == bookingId }
                    if (index != -1) {
                        currentList[index] = updatedBooking
                    }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            bookings = currentList,
                            successMessage = "Booking updated successfully"
                        )
                    }
                    onSuccess()
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
            )
        }
    }

    fun deleteBooking(bookingId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = bookingsRepository.deleteBooking(bookingId)
            result.fold(
                onSuccess = {
                    val currentList = _uiState.value.bookings.filter { it.id != bookingId }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            bookings = currentList,
                            successMessage = "Booking deleted"
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
            )
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
    
    fun clearSuccessMessage() {
        _uiState.update { it.copy(successMessage = null) }
    }
}
