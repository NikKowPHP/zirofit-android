package com.ziro.fit.data.repository

import android.util.Log
import com.ziro.fit.data.remote.ZiroApi
import com.ziro.fit.model.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class TrainerRepositoryTest {
    private val api: ZiroApi = mockk()
    private val repository = TrainerRepository(api)

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.e(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>(), any<Throwable>()) } returns 0
    }

    @Test
    fun `linkTrainer success returns message and emits to linkEvents Flow`() = runBlocking {
        val username = "trainer1"
        val message = "Successfully linked with trainer"
        val response = ApiResponse(
            data = LinkActionResponse(message = message),
            success = true
        )
        coEvery { api.linkTrainer(LinkTrainerRequest(trainerUsername = username)) } returns response

        val result = repository.linkTrainer(username)

        assertTrue(result.isSuccess)
        assertEquals(message, result.getOrNull())
        val linkEvent = repository.linkEvents.first()
        assertNotNull(linkEvent)
    }

    @Test
    fun `linkTrainer API failure returns Result failure`() = runBlocking {
        val username = "trainer1"
        coEvery { api.linkTrainer(LinkTrainerRequest(trainerUsername = username)) } throws RuntimeException("Network Error")

        val result = repository.linkTrainer(username)

        assertTrue(result.isFailure)
    }

    @Test
    fun `linkTrainer API returns success false returns Result failure`() = runBlocking {
        val username = "trainer1"
        val response = ApiResponse<LinkActionResponse>(
            data = null,
            success = false,
            message = "Failed to link with trainer"
        )
        coEvery { api.linkTrainer(LinkTrainerRequest(trainerUsername = username)) } returns response

        val result = repository.linkTrainer(username)

        assertTrue(result.isFailure)
        assertEquals("Failed to link with trainer", result.exceptionOrNull()?.message)
    }

    @Test
    fun `unlinkTrainer success returns message and emits to linkEvents Flow`() = runBlocking {
        val message = "Successfully unlinked from trainer"
        val response = ApiResponse(
            data = LinkActionResponse(message = message),
            success = true
        )
        coEvery { api.unlinkTrainer() } returns response

        val result = repository.unlinkTrainer()

        assertTrue(result.isSuccess)
        assertEquals(message, result.getOrNull())
        val linkEvent = repository.linkEvents.first()
        assertNotNull(linkEvent)
    }

    @Test
    fun `unlinkTrainer API failure returns Result failure`() = runBlocking {
        coEvery { api.unlinkTrainer() } throws RuntimeException("Network Error")

        val result = repository.unlinkTrainer()

        assertTrue(result.isFailure)
    }

    @Test
    fun `unlinkTrainer API returns success false returns Result failure`() = runBlocking {
        val response = ApiResponse<LinkActionResponse>(
            data = null,
            success = false,
            message = "Failed to unlink from trainer"
        )
        coEvery { api.unlinkTrainer() } returns response

        val result = repository.unlinkTrainer()

        assertTrue(result.isFailure)
        assertEquals("Failed to unlink from trainer", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getLinkedTrainer returns linked trainer when data exists`() = runBlocking {
        val linkedTrainer = LinkedTrainer(
            id = "trainer1",
            name = "John Trainer",
            email = "john@ziro.fit",
            profile = LinkedTrainerProfile(
                profilePhotoPath = "https://example.com/photo.jpg",
                aboutMe = "Fitness expert"
            )
        )
        val response = ApiResponse(
            data = LinkedTrainerResponse(trainer = linkedTrainer),
            success = true
        )
        coEvery { api.getLinkedTrainer() } returns response

        val result = repository.getLinkedTrainer()

        assertTrue(result.isSuccess)
        assertEquals(linkedTrainer, result.getOrNull())
    }

    @Test
    fun `getLinkedTrainer returns null trainer when no trainer linked`() = runBlocking {
        val response = ApiResponse(
            data = LinkedTrainerResponse(trainer = null),
            success = true
        )
        coEvery { api.getLinkedTrainer() } returns response

        val result = repository.getLinkedTrainer()

        assertTrue(result.isSuccess)
        assertNull(result.getOrNull())
    }

    @Test
    fun `getLinkedTrainer API failure returns Result failure`() = runBlocking {
        coEvery { api.getLinkedTrainer() } throws RuntimeException("Network Error")

        val result = repository.getLinkedTrainer()

        assertTrue(result.isFailure)
    }

    @Test
    fun `linkEvents Flow emits Unit on linkTrainer success`() = runBlocking {
        val username = "trainer1"
        val response = ApiResponse(
            data = LinkActionResponse(message = "Linked"),
            success = true
        )
        coEvery { api.linkTrainer(LinkTrainerRequest(trainerUsername = username)) } returns response

        repository.linkTrainer(username)
        val emittedEvent = repository.linkEvents.first()

        assertEquals(Unit, emittedEvent)
    }

    @Test
    fun `linkEvents Flow emits Unit on unlinkTrainer success`() = runBlocking {
        val response = ApiResponse(
            data = LinkActionResponse(message = "Unlinked"),
            success = true
        )
        coEvery { api.unlinkTrainer() } returns response

        repository.unlinkTrainer()
        val emittedEvent = repository.linkEvents.first()

        assertEquals(Unit, emittedEvent)
    }
}
