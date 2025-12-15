package com.ziro.fit.ui.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziro.fit.data.repository.LiveWorkoutRepository
import com.ziro.fit.model.LiveWorkoutUiModel
import com.ziro.fit.model.WorkoutSetUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import javax.inject.Inject

data class LiveWorkoutUiState(
    val isLoading: Boolean = true,
    val session: LiveWorkoutUiModel? = null,
    val elapsedSeconds: Long = 0,
    val error: String? = null,
    val isFinishing: Boolean = false,
    val isFinished: Boolean = false
)

@HiltViewModel
class LiveWorkoutViewModel @Inject constructor(
    private val repository: LiveWorkoutRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LiveWorkoutUiState())
    val uiState: StateFlow<LiveWorkoutUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        loadActiveSession()
    }

    private fun loadActiveSession() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            repository.getActiveSession()
                .onSuccess { session ->
                    _uiState.update { it.copy(session = session, isLoading = false) }
                    startTimer(session.startTime)
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
        }
    }

    private fun startTimer(startTimeIso: String) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            val start = LocalDateTime.parse(startTimeIso.removeSuffix("Z"))
            while (isActive) {
                val now = LocalDateTime.now()
                val seconds = Duration.between(start, now).seconds
                _uiState.update { it.copy(elapsedSeconds = seconds) }
                delay(1000)
            }
        }
    }

    // Called when user types in the text fields
    fun updateSetInput(exerciseId: String, setIndex: Int, weight: String, reps: String) {
        val currentSession = _uiState.value.session ?: return
        
        val updatedExercises = currentSession.exercises.map { ex ->
            if (ex.exerciseId == exerciseId) {
                val updatedSets = ex.sets.toMutableList()
                if (setIndex in updatedSets.indices) {
                    val targetSet = updatedSets[setIndex]
                    updatedSets[setIndex] = targetSet.copy(weight = weight, reps = reps)
                    ex.copy(sets = updatedSets)
                } else ex
            } else ex
        }
        
        _uiState.update { it.copy(session = currentSession.copy(exercises = updatedExercises)) }
    }

    fun onToggleSet(exerciseId: String, set: WorkoutSetUi) {
        val weightVal = set.weight.toDoubleOrNull()
        val repsVal = set.reps.toIntOrNull()

        if (weightVal == null || repsVal == null) return // Invalid input

        // 1. Optimistic UI Update: Mark as completed visually immediately
        // Note: Real confirmation comes from reloading, but we want UI to feel snappy.
        val currentSession = _uiState.value.session ?: return
        val updatedExercises = currentSession.exercises.map { ex ->
            if (ex.exerciseId == exerciseId) {
                val updatedSets = ex.sets.map { s ->
                    if (s.order == set.order) {
                        s.copy(isCompleted = true)
                    } else s
                }
                ex.copy(sets = updatedSets)
            } else ex
        }
        _uiState.update { it.copy(session = currentSession.copy(exercises = updatedExercises)) }

        // 2. API Call
        viewModelScope.launch {
            repository.logSet(
                _uiState.value.session!!.id, 
                exerciseId, 
                repsVal, 
                weightVal, 
                set.order
            ).onSuccess {
                // Refresh to get consistent state and real IDs from server
                // We do a "silent" refresh (no loading spinner) to keep UX smooth
                repository.getActiveSession().onSuccess { refreshedSession ->
                    _uiState.update { it.copy(session = refreshedSession) }
                }
            }.onFailure {
                // Revert UI state on failure? 
                // For simplicity, we just show error in state, but ideally we'd uncheck the box.
                _uiState.update { it.copy(error = "Failed to save set") }
            }
        }
    }

    fun finishWorkout() {
        val sessionId = _uiState.value.session?.id ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isFinishing = true) }
            repository.finishSession(sessionId, null)
                .onSuccess {
                    _uiState.update { it.copy(isFinished = true) }
                    timerJob?.cancel()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isFinishing = false, error = "Failed to finish: ${e.message}") }
                }
        }
    }
}
      