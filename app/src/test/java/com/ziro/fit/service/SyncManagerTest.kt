package com.ziro.fit.service

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import com.google.gson.Gson
import com.ziro.fit.data.remote.ZiroApi
import com.ziro.fit.model.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class SyncManagerTest {
    private val mockContext = mockk<Context>(relaxed = true)
    private val mockApi = mockk<ZiroApi>(relaxed = true)
    private val mockPrefs = mockk<SharedPreferences>(relaxed = true)
    private val mockConnectivityManager = mockk<ConnectivityManager>(relaxed = true)
    private val mockNetworkCapabilities = mockk<NetworkCapabilities>(relaxed = true)
    private val mockNetworkRequest = mockk<NetworkRequest>(relaxed = true)
    private val mockNetworkRequestBuilder = mockk<NetworkRequest.Builder>(relaxed = true)

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
        mockkConstructor(NetworkRequest.Builder::class)
        every { anyConstructed<NetworkRequest.Builder>().addCapability(any()) } returns mockNetworkRequestBuilder
        every { anyConstructed<NetworkRequest.Builder>().build() } returns mockNetworkRequest

        every { mockContext.getSharedPreferences(any<String>(), any()) } returns mockPrefs
        every { mockContext.getSystemService(Context.CONNECTIVITY_SERVICE) } returns mockConnectivityManager
        every { mockPrefs.getString(any<String>(), any()) } returns null
        every { mockPrefs.edit() } returns mockk(relaxed = true)
        every { mockConnectivityManager.activeNetwork } returns null
        every { mockConnectivityManager.getNetworkCapabilities(null) } returns mockNetworkCapabilities
        every { mockNetworkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) } returns false

        manager = SyncManager(mockContext, mockApi)
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

    @Test
    fun `SyncManager isOnline starts as false when no network`() {
        assertFalse(manager.isOnline.value)
    }
}
