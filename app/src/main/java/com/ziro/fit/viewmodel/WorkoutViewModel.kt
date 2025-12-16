package com.ziro.fit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziro.fit.data.repository.LiveWorkoutRepository
import com.ziro.fit.model.LiveWorkoutUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WorkoutUiState(
    val activeSession: LiveWorkoutUiModel? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val repository: LiveWorkoutRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutUiState())
    val uiState: StateFlow<WorkoutUiState> = _uiState.asStateFlow()

    fun startWorkout(clientId: String?, templateId: String?, plannedSessionId: String?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.startWorkout(clientId, templateId, plannedSessionId)
                .onSuccess { session ->
                    _uiState.update { it.copy(activeSession = session, isLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(error = error.localizedMessage, isLoading = false) }
                }
        }
    }
    
    fun getActiveSession() {
        viewModelScope.launch {
             _uiState.update { it.copy(isLoading = true, error = null) }
            repository.getActiveSession()
                .onSuccess { session ->
                    _uiState.update { it.copy(activeSession = session, isLoading = false) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(error = error.localizedMessage, isLoading = false) }
                }
        }
    }
}
