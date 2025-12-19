package com.ziro.fit.viewmodel

import android.app.Application
import android.util.Log
import app.cash.turbine.test
import com.ziro.fit.data.repository.LiveWorkoutRepository
import com.ziro.fit.model.LiveWorkoutUiModel
import com.ziro.fit.service.WorkoutState
import com.ziro.fit.service.WorkoutStateManager
import com.ziro.fit.util.MainDispatcherRule
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.Instant

@ExperimentalCoroutinesApi
class WorkoutViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: LiveWorkoutRepository = mockk()
    private val workoutStateManager: WorkoutStateManager = mockk(relaxed = true)
    private val application: Application = mockk(relaxed = true)
    
    private lateinit var viewModel: WorkoutViewModel
    private val managerStateFlow = MutableStateFlow(WorkoutState())

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.e(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>(), any<Throwable>()) } returns 0
        every { Log.w(any<String>(), any<String>()) } returns 0
        
        // Mock State Manager Flow
        every { workoutStateManager.state } returns managerStateFlow
        
        // Default mock for initial refresh in init block
        coEvery { repository.getActiveSession() } returns Result.success(null)
    }

/*
    @Test
    fun `refreshActiveSession updates state with session`() = runTest {
        val session = LiveWorkoutUiModel(
            id = "sess1",
            title = "Push Day",
            startTime = Instant.now().toString(),
            exercises = emptyList()
        )
        
        coEvery { repository.getActiveSession() } returns Result.success(session)
        every { workoutStateManager.updateSession(any()) } answers {
            val s = firstArg<LiveWorkoutUiModel?>()
            managerStateFlow.value = WorkoutState(activeSession = s)
        }

        viewModel = WorkoutViewModel(repository, workoutStateManager, application)
        advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertEquals("sess1", state.activeSession?.id)
        assertFalse(state.isLoading)
    }

    @Test
    fun `cancelWorkout calls repository and clears state`() = runTest {
         // Setup active session
        val session = LiveWorkoutUiModel(
            id = "sess1",
            title = "Push Day",
            startTime = Instant.now().toString(),
            exercises = emptyList()
        )
        managerStateFlow.value = WorkoutState(activeSession = session)
        coEvery { repository.getActiveSession() } returns Result.success(session)
        
        viewModel = WorkoutViewModel(repository, workoutStateManager, application)
        advanceUntilIdle() // Wait for init refresh
        
        // Setup Cancel Mocks
        coEvery { repository.cancelActiveWorkout("sess1") } returns Result.success(Unit)
        every { workoutStateManager.updateSession(null) } answers {
             managerStateFlow.value = WorkoutState(activeSession = null)
        }

        viewModel.cancelWorkout()
        advanceUntilIdle() // Wait for cancel coroutine
        
        coVerify { repository.cancelActiveWorkout("sess1") }
    }
*/
}
