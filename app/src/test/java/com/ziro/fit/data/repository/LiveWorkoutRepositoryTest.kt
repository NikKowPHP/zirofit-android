package com.ziro.fit.data.repository

import android.util.Log
import com.ziro.fit.data.remote.ZiroApi
import com.ziro.fit.model.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class LiveWorkoutRepositoryTest {

    private val api: ZiroApi = mockk()
    private val repository = LiveWorkoutRepository(api)

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.e(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>(), any<Throwable>()) } returns 0
        every { Log.w(any<String>(), any<String>()) } returns 0
    }

    @Test
    fun `getActiveSession returns null when no session is active`() = runBlocking {
        val response = ApiResponse(data = GetActiveSessionResponse(session = null), success = true)
        coEvery { api.getActiveSession() } returns response

        val result = repository.getActiveSession()

        assertTrue(result.isSuccess)
        assertNull(result.getOrNull())
    }

    @Test
    fun `getActiveSession maps correctly with ghost sets`() = runBlocking {
        // Template has 1 exercise with target 3 sets
        val templateExercise = ServerTemplateExercise(
            id = "step1",
            exerciseId = "ex1",
            order = 0,
            targetSets = 3,
            targetReps = "10",
            restSeconds = 60,
            notes = null,
            exercise = ServerExerciseInfo(id = "ex1", name = "Bench Press", equipment = null, videoUrl = null, description = null)
        )
        val template = ServerTemplate(id = "temp1", name = "Push Day", exercises = listOf(templateExercise))

        // Only 1 log exists (1st set)
        val log = ServerExerciseLog(
            id = "log1",
            createdAt = null,
            reps = 10,
            weight = 60.0,
            order = 0,
            isCompleted = true,
            supersetKey = null,
            orderInSuperset = null,
            exercise = ServerExerciseInfo(id = "ex1", name = "Bench Press", equipment = null, videoUrl = null, description = null)
        )

        val sessionResponse = ServerLiveSessionResponse(
            id = "sess1",
            startTime = "2023-01-01T10:00:00Z",
            endTime = null,
            status = "active",
            notes = null,
            createdAt = null,
            updatedAt = null,
            workoutTemplateId = "temp1",
            plannedDate = null,
            client = null,
            workoutTemplate = template,
            exerciseLogs = listOf(log)
        )

        coEvery { api.getActiveSession() } returns ApiResponse(data = GetActiveSessionResponse(session = sessionResponse), success = true)

        val result = repository.getActiveSession()

        assertTrue(result.isSuccess)
        val uiModel = result.getOrNull()
        assertNotNull(uiModel)
        assertEquals(1, uiModel!!.exercises.size)
        val exercise = uiModel.exercises[0]
        assertEquals(3, exercise.sets.size)
        
        // 1st set is real
        assertTrue(exercise.sets[0].isCompleted)
        assertEquals("log1", exercise.sets[0].logId)
        
        // 2nd and 3rd sets are ghosts
        assertFalse(exercise.sets[1].isCompleted)
        assertNull(exercise.sets[1].logId)
        assertFalse(exercise.sets[2].isCompleted)
        assertNull(exercise.sets[2].logId)
    }

    @Test
    fun `getActiveSession handles ad-hoc exercises`() = runBlocking {
        // Template is empty
        val template = ServerTemplate(id = "temp1", name = "Empty", exercises = emptyList())

        // 1 log exists for an exercise not in template
        val log = ServerExerciseLog(
            id = "log1",
            createdAt = null,
            reps = 12,
            weight = 20.0,
            order = 0,
            isCompleted = true,
            supersetKey = null,
            orderInSuperset = null,
            exercise = ServerExerciseInfo(id = "ex_adhoc", name = "Ad-hoc Exercise", equipment = null, videoUrl = null, description = null)
        )

        val sessionResponse = ServerLiveSessionResponse(
            id = "sess1",
            startTime = "2023-01-01T10:00:00Z",
            endTime = null,
            status = "active",
            notes = null,
            createdAt = null,
            updatedAt = null,
            workoutTemplateId = "temp1",
            plannedDate = null,
            client = null,
            workoutTemplate = template,
            exerciseLogs = listOf(log)
        )

        coEvery { api.getActiveSession() } returns ApiResponse(data = GetActiveSessionResponse(session = sessionResponse), success = true)

        val result = repository.getActiveSession()

        assertTrue(result.isSuccess)
        val uiModel = result.getOrNull()
        assertNotNull(uiModel)
        assertEquals(1, uiModel!!.exercises.size)
        assertEquals("Ad-hoc Exercise", uiModel.exercises[0].exerciseName)
        assertEquals(1, uiModel.exercises[0].sets.size)
        assertTrue(uiModel.exercises[0].sets[0].isCompleted)
    }

    @Test
    fun `logSet calls api correctly`() = runBlocking {
        coEvery { api.logSet(any()) } returns ApiResponse(data = Unit, success = true)

        val result = repository.logSet("sess1", "ex1", 10, 60.0, 0)

        assertTrue(result.isSuccess)
        coVerify { api.logSet(LogSetRequest("sess1", "ex1", 10, 60.0, 0)) }
    }

    @Test
    fun `cancelActiveWorkout calls api and returns success`() = runBlocking {
        coEvery { api.cancelActiveWorkout("sess1") } returns ApiResponse(data = Unit, success = true)

        val result = repository.cancelActiveWorkout("sess1")

        assertTrue(result.isSuccess)
        coVerify { api.cancelActiveWorkout("sess1") }
    }

    @Test
    fun `cancelActiveWorkout handles errors`() = runBlocking {
        coEvery { api.cancelActiveWorkout("sess1") } throws RuntimeException("Network Error")

        val result = repository.cancelActiveWorkout("sess1")

        assertTrue(result.isFailure)
        assertEquals("Network Error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getActiveSession absorbs REST steps into preceding exercise`() = runBlocking {
        // Template: Exercise A (rest 60), REST (duration 30), Exercise B (rest 45)
        val exerciseA = ServerTemplateExercise(
            id = "step1",
            exerciseId = "ex1",
            order = 0,
            targetSets = 3,
            targetReps = "10",
            restSeconds = 60,
            isRest = false,
            notes = null,
            exercise = ServerExerciseInfo(id = "ex1", name = "Bench Press", equipment = null, videoUrl = null, description = null)
        )
        val restStep = ServerTemplateExercise(
            id = "rest1",
            exerciseId = null,
            order = 1,
            targetSets = null,
            targetReps = null,
            restSeconds = null,
            isRest = true,
            durationSeconds = 30,
            notes = null,
            exercise = null
        )
        val exerciseB = ServerTemplateExercise(
            id = "step2",
            exerciseId = "ex2",
            order = 2,
            targetSets = 2,
            targetReps = "12",
            restSeconds = 45,
            isRest = false,
            notes = null,
            exercise = ServerExerciseInfo(id = "ex2", name = "Squats", equipment = null, videoUrl = null, description = null)
        )
        val template = ServerTemplate(id = "temp1", name = "Push Day", exercises = listOf(exerciseA, restStep, exerciseB))

        // Logs for exercises
        val logA = ServerExerciseLog(
            id = "log1", createdAt = null, reps = 10, weight = 60.0, order = 0,
            isCompleted = true, supersetKey = null, orderInSuperset = null,
            exercise = ServerExerciseInfo(id = "ex1", name = "Bench Press", equipment = null, videoUrl = null, description = null)
        )
        val logB = ServerExerciseLog(
            id = "log2", createdAt = null, reps = 12, weight = 80.0, order = 0,
            isCompleted = true, supersetKey = null, orderInSuperset = null,
            exercise = ServerExerciseInfo(id = "ex2", name = "Squats", equipment = null, videoUrl = null, description = null)
        )

        val sessionResponse = ServerLiveSessionResponse(
            id = "sess1", startTime = "2023-01-01T10:00:00Z", endTime = null, status = "active",
            notes = null, createdAt = null, updatedAt = null, workoutTemplateId = "temp1",
            plannedDate = null, client = null, workoutTemplate = template,
            exerciseLogs = listOf(logA, logB)
        )

        coEvery { api.getActiveSession() } returns ApiResponse(data = GetActiveSessionResponse(session = sessionResponse), success = true)

        val result = repository.getActiveSession()

        assertTrue(result.isSuccess)
        val uiModel = result.getOrNull()
        assertNotNull(uiModel)
        // Should have 2 exercises (REST step is skipped)
        assertEquals(2, uiModel!!.exercises.size)
        
        // First exercise should have absorbed REST: 60 + 30 = 90
        val ex1 = uiModel.exercises[0]
        assertEquals("Bench Press", ex1.exerciseName)
        assertEquals(90, ex1.restSeconds)
        
        // Second exercise should have its own rest: 45
        val ex2 = uiModel.exercises[1]
        assertEquals("Squats", ex2.exerciseName)
        assertEquals(45, ex2.restSeconds)
    }

    @Test
    fun `getActiveSession absorbs multiple consecutive REST steps`() = runBlocking {
        // Template: Exercise A (rest 60), REST (30), REST (20), Exercise B
        val exerciseA = ServerTemplateExercise(
            id = "step1", exerciseId = "ex1", order = 0, targetSets = 3, targetReps = "10",
            restSeconds = 60, isRest = false, notes = null,
            exercise = ServerExerciseInfo(id = "ex1", name = "Bench Press", equipment = null, videoUrl = null, description = null)
        )
        val rest1 = ServerTemplateExercise(
            id = "rest1", exerciseId = null, order = 1, targetSets = null, targetReps = null,
            restSeconds = null, isRest = true, durationSeconds = 30, notes = null, exercise = null
        )
        val rest2 = ServerTemplateExercise(
            id = "rest2", exerciseId = null, order = 2, targetSets = null, targetReps = null,
            restSeconds = null, isRest = true, durationSeconds = 20, notes = null, exercise = null
        )
        val exerciseB = ServerTemplateExercise(
            id = "step2", exerciseId = "ex2", order = 3, targetSets = 2, targetReps = "12",
            restSeconds = 45, isRest = false, notes = null,
            exercise = ServerExerciseInfo(id = "ex2", name = "Squats", equipment = null, videoUrl = null, description = null)
        )
        val template = ServerTemplate(id = "temp1", name = "Push Day", exercises = listOf(exerciseA, rest1, rest2, exerciseB))

        val logA = ServerExerciseLog(
            id = "log1", createdAt = null, reps = 10, weight = 60.0, order = 0,
            isCompleted = true, supersetKey = null, orderInSuperset = null,
            exercise = ServerExerciseInfo(id = "ex1", name = "Bench Press", equipment = null, videoUrl = null, description = null)
        )
        val logB = ServerExerciseLog(
            id = "log2", createdAt = null, reps = 12, weight = 80.0, order = 0,
            isCompleted = true, supersetKey = null, orderInSuperset = null,
            exercise = ServerExerciseInfo(id = "ex2", name = "Squats", equipment = null, videoUrl = null, description = null)
        )

        val sessionResponse = ServerLiveSessionResponse(
            id = "sess1", startTime = "2023-01-01T10:00:00Z", endTime = null, status = "active",
            notes = null, createdAt = null, updatedAt = null, workoutTemplateId = "temp1",
            plannedDate = null, client = null, workoutTemplate = template,
            exerciseLogs = listOf(logA, logB)
        )

        coEvery { api.getActiveSession() } returns ApiResponse(data = GetActiveSessionResponse(session = sessionResponse), success = true)

        val result = repository.getActiveSession()

        assertTrue(result.isSuccess)
        val uiModel = result.getOrNull()
        assertNotNull(uiModel)
        assertEquals(2, uiModel!!.exercises.size)
        
        // First exercise should absorb both REST steps: 60 + 30 + 20 = 110
        assertEquals(110, uiModel.exercises[0].restSeconds)
        // Second exercise keeps its own rest
        assertEquals(45, uiModel.exercises[1].restSeconds)
    }

    @Test
    fun `getActiveSession ignores REST as first step`() = runBlocking {
        // Template: REST (30), Exercise A (rest 60)
        val restStep = ServerTemplateExercise(
            id = "rest1", exerciseId = null, order = 0, targetSets = null, targetReps = null,
            restSeconds = null, isRest = true, durationSeconds = 30, notes = null, exercise = null
        )
        val exerciseA = ServerTemplateExercise(
            id = "step1", exerciseId = "ex1", order = 1, targetSets = 3, targetReps = "10",
            restSeconds = 60, isRest = false, notes = null,
            exercise = ServerExerciseInfo(id = "ex1", name = "Bench Press", equipment = null, videoUrl = null, description = null)
        )
        val template = ServerTemplate(id = "temp1", name = "Push Day", exercises = listOf(restStep, exerciseA))

        val logA = ServerExerciseLog(
            id = "log1", createdAt = null, reps = 10, weight = 60.0, order = 0,
            isCompleted = true, supersetKey = null, orderInSuperset = null,
            exercise = ServerExerciseInfo(id = "ex1", name = "Bench Press", equipment = null, videoUrl = null, description = null)
        )

        val sessionResponse = ServerLiveSessionResponse(
            id = "sess1", startTime = "2023-01-01T10:00:00Z", endTime = null, status = "active",
            notes = null, createdAt = null, updatedAt = null, workoutTemplateId = "temp1",
            plannedDate = null, client = null, workoutTemplate = template,
            exerciseLogs = listOf(logA)
        )

        coEvery { api.getActiveSession() } returns ApiResponse(data = GetActiveSessionResponse(session = sessionResponse), success = true)

        val result = repository.getActiveSession()

        assertTrue(result.isSuccess)
        val uiModel = result.getOrNull()
        assertNotNull(uiModel)
        // Should have 1 exercise (REST step is skipped)
        assertEquals(1, uiModel!!.exercises.size)
        // Exercise should have its own rest (60), not absorbed from preceding REST (which doesn't exist)
        assertEquals(60, uiModel.exercises[0].restSeconds)
    }

    @Test
    fun `getActiveSession exercise without following REST uses own restSeconds`() = runBlocking {
        // Template: Exercise A (rest 60), Exercise B (rest 45) - no REST steps
        val exerciseA = ServerTemplateExercise(
            id = "step1", exerciseId = "ex1", order = 0, targetSets = 3, targetReps = "10",
            restSeconds = 60, isRest = false, notes = null,
            exercise = ServerExerciseInfo(id = "ex1", name = "Bench Press", equipment = null, videoUrl = null, description = null)
        )
        val exerciseB = ServerTemplateExercise(
            id = "step2", exerciseId = "ex2", order = 1, targetSets = 2, targetReps = "12",
            restSeconds = 45, isRest = false, notes = null,
            exercise = ServerExerciseInfo(id = "ex2", name = "Squats", equipment = null, videoUrl = null, description = null)
        )
        val template = ServerTemplate(id = "temp1", name = "Push Day", exercises = listOf(exerciseA, exerciseB))

        val logA = ServerExerciseLog(
            id = "log1", createdAt = null, reps = 10, weight = 60.0, order = 0,
            isCompleted = true, supersetKey = null, orderInSuperset = null,
            exercise = ServerExerciseInfo(id = "ex1", name = "Bench Press", equipment = null, videoUrl = null, description = null)
        )
        val logB = ServerExerciseLog(
            id = "log2", createdAt = null, reps = 12, weight = 80.0, order = 0,
            isCompleted = true, supersetKey = null, orderInSuperset = null,
            exercise = ServerExerciseInfo(id = "ex2", name = "Squats", equipment = null, videoUrl = null, description = null)
        )

        val sessionResponse = ServerLiveSessionResponse(
            id = "sess1", startTime = "2023-01-01T10:00:00Z", endTime = null, status = "active",
            notes = null, createdAt = null, updatedAt = null, workoutTemplateId = "temp1",
            plannedDate = null, client = null, workoutTemplate = template,
            exerciseLogs = listOf(logA, logB)
        )

        coEvery { api.getActiveSession() } returns ApiResponse(data = GetActiveSessionResponse(session = sessionResponse), success = true)

        val result = repository.getActiveSession()

        assertTrue(result.isSuccess)
        val uiModel = result.getOrNull()
        assertNotNull(uiModel)
        assertEquals(2, uiModel!!.exercises.size)
        assertEquals(60, uiModel.exercises[0].restSeconds)
        assertEquals(45, uiModel.exercises[1].restSeconds)
    }
}
