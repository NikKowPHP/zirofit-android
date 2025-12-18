package com.ziro.fit.ui.checkins

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziro.fit.data.model.CheckInConfig
import com.ziro.fit.data.model.CheckInContext
import com.ziro.fit.data.model.CheckInHistoryItem
import com.ziro.fit.data.model.CheckInSubmissionRequest
import com.ziro.fit.data.repository.CheckInRepository
import com.ziro.fit.data.repository.TrainerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ClientCheckInUiState(
    val isLoading: Boolean = false,
    val config: CheckInConfig? = null,
    val history: List<CheckInHistoryItem> = emptyList(),
    val selectedCheckIn: com.ziro.fit.data.model.CheckInDetailWrapper? = null,
    val isSubmitting: Boolean = false,
    val submissionSuccess: Boolean = false,
    val isTrainerLinked: Boolean = true, // Default to true to avoid flashing "not linked" state
    val error: String? = null
)

@HiltViewModel
class ClientCheckInViewModel @Inject constructor(
    private val repository: CheckInRepository,
    private val trainerRepository: TrainerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClientCheckInUiState())
    val uiState: StateFlow<ClientCheckInUiState> = _uiState.asStateFlow()

    fun loadConfig() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            checkTrainerLink()
            repository.getCheckInConfig()
                .onSuccess { config ->
                    _uiState.update { it.copy(isLoading = false, config = config) }
                }
                .onFailure { e: Throwable ->
                    // Don't show error for config load failure, maybe just log?
                    // Or set a specific error state if needed.
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    private suspend fun checkTrainerLink() {
        trainerRepository.getLinkedTrainer()
            .onSuccess { trainer ->
                _uiState.update { it.copy(isTrainerLinked = trainer != null) }
            }
            .onFailure { _: Throwable ->
                // If we can't check, assume not linked or handle error?
                _uiState.update { it.copy(isTrainerLinked = false) }
            }
    }

    fun loadHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.getClientCheckInHistory()
                .onSuccess { items ->
                    _uiState.update { it.copy(isLoading = false, history = items) }
                }
                .onFailure { e: Throwable ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun loadCheckInDetails(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, selectedCheckIn = null) }
            repository.getClientCheckInDetails(id)
                .onSuccess { detail ->
                    _uiState.update { it.copy(isLoading = false, selectedCheckIn = detail) }
                }
                .onFailure { e: Throwable ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun submitCheckIn(request: CheckInSubmissionRequest) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, error = null, submissionSuccess = false) }
            repository.submitCheckIn(request)
                .onSuccess {
                    _uiState.update { it.copy(isSubmitting = false, submissionSuccess = true) }
                    // Reload config/history after successful submission
                    loadConfig()
                    loadHistory()
                }
                .onFailure { e: Throwable ->
                    _uiState.update { it.copy(isSubmitting = false, error = e.message) }
                }
        }
    }
    
    fun resetSubmissionState() {
        _uiState.update { it.copy(submissionSuccess = false, error = null) }
    }
}
