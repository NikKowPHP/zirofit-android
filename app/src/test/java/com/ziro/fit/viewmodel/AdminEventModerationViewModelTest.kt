package com.ziro.fit.viewmodel

import android.util.Log
import com.ziro.fit.data.remote.ZiroApi
import com.ziro.fit.model.*
import com.ziro.fit.util.MainDispatcherRule
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AdminEventModerationViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val api: ZiroApi = mockk(relaxed = true)

    private fun createPendingEvent(
        id: String = "evt1",
        title: String = "Yoga Class"
    ) = PendingEvent(
        id = id,
        title = title,
        description = "Relaxing yoga session",
        startTime = "2026-03-25T10:00:00Z",
        endTime = "2026-03-25T11:00:00Z",
        price = 0.0,
        maxParticipants = 20,
        location = "Yoga Studio",
        category = "fitness",
        status = "pending",
        trainer = EventTrainerSummary(
            id = "trainer1",
            name = "Jane Doe",
            profilePhotoPath = null
        )
    )

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.e(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>(), any<Throwable>()) } returns 0
    }

    @Test
    fun `init calls loadPendingEvents`() = runTest {
        val response = ApiResponse(
            success = true,
            data = AdminEventsResponse(events = emptyList()),
            message = null
        )
        coEvery { api.getPendingEvents() } returns response

        val viewModel = AdminEventModerationViewModel(api)
        advanceUntilIdle()

        coVerify { api.getPendingEvents() }
    }

    @Test
    fun `loadPendingEvents success updates uiState to Success and populates events`() = runTest {
        val event = createPendingEvent(id = "evt1", title = "First Event")
        val response = ApiResponse(
            success = true,
            data = AdminEventsResponse(events = listOf(event)),
            message = null
        )
        coEvery { api.getPendingEvents() } returns response

        val viewModel = AdminEventModerationViewModel(api)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is AdminEventsUiState.Success)
        val successState = viewModel.uiState.value as AdminEventsUiState.Success
        assertEquals(1, successState.events.size)
        assertEquals("First Event", successState.events.first().title)
    }

    @Test
    fun `loadPendingEvents failure updates uiState to Error`() = runTest {
        coEvery { api.getPendingEvents() } returns ApiResponse(
            success = false,
            data = null,
            message = "Network Error"
        )

        val viewModel = AdminEventModerationViewModel(api)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is AdminEventsUiState.Error)
        val errorState = viewModel.uiState.value as AdminEventsUiState.Error
        assertEquals("Network Error", errorState.message)
    }

    @Test
    fun `selectEvent sets selectedEvent`() = runTest {
        val response = ApiResponse(
            success = true,
            data = AdminEventsResponse(events = emptyList()),
            message = null
        )
        coEvery { api.getPendingEvents() } returns response

        val viewModel = AdminEventModerationViewModel(api)
        advanceUntilIdle()

        val event = createPendingEvent()
        viewModel.selectEvent(event)
        advanceUntilIdle()

        assertNotNull(viewModel.selectedEvent.value)
        assertEquals("evt1", viewModel.selectedEvent.value?.id)
    }

    @Test
    fun `clearSelection sets selectedEvent to null`() = runTest {
        val response = ApiResponse(
            success = true,
            data = AdminEventsResponse(events = emptyList()),
            message = null
        )
        coEvery { api.getPendingEvents() } returns response

        val viewModel = AdminEventModerationViewModel(api)
        advanceUntilIdle()

        val event = createPendingEvent()
        viewModel.selectEvent(event)
        advanceUntilIdle()

        assertNotNull(viewModel.selectedEvent.value)

        viewModel.clearSelection()
        advanceUntilIdle()

        assertNull(viewModel.selectedEvent.value)
    }

    @Test
    fun `approveEvent success removes event from list clears selection and sets isSuccess`() = runTest {
        val event1 = createPendingEvent(id = "evt1", title = "Event 1")
        val event2 = createPendingEvent(id = "evt2", title = "Event 2")
        val response = ApiResponse(
            success = true,
            data = AdminEventsResponse(events = listOf(event1, event2)),
            message = null
        )
        coEvery { api.getPendingEvents() } returns response

        val viewModel = AdminEventModerationViewModel(api)
        advanceUntilIdle()

        viewModel.selectEvent(event1)
        advanceUntilIdle()

        val moderationResponse = ApiResponse(
            success = true,
            data = EventModerationUpdateResponse(event = event1),
            message = null
        )
        coEvery { api.moderateEvent(eq("evt1"), any()) } returns moderationResponse

        viewModel.approveEvent("evt1")
        advanceUntilIdle()

        assertFalse(viewModel.events.value.any { it.id == "evt1" })
        assertNull(viewModel.selectedEvent.value)
        assertTrue(viewModel.isSuccess.value)
    }

    @Test
    fun `approveEvent failure sets actionError and resets isActionLoading`() = runTest {
        val event = createPendingEvent()
        val response = ApiResponse(
            success = true,
            data = AdminEventsResponse(events = listOf(event)),
            message = null
        )
        coEvery { api.getPendingEvents() } returns response

        val viewModel = AdminEventModerationViewModel(api)
        advanceUntilIdle()

        val moderationResponse: ApiResponse<EventModerationUpdateResponse> = mockk()
        coEvery { moderationResponse.success } returns false
        coEvery { moderationResponse.message } returns "Failed to approve"
        coEvery { moderationResponse.data } returns null
        coEvery { api.moderateEvent(eq("evt1"), any()) } returns moderationResponse

        viewModel.approveEvent("evt1")
        advanceUntilIdle()

        assertEquals("Failed to approve", viewModel.actionError.value)
        assertFalse(viewModel.isActionLoading.value)
    }

    @Test
    fun `rejectEvent success removes event from list clears selection and sets isSuccess`() = runTest {
        val event1 = createPendingEvent(id = "evt1", title = "Event 1")
        val event2 = createPendingEvent(id = "evt2", title = "Event 2")
        val response = ApiResponse(
            success = true,
            data = AdminEventsResponse(events = listOf(event1, event2)),
            message = null
        )
        coEvery { api.getPendingEvents() } returns response

        val viewModel = AdminEventModerationViewModel(api)
        advanceUntilIdle()

        viewModel.selectEvent(event1)
        advanceUntilIdle()

        val moderationResponse = ApiResponse(
            success = true,
            data = EventModerationUpdateResponse(event = event1),
            message = null
        )
        coEvery { api.moderateEvent(eq("evt1"), any()) } returns moderationResponse

        viewModel.rejectEvent("evt1", "Inappropriate content")
        advanceUntilIdle()

        assertFalse(viewModel.events.value.any { it.id == "evt1" })
        assertNull(viewModel.selectedEvent.value)
        assertTrue(viewModel.isSuccess.value)
    }

    @Test
    fun `rejectEvent failure sets actionError and resets isActionLoading`() = runTest {
        val event = createPendingEvent()
        val response = ApiResponse(
            success = true,
            data = AdminEventsResponse(events = listOf(event)),
            message = null
        )
        coEvery { api.getPendingEvents() } returns response

        val viewModel = AdminEventModerationViewModel(api)
        advanceUntilIdle()

        val moderationResponse: ApiResponse<EventModerationUpdateResponse> = mockk()
        coEvery { moderationResponse.success } returns false
        coEvery { moderationResponse.message } returns "Failed to reject"
        coEvery { moderationResponse.data } returns null
        coEvery { api.moderateEvent(eq("evt1"), any()) } returns moderationResponse

        viewModel.rejectEvent("evt1", "Test reason")
        advanceUntilIdle()

        assertEquals("Failed to reject", viewModel.actionError.value)
        assertFalse(viewModel.isActionLoading.value)
    }

    @Test
    fun `clearActionError sets actionError to null`() = runTest {
        val response = ApiResponse(
            success = true,
            data = AdminEventsResponse(events = emptyList()),
            message = null
        )
        coEvery { api.getPendingEvents() } returns response

        val viewModel = AdminEventModerationViewModel(api)
        advanceUntilIdle()

        viewModel.approveEvent("evt1")
        advanceUntilIdle()

        val reflectionAccess = viewModel.javaClass.getDeclaredMethod("clearActionError")
        reflectionAccess.invoke(viewModel)

        assertNull(viewModel.actionError.value)
    }

    @Test
    fun `clearSuccess sets isSuccess to false`() = runTest {
        val response = ApiResponse(
            success = true,
            data = AdminEventsResponse(events = emptyList()),
            message = null
        )
        coEvery { api.getPendingEvents() } returns response

        val viewModel = AdminEventModerationViewModel(api)
        advanceUntilIdle()

        viewModel.clearSuccess()

        assertFalse(viewModel.isSuccess.value)
    }
}
