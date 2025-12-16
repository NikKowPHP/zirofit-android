package com.ziro.fit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziro.fit.data.repository.ClientRepository
import com.ziro.fit.model.Measurement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ClientMeasurementsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val measurements: List<Measurement> = emptyList()
)

@HiltViewModel
class ClientMeasurementsViewModel @Inject constructor(
    private val clientRepository: ClientRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClientMeasurementsUiState())
    val uiState: StateFlow<ClientMeasurementsUiState> = _uiState.asStateFlow()

    fun loadMeasurements(clientId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            clientRepository.getClientMeasurements(clientId)
                .onSuccess { measurements ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        measurements = measurements
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to load measurements"
                    )
                }
        }
    }
    fun createMeasurement(clientId: String, date: String, weight: Double?, bodyFat: Double?, notes: String?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = clientRepository.createMeasurement(clientId, date, weight, bodyFat, notes)
            result.onSuccess {
                loadMeasurements(clientId)
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to create measurement"
                )
            }
        }
    }

    fun deleteMeasurement(clientId: String, measurementId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = clientRepository.deleteMeasurement(clientId, measurementId)
            result.onSuccess {
                loadMeasurements(clientId)
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to delete measurement"
                )
            }
        }
    }
}
