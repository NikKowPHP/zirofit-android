package com.ziro.fit.service

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.ziro.fit.data.remote.ZiroApi
import com.ziro.fit.model.*
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class SyncManagerTest {
    private val mockContext = mockk<Context>(relaxed = true)
    private val mockApi = mockk<ZiroApi>(relaxed = true)
    private val mockPrefs = mockk<SharedPreferences>(relaxed = true)

    private lateinit var manager: SyncManager
    private lateinit var gson: Gson

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.e(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>(), any<Throwable>()) } returns 0
        every { Log.w(any<String>(), any<String>()) } returns 0
        every { Log.d(any<String>(), any<String>()) } returns 0

        gson = Gson()
        every { mockContext.getSharedPreferences(any<String>(), any()) } returns mockPrefs
        every { mockPrefs.getString(any<String>(), any()) } returns null
        every { mockPrefs.edit() } returns mockk(relaxed = true)

        manager = SyncManager(mockContext, mockApi)
    }

    private fun performActionReflectively(action: SyncAction): Boolean {
        val method = SyncManager::class.java.getDeclaredMethod("performAction", SyncAction::class.java)
        method.isAccessible = true
        return method.invoke(manager, action) as Boolean
    }

    // --- Data class tests

    @Test
    fun `SyncAction data class properties`() {
        val action = SyncAction("id1", SyncActionType.LOG_SET, "{}", 12345L)
        assertEquals("id1", action.id)
        assertEquals(SyncActionType.LOG_SET, action.type)
        assertEquals("{}", action.payload)
        assertEquals(12345L, action.createdAt)
    }

    @Test
    fun `LogSetPayload data class properties`() {
        val payload = LogSetPayload(
            workoutSessionId = "sess1",
            exerciseId = "ex1",
            reps = 10,
            weight = 60.0,
            rpe = 8.0,
            order = 0,
            isCompleted = true,
            logId = "log1"
        )
        assertEquals("sess1", payload.workoutSessionId)
        assertEquals("ex1", payload.exerciseId)
        assertEquals(10, payload.reps)
        assertEquals(60.0, payload.weight, 0.001)
        assertEquals(8.0, payload.rpe as Double, 0.001)
        assertEquals(0, payload.order)
        assertTrue(payload.isCompleted)
        assertEquals("log1", payload.logId)
    }

    @Test
    fun `FinishWorkoutPayload data class properties`() {
        val payload = FinishWorkoutPayload(sessionId = "sess1", notes = "Great workout")
        assertEquals("sess1", payload.sessionId)
        assertEquals("Great workout", payload.notes)
    }

    @Test
    fun `SyncActionType enum has LOG_SET and FINISH_WORKOUT`() {
        assertEquals(2, SyncActionType.entries.size)
        assertEquals(SyncActionType.LOG_SET, SyncActionType.valueOf("LOG_SET"))
        assertEquals(SyncActionType.FINISH_WORKOUT, SyncActionType.valueOf("FINISH_WORKOUT"))
    }

    // --- performAction tests

    @Test
    fun `performAction LOG_SET success returns true`() = runBlocking {
        val payload = LogSetPayload("sess1", "ex1", 10, 60.0, null, 0, true, "log1")
        val action = SyncAction("act1", SyncActionType.LOG_SET, gson.toJson(payload), System.currentTimeMillis())

        coEvery { mockApi.logSet(any()) } returns ApiResponse(data = Unit, success = true)

        val result = performActionReflectively(action)
        assertTrue(result)
    }

    @Test
    fun `performAction FINISH_WORKOUT success returns true`() = runBlocking {
        val payload = FinishWorkoutPayload("sess1", "Done")
        val action = SyncAction("act1", SyncActionType.FINISH_WORKOUT, gson.toJson(payload), System.currentTimeMillis())

        coEvery { mockApi.finishWorkout(any()) } returns ApiResponse(data = FinishWorkoutResponse(mockk(), null), success = true)

        val result = performActionReflectively(action)
        assertTrue(result)
    }

    @Test
    fun `performAction LOG_SET 404 returns true for self-healing`() = runBlocking {
        val payload = LogSetPayload("sess1", "ex1", 10, 60.0, null, 0, true, "log1")
        val action = SyncAction("act1", SyncActionType.LOG_SET, gson.toJson(payload), System.currentTimeMillis())

        val errorResponse = Response.error<Any>(404, "{}".toResponseBody("application/json".toMediaTypeOrNull()))
        coEvery { mockApi.logSet(any()) } throws HttpException(errorResponse)

        val result = performActionReflectively(action)
        assertTrue(result)
    }

    @Test
    fun `performAction FINISH_WORKOUT 404 returns true for self-healing`() = runBlocking {
        val payload = FinishWorkoutPayload("sess1", null)
        val action = SyncAction("act1", SyncActionType.FINISH_WORKOUT, gson.toJson(payload), System.currentTimeMillis())

        val errorResponse = Response.error<Any>(404, "{}".toResponseBody("application/json".toMediaTypeOrNull()))
        coEvery { mockApi.finishWorkout(any()) } throws HttpException(errorResponse)

        val result = performActionReflectively(action)
        assertTrue(result)
    }

    @Test
    fun `performAction LOG_SET session_not_found message returns true`() = runBlocking {
        val payload = LogSetPayload("sess1", "ex1", 10, 60.0, null, 0, true, "log1")
        val action = SyncAction("act1", SyncActionType.LOG_SET, gson.toJson(payload), System.currentTimeMillis())

        val json = """{"message": "session_not_found"}"""
        val errorResponse = Response.error<Any>(400, json.toResponseBody("application/json".toMediaTypeOrNull()))
        coEvery { mockApi.logSet(any()) } throws HttpException(errorResponse)

        val result = performActionReflectively(action)
        assertTrue(result)
    }

    @Test
    fun `performAction LOG_SET Session not found capitalized returns true`() = runBlocking {
        val payload = LogSetPayload("sess1", "ex1", 10, 60.0, null, 0, true, "log1")
        val action = SyncAction("act1", SyncActionType.LOG_SET, gson.toJson(payload), System.currentTimeMillis())

        val json = """{"message": "Session not found"}"""
        val errorResponse = Response.error<Any>(400, json.toResponseBody("application/json".toMediaTypeOrNull()))
        coEvery { mockApi.logSet(any()) } throws HttpException(errorResponse)

        val result = performActionReflectively(action)
        assertTrue(result)
    }

    @Test
    fun `performAction LOG_SET other exception returns false`() = runBlocking {
        val payload = LogSetPayload("sess1", "ex1", 10, 60.0, null, 0, true, "log1")
        val action = SyncAction("act1", SyncActionType.LOG_SET, gson.toJson(payload), System.currentTimeMillis())

        coEvery { mockApi.logSet(any()) } throws IOException("Network error")

        val result = performActionReflectively(action)
        assertFalse(result)
    }

    @Test
    fun `performAction FINISH_WORKOUT other exception returns false`() = runBlocking {
        val payload = FinishWorkoutPayload("sess1", null)
        val action = SyncAction("act1", SyncActionType.FINISH_WORKOUT, gson.toJson(payload), System.currentTimeMillis())

        coEvery { mockApi.finishWorkout(any()) } throws IOException("Network error")

        val result = performActionReflectively(action)
        assertFalse(result)
    }

    @Test
    fun `performAction FINISH_WORKOUT session_not_found message returns true`() = runBlocking {
        val payload = FinishWorkoutPayload("sess1", null)
        val action = SyncAction("act1", SyncActionType.FINISH_WORKOUT, gson.toJson(payload), System.currentTimeMillis())

        val json = """{"message": "session_not_found"}"""
        val errorResponse = Response.error<Any>(400, json.toResponseBody("application/json".toMediaTypeOrNull()))
        coEvery { mockApi.finishWorkout(any()) } throws HttpException(errorResponse)

        val result = performActionReflectively(action)
        assertTrue(result)
    }
}
