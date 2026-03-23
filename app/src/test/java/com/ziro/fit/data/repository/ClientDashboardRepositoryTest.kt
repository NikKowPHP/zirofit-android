package com.ziro.fit.data.repository

import android.util.Log
import com.ziro.fit.data.remote.ZiroApi
import com.ziro.fit.model.*
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ClientDashboardRepositoryTest {
    private val api: ZiroApi = mockk()
    private val repository = ClientDashboardRepository(api)

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.e(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>(), any<Throwable>()) } returns 0
    }

    @Test
    fun `zero volumeHistory entries shows log more message`() = runBlocking {
        val responseData = ClientProgressResponse(
            volumeHistory = emptyList(),
            exercisePerformance = null
        )
        coEvery { api.getClientProgress() } returns ApiResponse(data = responseData, success = true)

        val result = repository.getClientProgress()

        assertTrue(result.isSuccess)
        assertEquals("Log more workouts to see insights.", result.getOrNull()!!.insightsMessage)
    }

    @Test
    fun `one volumeHistory entry shows log more message`() = runBlocking {
        val responseData = ClientProgressResponse(
            volumeHistory = listOf(
                VolumeDataPoint(date = "2024-01-01", totalVolume = 5000.0)
            ),
            exercisePerformance = null
        )
        coEvery { api.getClientProgress() } returns ApiResponse(data = responseData, success = true)

        val result = repository.getClientProgress()

        assertTrue(result.isSuccess)
        assertEquals("Log more workouts to see insights.", result.getOrNull()!!.insightsMessage)
    }

    @Test
    fun `two entries with current greater than previous shows more volume message`() = runBlocking {
        val responseData = ClientProgressResponse(
            volumeHistory = listOf(
                VolumeDataPoint(date = "2024-01-01", totalVolume = 5000.0),
                VolumeDataPoint(date = "2024-01-02", totalVolume = 7500.0)
            ),
            exercisePerformance = null
        )
        coEvery { api.getClientProgress() } returns ApiResponse(data = responseData, success = true)

        val result = repository.getClientProgress()

        assertTrue(result.isSuccess)
        assertEquals("You lifted 2500 kg more than last time!", result.getOrNull()!!.insightsMessage)
    }

    @Test
    fun `two entries with current less than previous shows less volume message`() = runBlocking {
        val responseData = ClientProgressResponse(
            volumeHistory = listOf(
                VolumeDataPoint(date = "2024-01-01", totalVolume = 8000.0),
                VolumeDataPoint(date = "2024-01-02", totalVolume = 6000.0)
            ),
            exercisePerformance = null
        )
        coEvery { api.getClientProgress() } returns ApiResponse(data = responseData, success = true)

        val result = repository.getClientProgress()

        assertTrue(result.isSuccess)
        assertEquals("You lifted 2000 kg less than last time.", result.getOrNull()!!.insightsMessage)
    }

    @Test
    fun `two entries with current equal to previous shows same volume message`() = runBlocking {
        val responseData = ClientProgressResponse(
            volumeHistory = listOf(
                VolumeDataPoint(date = "2024-01-01", totalVolume = 5000.0),
                VolumeDataPoint(date = "2024-01-02", totalVolume = 5000.0)
            ),
            exercisePerformance = null
        )
        coEvery { api.getClientProgress() } returns ApiResponse(data = responseData, success = true)

        val result = repository.getClientProgress()

        assertTrue(result.isSuccess)
        assertEquals("Same volume as last session.", result.getOrNull()!!.insightsMessage)
    }

    @Test
    fun `more than two entries uses last 2 entries for comparison`() = runBlocking {
        val responseData = ClientProgressResponse(
            volumeHistory = listOf(
                VolumeDataPoint(date = "2024-01-01", totalVolume = 3000.0),
                VolumeDataPoint(date = "2024-01-02", totalVolume = 4000.0),
                VolumeDataPoint(date = "2024-01-03", totalVolume = 6000.0),
                VolumeDataPoint(date = "2024-01-04", totalVolume = 9000.0)
            ),
            exercisePerformance = null
        )
        coEvery { api.getClientProgress() } returns ApiResponse(data = responseData, success = true)

        val result = repository.getClientProgress()

        assertTrue(result.isSuccess)
        assertEquals("You lifted 3000 kg more than last time!", result.getOrNull()!!.insightsMessage)
    }

    @Test
    fun `unsorted entries are sorted by date before taking last 2`() = runBlocking {
        val responseData = ClientProgressResponse(
            volumeHistory = listOf(
                VolumeDataPoint(date = "2024-01-03", totalVolume = 7000.0),
                VolumeDataPoint(date = "2024-01-01", totalVolume = 3000.0),
                VolumeDataPoint(date = "2024-01-04", totalVolume = 10000.0),
                VolumeDataPoint(date = "2024-01-02", totalVolume = 5000.0)
            ),
            exercisePerformance = null
        )
        coEvery { api.getClientProgress() } returns ApiResponse(data = responseData, success = true)

        val result = repository.getClientProgress()

        assertTrue(result.isSuccess)
        assertEquals("You lifted 3000 kg more than last time!", result.getOrNull()!!.insightsMessage)
    }
}
