package com.ziro.fit.service

import com.ziro.fit.model.LiveWorkoutUiModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max

data class WorkoutState(
    val activeSession: LiveWorkoutUiModel? = null,
    val elapsedSeconds: Long = 0,
    val isRestActive: Boolean = false,
    val restSecondsRemaining: Int = 0,
    val restTotalSeconds: Int = 0,
    val restingExerciseId: String? = null
)

@Singleton
class WorkoutStateManager @Inject constructor() {
    private val _state = MutableStateFlow(WorkoutState())
    val state: StateFlow<WorkoutState> = _state.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var timerJob: Job? = null
    private var restTimerJob: Job? = null

    fun updateSession(session: LiveWorkoutUiModel?) {
        _state.update { it.copy(activeSession = session) }
        if (session != null && timerJob == null) {
            startTimer(session.startTime)
        } else if (session == null) {
            stopTimer()
        }
    }

    private fun startTimer(startTimeIso: String) {
        timerJob?.cancel()
        timerJob = scope.launch {
            val startInstant = try {
                if (startTimeIso.endsWith("Z")) {
                    Instant.parse(startTimeIso)
                } else {
                    LocalDateTime.parse(startTimeIso)
                        .atZone(ZoneOffset.UTC)
                        .toInstant()
                }
            } catch (e: Exception) {
                Instant.now()
            }

            while (isActive) {
                val now = Instant.now()
                val seconds = Duration.between(startInstant, now).seconds
                _state.update { it.copy(elapsedSeconds = max(0, seconds)) }
                delay(1000)
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
        _state.update { it.copy(elapsedSeconds = 0) }
    }

    fun startRestTimer(seconds: Int, exerciseId: String? = null) {
        restTimerJob?.cancel()
        _state.update { 
            it.copy(
                isRestActive = true,
                restSecondsRemaining = seconds,
                restTotalSeconds = seconds,
                restingExerciseId = exerciseId
            ) 
        }

        restTimerJob = scope.launch {
            var remaining = seconds
            while (remaining > 0 && isActive) {
                delay(1000)
                remaining--
                _state.update { it.copy(restSecondsRemaining = remaining) }
            }
            // Rest Finished
            _state.update { it.copy(isRestActive = false, restSecondsRemaining = 0) }
        }
    }

    fun stopRestTimer() {
        restTimerJob?.cancel()
        _state.update { it.copy(isRestActive = false, restingExerciseId = null) }
    }

    fun adjustRestTime(secondsToAdd: Int) {
        _state.update { 
            val newTotal = it.restTotalSeconds + secondsToAdd
            val newRemaining = it.restSecondsRemaining + secondsToAdd
            it.copy(
                restTotalSeconds = max(0, newTotal), 
                restSecondsRemaining = max(0, newRemaining)
            )
        }
    }
}
