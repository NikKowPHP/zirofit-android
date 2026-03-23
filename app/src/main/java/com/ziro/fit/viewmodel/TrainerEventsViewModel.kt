package com.ziro.fit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziro.fit.data.repository.ExploreRepository
import com.ziro.fit.model.ExploreEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class TrainerEventsUiState(
    val isLoading: Boolean = false,
    val events: List<ExploreEvent> = emptyList(),
    val error: String? = null,
    val showForm: Boolean = false,
    val editingEvent: ExploreEvent? = null,
    val formTitle: String = "",
    val formDescription: String = "",
    val formLocation: String = "",
    val formAddress: String = "",
    val formDate: LocalDate = LocalDate.now(),
    val formStartTime: LocalTime = LocalTime.of(9, 0),
    val formEndTime: LocalTime = LocalTime.of(10, 0),
    val formPrice: String = "",
    val formCapacity: String = "",
    val formCategoryId: String? = null,
    val isSaving: Boolean = false,
    val deleteConfirmEventId: String? = null,
    val saveSuccess: Boolean = false
)

@HiltViewModel
class TrainerEventsViewModel @Inject constructor(
    private val repository: ExploreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrainerEventsUiState())
    val uiState: StateFlow<TrainerEventsUiState> = _uiState.asStateFlow()

    init {
        loadEvents()
    }

    fun loadEvents() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            repository.getTrainerEvents()
                .onSuccess { events ->
                    _uiState.update { it.copy(isLoading = false, events = events, error = null) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    fun showCreateForm() {
        _uiState.update {
            it.copy(
                showForm = true,
                editingEvent = null,
                formTitle = "",
                formDescription = "",
                formLocation = "",
                formAddress = "",
                formDate = LocalDate.now(),
                formStartTime = LocalTime.of(9, 0),
                formEndTime = LocalTime.of(10, 0),
                formPrice = "",
                formCapacity = "",
                formCategoryId = null
            )
        }
    }

    fun showEditForm(event: ExploreEvent) {
        _uiState.update {
            it.copy(
                showForm = true,
                editingEvent = event,
                formTitle = event.title,
                formDescription = event.description ?: "",
                formLocation = event.locationName,
                formAddress = event.address ?: "",
                formDate = parseDate(event.startTime),
                formStartTime = parseTime(event.startTime),
                formEndTime = event.endTime?.let { parseTime(it) } ?: LocalTime.of(10, 0),
                formPrice = event.price?.toString() ?: "",
                formCapacity = event.capacity?.toString() ?: "",
                formCategoryId = event.categoryId
            )
        }
    }

    fun hideForm() {
        _uiState.update { it.copy(showForm = false, editingEvent = null) }
    }

    fun setFormTitle(title: String) {
        _uiState.update { it.copy(formTitle = title) }
    }

    fun setFormDescription(description: String) {
        _uiState.update { it.copy(formDescription = description) }
    }

    fun setFormLocation(location: String) {
        _uiState.update { it.copy(formLocation = location) }
    }

    fun setFormAddress(address: String) {
        _uiState.update { it.copy(formAddress = address) }
    }

    fun setFormDate(date: LocalDate) {
        _uiState.update { it.copy(formDate = date) }
    }

    fun setFormStartTime(time: LocalTime) {
        _uiState.update { it.copy(formStartTime = time) }
    }

    fun setFormEndTime(time: LocalTime) {
        _uiState.update { it.copy(formEndTime = time) }
    }

    fun setFormPrice(price: String) {
        _uiState.update { it.copy(formPrice = price) }
    }

    fun setFormCapacity(capacity: String) {
        _uiState.update { it.copy(formCapacity = capacity) }
    }

    fun setFormCategoryId(categoryId: String?) {
        _uiState.update { it.copy(formCategoryId = categoryId) }
    }

    fun saveEvent() {
        val state = _uiState.value

        if (state.formTitle.isBlank()) {
            _uiState.update { it.copy(error = "Title is required") }
            return
        }
        if (state.formLocation.isBlank()) {
            _uiState.update { it.copy(error = "Location is required") }
            return
        }
        if (state.formEndTime <= state.formStartTime) {
            _uiState.update { it.copy(error = "End time must be after start time") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }

            val formatter = DateTimeFormatter.ISO_INSTANT
            val startDateTime = state.formDate.atTime(state.formStartTime).atZone(ZoneId.of("UTC")).toInstant()
            val endDateTime = state.formDate.atTime(state.formEndTime).atZone(ZoneId.of("UTC")).toInstant()

            val event = ExploreEvent(
                id = state.editingEvent?.id ?: "",
                title = state.formTitle,
                description = state.formDescription.ifBlank { null },
                startTime = formatter.format(startDateTime),
                endTime = formatter.format(endDateTime),
                price = state.formPrice.toDoubleOrNull(),
                currency = if (state.formPrice.isNotBlank()) "USD" else null,
                locationName = state.formLocation,
                address = state.formAddress.ifBlank { null },
                latitude = state.editingEvent?.latitude,
                longitude = state.editingEvent?.longitude,
                imageUrl = state.editingEvent?.imageUrl,
                categoryId = state.formCategoryId,
                cityId = state.editingEvent?.cityId,
                priceDisplay = if (state.formPrice.isBlank()) "Free" else "$${state.formPrice}",
                hostName = state.editingEvent?.hostName,
                hostId = state.editingEvent?.hostId,
                trainerName = state.editingEvent?.trainerName,
                trainerId = state.editingEvent?.trainerId,
                enrolledCount = state.editingEvent?.enrolledCount,
                capacity = state.formCapacity.toIntOrNull(),
                isBooked = state.editingEvent?.isBooked,
                isNearCapacity = state.editingEvent?.isNearCapacity,
                trainer = state.editingEvent?.trainer
            )

            val result = if (state.editingEvent != null) {
                repository.updateEvent(state.editingEvent.id, event)
            } else {
                repository.createEvent(event)
            }

            result
                .onSuccess {
                    _uiState.update { it.copy(isSaving = false, showForm = false, editingEvent = null, saveSuccess = true) }
                    loadEvents()
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isSaving = false, error = error.message) }
                }
        }
    }

    fun showDeleteConfirmation(eventId: String) {
        _uiState.update { it.copy(deleteConfirmEventId = eventId) }
    }

    fun hideDeleteConfirmation() {
        _uiState.update { it.copy(deleteConfirmEventId = null) }
    }

    fun deleteEvent(eventId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, deleteConfirmEventId = null) }

            repository.deleteEvent(eventId)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    loadEvents()
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun resetSaveSuccess() {
        _uiState.update { it.copy(saveSuccess = false) }
    }

    private fun parseDate(isoDateTime: String): LocalDate {
        return try {
            java.time.ZonedDateTime.parse(isoDateTime).toLocalDate()
        } catch (e: Exception) {
            LocalDate.now()
        }
    }

    private fun parseTime(isoDateTime: String): LocalTime {
        return try {
            java.time.ZonedDateTime.parse(isoDateTime).toLocalTime()
        } catch (e: Exception) {
            LocalTime.of(9, 0)
        }
    }
}
