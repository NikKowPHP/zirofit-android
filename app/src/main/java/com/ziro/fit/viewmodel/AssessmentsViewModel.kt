package com.ziro.fit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziro.fit.data.repository.AssessmentsRepository
import com.ziro.fit.model.Assessment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AssessmentsUiState(
    val assessments: List<Assessment> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val operationSuccess: Boolean = false // One-shot event for navigation/feedback
)

@HiltViewModel
class AssessmentsViewModel @Inject constructor(
    private val repository: AssessmentsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AssessmentsUiState())
    val uiState: StateFlow<AssessmentsUiState> = _uiState.asStateFlow()

    fun loadAssessments() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.getAssessments().collect { result ->
                result.onSuccess { list ->
                    _uiState.update { it.copy(isLoading = false, assessments = list) }
                }.onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
            }
        }
    }

    fun createAssessment(name: String, description: String?, unit: String) {
        viewModelScope.launch {
             _uiState.update { it.copy(isLoading = true, error = null, operationSuccess = false) }
            repository.createAssessment(name, description, unit).collect { result ->
                result.onSuccess { newAssessment ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            operationSuccess = true,
                            assessments = it.assessments + newAssessment 
                        ) 
                    }
                }.onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
            }
        }
    }

    fun updateAssessment(id: String, name: String, description: String?, unit: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, operationSuccess = false) }
            repository.updateAssessment(id, name, description, unit).collect { result ->
                 result.onSuccess { updatedAssessment ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            operationSuccess = true,
                            assessments = state.assessments.map { if (it.id == id) updatedAssessment else it }
                        )
                    }
                }.onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
            }
        }
    }

    fun deleteAssessment(id: String) {
         viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.deleteAssessment(id).collect { result ->
                result.onSuccess {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            assessments = state.assessments.filter { it.id != id }
                        )
                    }
                }.onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
            }
        }
    }

    fun resetOperationSuccess() {
        _uiState.update { it.copy(operationSuccess = false) }
    }
}
