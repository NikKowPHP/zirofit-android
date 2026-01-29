package com.ziro.fit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziro.fit.data.repository.AssessmentsRepository
import com.ziro.fit.data.repository.ClientRepository
import com.ziro.fit.model.Assessment
import com.ziro.fit.model.AssessmentResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ClientAssessmentsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val assessments: List<AssessmentResult> = emptyList(),
    val availableAssessmentTypes: List<Assessment> = emptyList(),
    val isCreatingType: Boolean = false
)

@HiltViewModel
class ClientAssessmentsViewModel @Inject constructor(
    private val clientRepository: ClientRepository,
    private val assessmentsRepository: AssessmentsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClientAssessmentsUiState())
    val uiState: StateFlow<ClientAssessmentsUiState> = _uiState.asStateFlow()

    fun loadAssessments(clientId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            // Load client's assessment results
            clientRepository.getClientAssessments(clientId)
                .onSuccess { assessments ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        assessments = assessments
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to load assessments"
                    )
                }

            // Also load available assessment types for the dropdown
            loadAvailableAssessmentTypes()
        }
    }

    private suspend fun loadAvailableAssessmentTypes() {
        assessmentsRepository.getAssessments().collect { result ->
            result.onSuccess { types ->
                _uiState.value = _uiState.value.copy(availableAssessmentTypes = types)
            }
            // If fetching types fails, we just don't show them, or we could log it.
            // Keeping existing error state for client data priority.
        }
    }

    fun createAssessment(clientId: String, assessmentId: String, date: String, value: Double, notes: String?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = clientRepository.createAssessment(clientId, assessmentId, date, value, notes)
            result.onSuccess {
                loadAssessments(clientId)
            }.onFailure { e ->
                 _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to create assessment"
                )
            }
        }
    }

    fun createAssessmentType(name: String, unit: String, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCreatingType = true, error = null)
            assessmentsRepository.createAssessment(name, null, unit).collect { result ->
                result.onSuccess { newAssessment ->
                    _uiState.value = _uiState.value.copy(
                        isCreatingType = false,
                        availableAssessmentTypes = _uiState.value.availableAssessmentTypes + newAssessment
                    )
                    onSuccess(newAssessment.id)
                }.onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isCreatingType = false,
                        error = e.message ?: "Failed to create assessment type"
                    )
                }
            }
        }
    }

    fun deleteAssessment(clientId: String, resultId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = clientRepository.deleteAssessment(clientId, resultId)
            result.onSuccess {
                loadAssessments(clientId)
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to delete assessment"
                )
            }
        }
    }
}
