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

class ExploreRepositoryTest {
    private val api: ZiroApi = mockk(relaxed = true)
    private val repository = ExploreRepository(api)

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.e(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>(), any<Throwable>()) } returns 0
    }

    private fun createEvent(
        id: String = "evt1",
        title: String = "Yoga Class",
        price: Double? = 0.0,
        priceDisplay: String? = "Free"
    ) = ExploreEvent(
        id = id,
        title = title,
        description = "Relaxing yoga session",
        startTime = "2026-03-25T10:00:00Z",
        endTime = "2026-03-25T11:00:00Z",
        price = price,
        currency = "PLN",
        locationName = "Yoga Studio",
        address = "123 Main St",
        latitude = 52.0,
        longitude = 21.0,
        imageUrl = null,
        categoryId = "cat1",
        cityId = "city1",
        priceDisplay = priceDisplay,
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

    @Test
    fun `getEvents success returns events response with pagination`() = runBlocking {
        val event = createEvent()
        val response = ExploreEventsResponse(
            events = listOf(event),
            pagination = PaginationData(total = 1, page = 1, hasMore = false)
        )
        coEvery { api.getExploreEvents(any(), any(), any(), any(), any(), any(), any(), any()) } returns
            ApiResponse(success = true, data = response)

        val result = repository.getEvents(page = 1, limit = 20)

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.events?.size)
        assertEquals("evt1", result.getOrNull()?.events?.first()?.id)
    }

    @Test
    fun `getEvents failure returns Result failure`() = runBlocking {
        coEvery { api.getExploreEvents(any(), any(), any(), any(), any(), any(), any(), any()) } throws
            RuntimeException("Network Error")

        val result = repository.getEvents(page = 1, limit = 20)

        assertTrue(result.isFailure)
    }

    @Test
    fun `getEvents passes correct params to API`() = runBlocking {
        val response = ExploreEventsResponse(
            events = emptyList(),
            pagination = PaginationData(total = 0, page = 1, hasMore = false)
        )
        coEvery { api.getExploreEvents(any(), any(), any(), any(), any(), any(), any(), any()) } returns
            ApiResponse(success = true, data = response)

        repository.getEvents(page = 2, limit = 10, categoryId = "cat1", search = "yoga", isFree = true)

        coVerify {
            api.getExploreEvents(
                page = 2,
                limit = 10,
                cityId = null,
                lat = null,
                long = null,
                categoryId = "cat1",
                search = "yoga",
                isFree = true,
                sortBy = null
            )
        }
    }

    @Test
    fun `getEventDetails success returns event`() = runBlocking {
        val event = createEvent()
        val detailResponse = EventDetailResponse(event = event)
        coEvery { api.getEventDetails("evt1") } returns
            ApiResponse(success = true, data = detailResponse)

        val result = repository.getEventDetails("evt1")

        assertTrue(result.isSuccess)
        assertEquals("evt1", result.getOrNull()?.id)
        assertEquals("Yoga Class", result.getOrNull()?.title)
    }

    @Test
    fun `getEventDetails failure returns Result failure`() = runBlocking {
        coEvery { api.getEventDetails("evt1") } throws RuntimeException("Network Error")

        val result = repository.getEventDetails("evt1")

        assertTrue(result.isFailure)
    }

    @Test
    fun `getEventDetails null data returns Result failure`() = runBlocking {
        coEvery { api.getEventDetails("evt1") } returns
            ApiResponse(success = false, data = null, message = "Event not found")

        val result = repository.getEventDetails("evt1")

        assertTrue(result.isFailure)
    }

    @Test
    fun `joinFreeEvent success returns Unit`() = runBlocking {
        coEvery { api.joinFreeEvent("evt1") } returns
            ApiResponse(success = true, data = null)

        val result = repository.joinFreeEvent("evt1")

        assertTrue(result.isSuccess)
        assertEquals(Unit, result.getOrNull())
    }

    @Test
    fun `joinFreeEvent failure returns Result failure`() = runBlocking {
        coEvery { api.joinFreeEvent("evt1") } throws RuntimeException("Network Error")

        val result = repository.joinFreeEvent("evt1")

        assertTrue(result.isFailure)
    }

    @Test
    fun `joinFreeEvent API returns success false returns Result failure`() = runBlocking {
        coEvery { api.joinFreeEvent("evt1") } returns
            ApiResponse(success = false, data = null, message = "Failed to join")

        val result = repository.joinFreeEvent("evt1")

        assertTrue(result.isFailure)
    }

    @Test
    fun `getMetadata success returns metadata`() = runBlocking {
        val metadata = ExploreMetadataResponse(
            cities = listOf(ExploreCity(id = "city1", name = "Warsaw")),
            categories = listOf(ExploreCategory(id = "cat1", name = "Yoga"))
        )
        coEvery { api.getExploreMetadata() } returns
            ApiResponse(success = true, data = metadata)

        val result = repository.getMetadata()

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.cities?.size)
        assertEquals(1, result.getOrNull()?.categories?.size)
    }

    @Test
    fun `getMetadata failure returns Result failure`() = runBlocking {
        coEvery { api.getExploreMetadata() } throws RuntimeException("Network Error")

        val result = repository.getMetadata()

        assertTrue(result.isFailure)
    }

    @Test
    fun `getFeatured success returns featured response`() = runBlocking {
        val featured = ExploreFeaturedResponse(
            featuredEvents = listOf(createEvent()),
            featuredTrainers = emptyList()
        )
        coEvery { api.getExploreFeatured(any(), any(), any()) } returns
            ApiResponse(success = true, data = featured)

        val result = repository.getFeatured(lat = 52.0, long = 21.0, cityId = "city1")

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.featuredEvents?.size)
    }

    @Test
    fun `getFeatured failure returns Result failure`() = runBlocking {
        coEvery { api.getExploreFeatured(any(), any(), any()) } throws RuntimeException("Network Error")

        val result = repository.getFeatured()

        assertTrue(result.isFailure)
    }

    @Test
    fun `getFeatured null data returns Result failure`() = runBlocking {
        coEvery { api.getExploreFeatured(any(), any(), any()) } returns
            ApiResponse(success = false, data = null)

        val result = repository.getFeatured()

        assertTrue(result.isFailure)
    }
}
