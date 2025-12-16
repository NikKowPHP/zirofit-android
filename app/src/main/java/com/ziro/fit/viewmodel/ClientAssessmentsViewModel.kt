package com.ziro.fit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziro.fit.data.repository.ClientRepository
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
    val assessments: List<AssessmentResult> = emptyList()
)

@HiltViewModel
class ClientAssessmentsViewModel @Inject constructor(
    private val clientRepository: ClientRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClientAssessmentsUiState())
    val uiState: StateFlow<ClientAssessmentsUiState> = _uiState.asStateFlow()

    fun loadAssessments(clientId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
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
