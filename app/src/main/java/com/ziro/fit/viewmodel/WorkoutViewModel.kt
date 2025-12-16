package com.ziro.fit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziro.fit.data.repository.LiveWorkoutRepository
import com.ziro.fit.model.Exercise
import com.ziro.fit.model.LiveWorkoutUiModel
import com.ziro.fit.model.WorkoutExerciseUi
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

data class WorkoutUiState(
    val activeSession: LiveWorkoutUiModel? = null,
    val elapsedSeconds: Long = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isFinishing: Boolean = false,
    val isSessionCompleted: Boolean = false,
    // Exercise Library State
    val availableExercises: List<Exercise> = emptyList(),
    val isExercisesLoading: Boolean = false
)

@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val repository: LiveWorkoutRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutUiState())
    val uiState: StateFlow<WorkoutUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        checkForActiveSession()
    }

    private fun checkForActiveSession() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.getActiveSession()
                .onSuccess { session ->
                    if (session.id.isNotEmpty()) {
                        _uiState.update { it.copy(activeSession = session, isLoading = false) }
                        startTimer(session.startTime)
                    } else {
                        _uiState.update { it.copy(activeSession = null, isLoading = false) }
                    }
                }
                .onFailure {
                    _uiState.update { it.copy(activeSession = null, isLoading = false) }
                }
        }
    }

    fun startWorkout(clientId: String?, templateId: String?, plannedSessionId: String?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.startWorkout(clientId, templateId, plannedSessionId)
                .onSuccess { session ->
                    _uiState.update { it.copy(activeSession = session, isLoading = false, isSessionCompleted = false) }
                    startTimer(session.startTime)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(error = error.localizedMessage, isLoading = false) }
                }
        }
    }

    fun loadExercises(query: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isExercisesLoading = true) }
            repository.getExercises(query)
                .onSuccess { exercises ->
                    _uiState.update { it.copy(availableExercises = exercises, isExercisesLoading = false) }
                }
                .onFailure {
                    _uiState.update { it.copy(isExercisesLoading = false) } // Silent fail for now
                }
        }
    }

    fun addExerciseToSession(exercise: Exercise) {
        val currentSession = _uiState.value.activeSession ?: return
        
        // Create a new UI exercise with one empty ghost set
        val newExerciseUi = WorkoutExerciseUi(
            exerciseId = exercise.id,
            exerciseName = exercise.name,
            targetReps = null,
            sets = listOf(
                WorkoutSetUi(
                    logId = null,
                    setNumber = 1,
                    weight = "",
                    reps = "",
                    isCompleted = false,
                    order = 0
                )
            )
        )
        
        // Check if exercise already exists to avoid duplicates (optional, usually we allow duplicates in workout)
        // For simplicity, we just append it.
        val updatedExercises = currentSession.exercises + newExerciseUi
        _uiState.update { it.copy(activeSession = currentSession.copy(exercises = updatedExercises)) }
    }

    private fun startTimer(startTimeIso: String) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            try {
                val start = LocalDateTime.parse(startTimeIso.removeSuffix("Z"))
                while (isActive) {
                    val now = LocalDateTime.now()
                    val seconds = Duration.between(start, now).seconds
                    _uiState.update { it.copy(elapsedSeconds = maxOf(0, seconds)) }
                    delay(1000)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(elapsedSeconds = 0) }
            }
        }
    }

    fun updateSetInput(exerciseId: String, setIndex: Int, weight: String, reps: String) {
        val currentSession = _uiState.value.activeSession ?: return
        
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
        
        _uiState.update { it.copy(activeSession = currentSession.copy(exercises = updatedExercises)) }
    }
    
    fun addSetToExercise(exerciseId: String) {
        val currentSession = _uiState.value.activeSession ?: return
        
        val updatedExercises = currentSession.exercises.map { ex ->
             if (ex.exerciseId == exerciseId) {
                 val nextSetNumber = ex.sets.size + 1
                 val nextOrder = ex.sets.maxOfOrNull { it.order }?.plus(1) ?: 0
                 
                 val newSet = WorkoutSetUi(
                    logId = null,
                    setNumber = nextSetNumber,
                    weight = ex.sets.lastOrNull()?.weight ?: "", // Carry over weight
                    reps = "",
                    isCompleted = false,
                    order = nextOrder
                 )
                 ex.copy(sets = ex.sets + newSet)
             } else ex
        }
         _uiState.update { it.copy(activeSession = currentSession.copy(exercises = updatedExercises)) }
    }

    fun logSet(exerciseId: String, set: WorkoutSetUi) {
        val session = _uiState.value.activeSession ?: return
        val weightVal = set.weight.toDoubleOrNull()
        val repsVal = set.reps.toIntOrNull()

        if (weightVal == null || repsVal == null) return

        // 1. Optimistic Update
        val updatedExercises = session.exercises.map { ex ->
            if (ex.exerciseId == exerciseId) {
                val updatedSets = ex.sets.map { s ->
                    if (s.order == set.order) s.copy(isCompleted = true) else s
                }
                ex.copy(sets = updatedSets)
            } else ex
        }
        _uiState.update { it.copy(activeSession = session.copy(exercises = updatedExercises)) }

        // 2. API Call
        viewModelScope.launch {
            repository.logSet(session.id, exerciseId, repsVal, weightVal, set.order)
                .onSuccess {
                    repository.getActiveSession().onSuccess { refreshed ->
                        _uiState.update { it.copy(activeSession = refreshed) }
                    }
                }
                .onFailure {
                    _uiState.update { it.copy(error = "Failed to save set") }
                }
        }
    }

    fun finishWorkout(notes: String? = null) {
        val sessionId = _uiState.value.activeSession?.id ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isFinishing = true) }
            repository.finishSession(sessionId, notes)
                .onSuccess {
                    timerJob?.cancel()
                    _uiState.update { it.copy(
                        isFinishing = false, 
                        activeSession = null, 
                        isSessionCompleted = true 
                    ) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isFinishing = false, error = e.localizedMessage) }
                }
        }
    }
    
    fun onSessionCompletedNavigated() {
        _uiState.update { it.copy(isSessionCompleted = false) }
    }
}
