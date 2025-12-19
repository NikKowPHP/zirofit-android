package com.ziro.fit.viewmodel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziro.fit.data.repository.LiveWorkoutRepository
import com.ziro.fit.model.Exercise
import com.ziro.fit.model.LiveWorkoutUiModel
import com.ziro.fit.model.WorkoutExerciseUi
import com.ziro.fit.model.WorkoutSetUi
import com.ziro.fit.model.WorkoutStats
import com.ziro.fit.service.ActiveWorkoutService
import com.ziro.fit.service.WorkoutStateManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import javax.inject.Inject

// Re-using the same UI state data class, but mapping from Manager state
data class WorkoutUiState(
    val activeSession: LiveWorkoutUiModel? = null,
    val elapsedSeconds: Long = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isFinishing: Boolean = false,
    val isSessionCompleted: Boolean = false,
    val workoutSuccessStats: WorkoutStats? = null,
    val availableExercises: List<Exercise> = emptyList(),
    val isExercisesLoading: Boolean = false,
    val isRestActive: Boolean = false,
    val restingExerciseId: String? = null,
    val restSecondsRemaining: Int = 0,
    val restTotalSeconds: Int = 60
)

@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val repository: LiveWorkoutRepository,
    private val workoutStateManager: WorkoutStateManager,
    private val application: Application
) : ViewModel() {
    private val _uiState = MutableStateFlow(WorkoutUiState())
    val uiState: StateFlow<WorkoutUiState> = _uiState.asStateFlow()

    init {
        // Observe the central state manager
        viewModelScope.launch {
            workoutStateManager.state.collect { managerState ->
                _uiState.update { 
                    it.copy(
                        activeSession = managerState.activeSession,
                        elapsedSeconds = managerState.elapsedSeconds,
                        isRestActive = managerState.isRestActive,
                        restSecondsRemaining = managerState.restSecondsRemaining,
                        restTotalSeconds = managerState.restTotalSeconds,
                        restingExerciseId = managerState.restingExerciseId
                    )
                }
            }
        }
        refreshActiveSession()
    }

    private fun updateServiceState(session: LiveWorkoutUiModel?) {
        if (!hasRequiredPermissions()) return

        val intent = Intent(application, ActiveWorkoutService::class.java)
        if (session != null) {
            application.startForegroundService(intent)
        } else {
            intent.action = ActiveWorkoutService.ACTION_STOP_SERVICE
            application.startService(intent)
        }
    }

    fun onPermissionsResult() {
        // Retry starting service if we have an active session
        val currentSession = workoutStateManager.state.value.activeSession
        if (currentSession != null && hasRequiredPermissions()) {
            val intent = Intent(application, ActiveWorkoutService::class.java)
            application.startForegroundService(intent)
        }
    }

    private fun hasRequiredPermissions(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(application, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(application, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    fun refreshActiveSession() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.getActiveSession()
                .onSuccess { session ->
                    // Logic to merge sessions (if active) similar to before
                    val merged = if (session != null) {
                        mergeSessions(session, workoutStateManager.state.value.activeSession)
                    } else null
                    
                    workoutStateManager.updateSession(merged)
                    updateServiceState(merged)
                    _uiState.update { it.copy(isLoading = false) }
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
                    workoutStateManager.updateSession(session)
                    updateServiceState(session)
                    _uiState.update { it.copy(isLoading = false, isSessionCompleted = false) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(error = error.localizedMessage, isLoading = false) }
                }
        }
    }

    // Pass-through methods to Manager
    fun startRestTimer(seconds: Int, exerciseId: String? = null) {
        workoutStateManager.startRestTimer(seconds, exerciseId)
    }
    
    fun stopRestTimer() {
        workoutStateManager.stopRestTimer()
    }
    
    fun adjustRestTime(secondsToAdd: Int) {
        workoutStateManager.adjustRestTime(secondsToAdd)
    }

    // ... (Keep existing methods: loadExercises, addExerciseToSession, updateSetInput, logSet)
    // Ensure they update workoutStateManager.updateSession(...) when session structure changes locally.

    fun addExercisesToSession(exercises: List<Exercise>) {
        val currentSession = workoutStateManager.state.value.activeSession ?: return
        val newExercisesUi = exercises.map { exercise ->
             WorkoutExerciseUi(
                exerciseId = exercise.id,
                exerciseName = exercise.name,
                targetReps = null,
                restSeconds = null,
                sets = listOf(
                    WorkoutSetUi(null, 1, "", "", false, 0)
                )
            )
        }
        val updatedExercises = currentSession.exercises + newExercisesUi
        val updatedSession = currentSession.copy(exercises = updatedExercises)
        workoutStateManager.updateSession(updatedSession)
    }

    fun updateSetInput(exerciseId: String, setIndex: Int, weight: String, reps: String) {
        val currentSession = workoutStateManager.state.value.activeSession ?: return
        val updatedExercises = currentSession.exercises.map { ex ->
            if (ex.exerciseId == exerciseId) {
                val updatedSets = ex.sets.toMutableList()
                if (setIndex in updatedSets.indices) {
                    updatedSets[setIndex] = updatedSets[setIndex].copy(weight = weight, reps = reps)
                    ex.copy(sets = updatedSets)
                } else ex
            } else ex
        }
        workoutStateManager.updateSession(currentSession.copy(exercises = updatedExercises))
    }

    fun addSetToExercise(exerciseId: String) {
        val currentSession = workoutStateManager.state.value.activeSession ?: return
        val updatedExercises = currentSession.exercises.map { ex ->
             if (ex.exerciseId == exerciseId) {
                 val nextSetNumber = ex.sets.size + 1
                 val nextOrder = ex.sets.maxOfOrNull { it.order }?.plus(1) ?: 0
                 val newSet = WorkoutSetUi(null, nextSetNumber, ex.sets.lastOrNull()?.weight ?: "", "", false, nextOrder)
                 ex.copy(sets = ex.sets + newSet)
             } else ex
        }
        workoutStateManager.updateSession(currentSession.copy(exercises = updatedExercises))
    }

    fun logSet(exerciseId: String, set: WorkoutSetUi) {
        val session = workoutStateManager.state.value.activeSession ?: return
        val weightVal = set.weight.toDoubleOrNull()
        val repsVal = set.reps.toIntOrNull()
        if (weightVal == null || repsVal == null) return

        // Optimistic Update
        val updatedExercises = session.exercises.map { ex ->
            if (ex.exerciseId == exerciseId) {
                val updatedSets = ex.sets.map { s ->
                    if (s.order == set.order) s.copy(isCompleted = true) else s
                }
                ex.copy(sets = updatedSets)
            } else ex
        }
        workoutStateManager.updateSession(session.copy(exercises = updatedExercises))
        
        // Timer
        val exercise = session.exercises.find { it.exerciseId == exerciseId }
        val restTime = exercise?.restSeconds ?: 60
        startRestTimer(restTime, exerciseId)

        viewModelScope.launch {
            repository.logSet(session.id, exerciseId, repsVal, weightVal, set.order)
                .onSuccess {
                    repository.getActiveSession().onSuccess { refreshed ->
                        if (refreshed != null) {
                            val merged = mergeSessions(refreshed, workoutStateManager.state.value.activeSession)
                            workoutStateManager.updateSession(merged)
                        }
                    }
                }
                .onFailure {
                    _uiState.update { it.copy(error = "Failed to save set") }
                }
        }
    }

    fun finishWorkout(notes: String? = null) {
        val sessionId = workoutStateManager.state.value.activeSession?.id ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isFinishing = true) }
            repository.finishSession(sessionId, notes)
                .onSuccess { response ->
                    workoutStateManager.updateSession(null) // This stops the service
                    updateServiceState(null)
                    _uiState.update { it.copy(
                        isFinishing = false, 
                        isSessionCompleted = true,
                        workoutSuccessStats = response.stats
                    ) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isFinishing = false, error = e.localizedMessage) }
                }
        }
    }

    fun loadExercises(query: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isExercisesLoading = true) }
            repository.getExercises(query)
                .onSuccess { response ->
                    _uiState.update { it.copy(availableExercises = response.exercises, isExercisesLoading = false) }
                }
                .onFailure {
                    _uiState.update { it.copy(isExercisesLoading = false) }
                }
        }
    }

    private fun mergeSessions(newSession: LiveWorkoutUiModel, oldSession: LiveWorkoutUiModel?): LiveWorkoutUiModel {
        if (oldSession == null) return newSession
        val newExerciseIds = newSession.exercises.map { it.exerciseId }.toSet()
        val localOnlyExercises = oldSession.exercises.filter { !newExerciseIds.contains(it.exerciseId) }
        return newSession.copy(exercises = newSession.exercises + localOnlyExercises)
    }

    fun onSessionCompletedNavigated() {
        _uiState.update { it.copy(isSessionCompleted = false, workoutSuccessStats = null) }
    }
}