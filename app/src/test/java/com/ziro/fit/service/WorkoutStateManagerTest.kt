package com.ziro.fit.service

import com.ziro.fit.model.LiveWorkoutUiModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import kotlinx.coroutines.flow.StateFlow

class WorkoutStateManagerTest {
    private val manager = WorkoutStateManager()

    @Test
    fun initialState_hasNullActiveSession() {
        assertNull(manager.state.value.activeSession)
    }

    @Test
    fun initialState_hasZeroElapsedSeconds() {
        assertEquals(0L, manager.state.value.elapsedSeconds)
    }

    @Test
    fun initialState_hasNoRestActive() {
        assertFalse(manager.state.value.isRestActive)
    }

    @Test
    fun initialState_hasZeroRestSeconds() {
        assertEquals(0, manager.state.value.restSecondsRemaining)
        assertEquals(0, manager.state.value.restTotalSeconds)
    }

    @Test
    fun initialState_hasNullRestingExerciseId() {
        assertNull(manager.state.value.restingExerciseId)
    }

    @Test
    fun state_returnsStateFlow() {
        assertTrue(manager.state is StateFlow<*>)
    }

    @Test
    fun state_emitsWorkoutState() {
        val currentState = manager.state.value
        assertTrue(currentState is WorkoutState)
    }

    @Test
    fun updateSession_withNull_clearsActiveSession() {
        manager.updateSession(null)
        assertNull(manager.state.value.activeSession)
    }

    @Test
    fun adjustRestTime_positiveSeconds_addsToBothTotalAndRemaining() {
        manager.startRestTimer(60, "ex1")
        manager.adjustRestTime(15)
        
        assertEquals(75, manager.state.value.restTotalSeconds)
        assertEquals(75, manager.state.value.restSecondsRemaining)
    }

    @Test
    fun adjustRestTime_negativeSeconds_subtractsFromBoth() {
        manager.startRestTimer(60, "ex1")
        manager.adjustRestTime(-10)
        
        assertEquals(50, manager.state.value.restTotalSeconds)
        assertEquals(50, manager.state.value.restSecondsRemaining)
    }

    @Test
    fun adjustRestTime_neverGoesBelowZero() {
        manager.startRestTimer(30, "ex1")
        manager.adjustRestTime(-50)
        
        assertEquals(0, manager.state.value.restTotalSeconds)
        assertEquals(0, manager.state.value.restSecondsRemaining)
    }

    @Test
    fun adjustRestTime_addNegativeThatExceedsRemaining_setsBothToZero() {
        manager.startRestTimer(20, "ex1")
        manager.adjustRestTime(-100)
        
        assertEquals(0, manager.state.value.restTotalSeconds)
        assertEquals(0, manager.state.value.restSecondsRemaining)
    }

    @Test
    fun adjustRestTime_zeroSeconds_doesNotChange() {
        manager.startRestTimer(60, "ex1")
        manager.adjustRestTime(0)
        
        assertEquals(60, manager.state.value.restTotalSeconds)
        assertEquals(60, manager.state.value.restSecondsRemaining)
    }

    @Test
    fun stopRestTimer_setsIsRestActiveToFalse() {
        manager.startRestTimer(60, "ex1")
        manager.stopRestTimer()
        
        assertFalse(manager.state.value.isRestActive)
    }

    @Test
    fun stopRestTimer_clearsRestingExerciseId() {
        manager.startRestTimer(60, "ex1")
        manager.stopRestTimer()
        
        assertNull(manager.state.value.restingExerciseId)
    }

    @Test
    fun stopRestTimer_doesNotClearRestSeconds() {
        manager.startRestTimer(60, "ex1")
        manager.stopRestTimer()
        
        assertEquals(60, manager.state.value.restSecondsRemaining)
    }

    @Test
    fun stopRestTimer_doesNotClearRestTotalSeconds() {
        manager.startRestTimer(60, "ex1")
        manager.stopRestTimer()
        
        assertEquals(60, manager.state.value.restTotalSeconds)
    }
}
