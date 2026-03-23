package com.ziro.fit.viewmodel

import android.util.Log
import com.ziro.fit.data.repository.ExploreRepository
import com.ziro.fit.model.*
import com.ziro.fit.util.MainDispatcherRule
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EventsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: ExploreRepository = mockk(relaxed = true)

    private fun createEvent(
        id: String = "evt1",
        title: String = "Yoga Class"
    ) = ExploreEvent(
        id = id,
        title = title,
        description = "Relaxing yoga session",
        startTime = "2026-03-25T10:00:00Z",
        endTime = "2026-03-25T11:00:00Z",
        price = 0.0,
        currency = "PLN",
        locationName = "Yoga Studio",
        address = "123 Main St",
        latitude = 52.0,
        longitude = 21.0,
        imageUrl = null,
        categoryId = "cat1",
        cityId = "city1",
        priceDisplay = "Free",
        hostName = null,
        hostId = null,
        trainerName = "Jane Doe",
        trainerId = "t1",
        enrolledCount = 5,
        capacity = 20,
        isBooked = false,
        isNearCapacity = false,
        trainer = null
    )

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.e(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>(), any<Throwable>()) } returns 0
    }

    @Test
    fun `init loads events automatically`() = runTest {
        val response = ExploreEventsResponse(
            events = listOf(createEvent()),
            pagination = PaginationData(total = 1, page = 1, hasMore = false)
        )
        coEvery { repository.getEvents(any(), any(), any(), any(), any()) } returns Result.success(response)

        val viewModel = EventsViewModel(repository)
        advanceUntilIdle()

        coVerify { repository.getEvents(any(), any(), any(), any(), any()) }
        assertEquals(1, viewModel.uiState.value.events.size)
    }

    @Test
    fun `loadEvents success updates events list in state`() = runTest {
        val response = ExploreEventsResponse(
            events = listOf(createEvent(id = "evt1", title = "First Event")),
            pagination = PaginationData(total = 1, page = 1, hasMore = false)
        )
        coEvery { repository.getEvents(any(), any(), any(), any(), any()) } returns Result.success(response)

        val viewModel = EventsViewModel(repository)
        advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.events.size)
        assertEquals("First Event", viewModel.uiState.value.events.first().title)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `loadEvents failure updates error state`() = runTest {
        coEvery { repository.getEvents(any(), any(), any(), any(), any()) } returns
            Result.failure(RuntimeException("Network Error"))

        val viewModel = EventsViewModel(repository)
        advanceUntilIdle()

        assertEquals("Network Error", viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `loadEvents with refresh true resets page to 1 and clears events first`() = runTest {
        val firstResponse = ExploreEventsResponse(
            events = listOf(createEvent(id = "evt1")),
            pagination = PaginationData(total = 2, page = 1, hasMore = true)
        )
        val secondResponse = ExploreEventsResponse(
            events = listOf(createEvent(id = "evt2")),
            pagination = PaginationData(total = 2, page = 2, hasMore = false)
        )
        coEvery { repository.getEvents(any(), any(), any(), any(), any()) } returnsMany
            listOf(Result.success(firstResponse), Result.success(secondResponse))

        val viewModel = EventsViewModel(repository)
        advanceUntilIdle()

        coEvery { repository.getEvents(any(), any(), any(), any(), any()) } returns
            Result.success(secondResponse)
        viewModel.loadEvents(refresh = true)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.events.isEmpty())
    }

    @Test
    fun `loadEvents appends events when not refreshing (pagination)`() = runTest {
        val firstPage = ExploreEventsResponse(
            events = listOf(createEvent(id = "evt1")),
            pagination = PaginationData(total = 2, page = 1, hasMore = true)
        )
        val secondPage = ExploreEventsResponse(
            events = listOf(createEvent(id = "evt2")),
            pagination = PaginationData(total = 2, page = 2, hasMore = false)
        )
        coEvery { repository.getEvents(any(), any(), any(), any(), any()) } returnsMany
            listOf(Result.success(firstPage), Result.success(secondPage))

        val viewModel = EventsViewModel(repository)
        advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.events.size)

        viewModel.loadEvents(refresh = false)
        advanceUntilIdle()

        assertEquals(2, viewModel.uiState.value.events.size)
    }

    @Test
    fun `loadEvents updates hasMore from pagination response`() = runTest {
        val response = ExploreEventsResponse(
            events = listOf(createEvent()),
            pagination = PaginationData(total = 100, page = 1, hasMore = true)
        )
        coEvery { repository.getEvents(any(), any(), any(), any(), any()) } returns Result.success(response)

        val viewModel = EventsViewModel(repository)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.hasMore)
    }

    @Test
    fun `onSearchQueryChanged triggers loadEvents with new query`() = runTest {
        val response = ExploreEventsResponse(
            events = emptyList(),
            pagination = PaginationData(total = 0, page = 1, hasMore = false)
        )
        coEvery { repository.getEvents(any(), any(), any(), any(), any()) } returns Result.success(response)

        val viewModel = EventsViewModel(repository)
        advanceUntilIdle()

        viewModel.onSearchQueryChanged("yoga")
        advanceUntilIdle()

        assertEquals("yoga", viewModel.uiState.value.searchQuery)
        coVerify {
            repository.getEvents(
                page = any(),
                categoryId = any(),
                search = eq("yoga"),
                isFree = any()
            )
        }
    }

    @Test
    fun `onCategorySelected triggers loadEvents with category`() = runTest {
        val response = ExploreEventsResponse(
            events = emptyList(),
            pagination = PaginationData(total = 0, page = 1, hasMore = false)
        )
        coEvery { repository.getEvents(any(), any(), any(), any(), any()) } returns Result.success(response)

        val viewModel = EventsViewModel(repository)
        advanceUntilIdle()

        viewModel.onCategorySelected("cat1")
        advanceUntilIdle()

        assertEquals("cat1", viewModel.uiState.value.selectedCategory)
        coVerify {
            repository.getEvents(
                page = any(),
                categoryId = eq("cat1"),
                search = any(),
                isFree = any()
            )
        }
    }

    @Test
    fun `onFilterFree triggers loadEvents with free filter`() = runTest {
        val response = ExploreEventsResponse(
            events = emptyList(),
            pagination = PaginationData(total = 0, page = 1, hasMore = false)
        )
        coEvery { repository.getEvents(any(), any(), any(), any(), any()) } returns Result.success(response)

        val viewModel = EventsViewModel(repository)
        advanceUntilIdle()

        viewModel.onFilterFree(true)
        advanceUntilIdle()

        assertEquals(true, viewModel.uiState.value.isFreeOnly)
        coVerify {
            repository.getEvents(
                page = any(),
                categoryId = any(),
                search = any(),
                isFree = eq(true)
            )
        }
    }

    @Test
    fun `clearError sets error to null`() = runTest {
        coEvery { repository.getEvents(any(), any(), any(), any(), any()) } returns
            Result.failure(RuntimeException("Network Error"))

        val viewModel = EventsViewModel(repository)
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.error)

        viewModel.clearError()

        assertNull(viewModel.uiState.value.error)
    }
}
