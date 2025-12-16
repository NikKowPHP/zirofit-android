package com.ziro.fit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziro.fit.data.repository.ExerciseRepository
import com.ziro.fit.model.Exercise
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExerciseUiState(
    val exercises: List<Exercise> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false // One-shot event for navigation/feedback
)

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExerciseUiState())
    val uiState: StateFlow<ExerciseUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        fetchExercises()
    }

    fun fetchExercises(query: String? = null) {
        // Debounce search
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (query != null) {
                delay(500) // Debounce delay
            }
            
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = exerciseRepository.getExercises(query)
            result.onSuccess { exercises ->
                _uiState.value = _uiState.value.copy(
                    exercises = exercises,
                    isLoading = false
                )
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to fetch exercises"
                )
            }
        }
    }

    fun createExercise(name: String, muscleGroup: String?, equipment: String?, videoUrl: String?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, saveSuccess = false)
            val result = exerciseRepository.createExercise(name, muscleGroup, equipment, videoUrl)
            result.onSuccess {
                fetchExercises() // Refresh list
                _uiState.value = _uiState.value.copy(saveSuccess = true) // Trigger success
                // Reset success flag after consumption (handled by UI observation usually)
                delay(100) 
                 _uiState.value = _uiState.value.copy(saveSuccess = false)
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to create exercise"
                )
            }
        }
    }

    fun updateExercise(id: String, name: String, muscleGroup: String?, equipment: String?, videoUrl: String?) {
        viewModelScope.launch {
             _uiState.value = _uiState.value.copy(isLoading = true, error = null, saveSuccess = false)
            val result = exerciseRepository.updateExercise(id, name, muscleGroup, equipment, videoUrl)
            result.onSuccess {
                fetchExercises()
                 _uiState.value = _uiState.value.copy(saveSuccess = true)
                delay(100) 
                 _uiState.value = _uiState.value.copy(saveSuccess = false)
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to update exercise"
                )
            }
        }
    }

    fun deleteExercise(id: String) {
        viewModelScope.launch {
            // Optimistic update
            val currentList = _uiState.value.exercises
            _uiState.value = _uiState.value.copy(exercises = currentList.filter { it.id != id })
            
            val result = exerciseRepository.deleteExercise(id)
            if (result.isFailure) {
                fetchExercises() // Revert
                _uiState.value = _uiState.value.copy(error = "Failed to delete exercise")
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
