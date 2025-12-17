<response>
  <reasoning>
    The user is reporting that the timer starts at "60 min" immediately in client mode.
    This persists because my previous fix still had a fallback path or logic that wasn't robust enough for the specific timestamp format returned by the server.
    
    The issue is almost certainly a Timezone interpretation mismatch:
    - Server sends a time string (e.g., "12:00").
    - The app (in Poland/CET, UTC+1) interprets "12:00" as "12:00 Local Time" (which is 11:00 UTC).
    - But the server meant "12:00 UTC".
    - `Now` is "13:00 Local" (12:00 UTC).
    - The difference calculated was: `Now (12:00 UTC)` - `Start (11:00 UTC)` = 1 Hour (60 mins).

    My previous fix tried to use `Instant` but likely fell back to the `catch` block which used `atZone(ZoneId.systemDefault())`, perpetuating the error.

    I will modify `app/src/main/java/com/ziro/fit/viewmodel/WorkoutViewModel.kt` again.
    This time, I will enforce **UTC interpretation**. If the string doesn't have a timezone indicator ('Z'), I will append 'Z' or explicitly parse it as UTC, avoiding `ZoneId.systemDefault()` entirely for the server timestamp.
  </reasoning>
  <modifications>
    <file path="app/src/main/java/com/ziro/fit/viewmodel/WorkoutViewModel.kt">
      <![CDATA[
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
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import javax.inject.Inject
import kotlin.math.max

data class WorkoutUiState(
    val activeSession: LiveWorkoutUiModel? = null,
    val elapsedSeconds: Long = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isFinishing: Boolean = false,
    val isSessionCompleted: Boolean = false,
    // Exercise Library State
    val availableExercises: List<Exercise> = emptyList(),
    val isExercisesLoading: Boolean = false,
    // Rest Timer State
    val isRestActive: Boolean = false,
    val restSecondsRemaining: Int = 0,
    val restTotalSeconds: Int = 60
)

@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val repository: LiveWorkoutRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutUiState())
    val uiState: StateFlow<WorkoutUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var restTimerJob: Job? = null

    init {
        refreshActiveSession()
    }

    fun refreshActiveSession() {
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
            restSeconds = null,
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
                // FORCE UTC INTERPRETATION
                // The server sends UTC time. If we parse it as "System Default" (Local) when 'Z' is missing,
                // we get a timezone offset error (e.g. +1h in Poland results in 60min elapsed immediately).
                
                val startInstant = try {
                    // 1. Try parsing strictly as Instant (handles "2023-10-01T12:00:00Z")
                    if (startTimeIso.endsWith("Z")) {
                        Instant.parse(startTimeIso)
                    } else {
                        // 2. If missing 'Z', parse as LocalDateTime then force UTC zone
                        LocalDateTime.parse(startTimeIso)
                            .atZone(ZoneOffset.UTC)
                            .toInstant()
                    }
                } catch (e: Exception) {
                    // 3. Fallback: try appending Z blindly if LocalDateTime parse failed (maybe it was just a format quirk)
                    Instant.parse("${startTimeIso}Z")
                }

                while (isActive) {
                    val now = Instant.now()
                    val seconds = Duration.between(startInstant, now).seconds
                    // Ensure we don't show negative time if client clock is slightly behind server
                    _uiState.update { it.copy(elapsedSeconds = maxOf(0, seconds)) }
                    delay(1000)
                }
            } catch (e: Exception) {
                // If parsing completely fails, start from 0 to avoid showing weird values like "60 min"
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
        
        // Trigger Rest Timer
        val exercise = session.exercises.find { it.exerciseId == exerciseId }
        val restTime = exercise?.restSeconds ?: 60 // Default 60s if not specified (e.g. freestyle)
        startRestTimer(restTime)

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
    
    fun startRestTimer(seconds: Int) {
        restTimerJob?.cancel()
        _uiState.update { 
            it.copy(
                isRestActive = true,
                restTotalSeconds = seconds,
                restSecondsRemaining = seconds
            ) 
        }
        
        restTimerJob = viewModelScope.launch {
            var remaining = seconds
            while (remaining > 0 && isActive) {
                delay(1000)
                remaining--
                _uiState.update { it.copy(restSecondsRemaining = remaining) }
            }
            stopRestTimer()
        }
    }
    
    fun stopRestTimer() {
        restTimerJob?.cancel()
        _uiState.update { it.copy(isRestActive = false) }
    }
    
    fun adjustRestTime(secondsToAdd: Int) {
        _uiState.update { 
            val newTotal = it.restTotalSeconds + secondsToAdd
            val newRemaining = it.restSecondsRemaining + secondsToAdd
            it.copy(
                restTotalSeconds = maxOf(0, newTotal), 
                restSecondsRemaining = maxOf(0, newRemaining)
            )
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
      ]]>
    </file>
    <file path="TASKS.md">
      <![CDATA[
- [x] Fix timer starting at 60min for freestyle workouts (forced UTC parsing)
      ]]>
    </file>
  </modifications>
</response>