package com.ziro.fit.viewmodel

import android.util.Log
import com.ziro.fit.data.repository.BillingRepository
import com.ziro.fit.data.repository.ExploreRepository
import com.ziro.fit.model.ExploreEvent
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
class EventDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val exploreRepository: ExploreRepository = mockk(relaxed = true)
    private val billingRepository: BillingRepository = mockk(relaxed = true)
    private lateinit var viewModel: EventDetailViewModel

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.e(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>(), any<Throwable>()) } returns 0
    }

    private fun freeEvent() = ExploreEvent(
        id = "evt1",
        title = "Free Yoga",
        description = "Relaxing yoga session",
        startTime = "2026-03-25T10:00:00Z",
        endTime = "2026-03-25T11:00:00Z",
        price = 0.0,
        currency = "PLN",
        locationName = "Studio",
        address = "123 Main St",
        latitude = 52.0,
        longitude = 21.0,
        imageUrl = null,
        categoryId = null,
        cityId = null,
        priceDisplay = "Free",
        hostName = null,
        hostId = null,
        trainerName = null,
        trainerId = null,
        enrolledCount = 0,
        capacity = 20,
        isBooked = false,
        isNearCapacity = false,
        trainer = null
    )

    private fun paidEvent() = freeEvent().copy(id = "evt2", price = 50.0, priceDisplay = "50 PLN")

    private fun createViewModel(): EventDetailViewModel {
        return EventDetailViewModel(exploreRepository, billingRepository)
    }

    @Test
    fun `loadEventDetails success updates event in state`() = runTest {
        val event = freeEvent()
        coEvery { exploreRepository.getEventDetails("evt1") } returns Result.success(event)

        val viewModel = createViewModel()
        viewModel.loadEventDetails("evt1")
        advanceUntilIdle()

        assertEquals(event, viewModel.uiState.value.event)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `loadEventDetails failure updates error state`() = runTest {
        coEvery { exploreRepository.getEventDetails("evt1") } returns
            Result.failure(RuntimeException("Event not found"))

        val viewModel = createViewModel()
        viewModel.loadEventDetails("evt1")
        advanceUntilIdle()

        assertEquals("Event not found", viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `enroll with free event calls exploreRepository joinFreeEvent`() = runTest {
        val event = freeEvent()
        coEvery { exploreRepository.joinFreeEvent(event.id) } returns Result.success(Unit)

        val viewModel = createViewModel()
        viewModel.enroll(event)
        advanceUntilIdle()

        coVerify { exploreRepository.joinFreeEvent(event.id) }
        assertTrue(viewModel.uiState.value.joinSuccess)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `enroll with free event failure updates error state`() = runTest {
        val event = freeEvent()
        coEvery { exploreRepository.joinFreeEvent(event.id) } returns
            Result.failure(RuntimeException("Failed to join"))

        val viewModel = createViewModel()
        viewModel.enroll(event)
        advanceUntilIdle()

        assertEquals("Failed to join", viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.joinSuccess)
    }

    @Test
    fun `enroll with paid event calls billingRepository createCheckoutSession`() = runTest {
        val event = paidEvent()
        coEvery { billingRepository.createCheckoutSession(eventId = event.id, type = "event") } returns
            Result.success("https://checkout.stripe.com")

        val viewModel = createViewModel()
        viewModel.enroll(event)
        advanceUntilIdle()

        coVerify { billingRepository.createCheckoutSession(eventId = event.id, type = "event") }
        assertEquals("https://checkout.stripe.com", viewModel.uiState.value.checkoutUrl)
    }

    @Test
    fun `enroll with paid event success sets checkoutUrl`() = runTest {
        val event = paidEvent()
        val checkoutUrl = "https://checkout.stripe.com/pay"
        coEvery { billingRepository.createCheckoutSession(eventId = event.id, type = "event") } returns
            Result.success(checkoutUrl)

        val viewModel = createViewModel()
        viewModel.enroll(event)
        advanceUntilIdle()

        assertEquals(checkoutUrl, viewModel.uiState.value.checkoutUrl)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `enroll with paid event failure updates error state`() = runTest {
        val event = paidEvent()
        coEvery { billingRepository.createCheckoutSession(eventId = event.id, type = "event") } returns
            Result.failure(RuntimeException("Checkout failed"))

        val viewModel = createViewModel()
        viewModel.enroll(event)
        advanceUntilIdle()

        assertEquals("Checkout failed", viewModel.uiState.value.error)
        assertNull(viewModel.uiState.value.checkoutUrl)
    }

    @Test
    fun `clearCheckoutUrl sets checkoutUrl to null`() = runTest {
        val event = paidEvent()
        coEvery { billingRepository.createCheckoutSession(eventId = event.id, type = "event") } returns
            Result.success("https://checkout.stripe.com")

        val viewModel = createViewModel()
        viewModel.enroll(event)
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.checkoutUrl)

        viewModel.clearCheckoutUrl()

        assertNull(viewModel.uiState.value.checkoutUrl)
    }

    @Test
    fun `clearError sets error to null`() = runTest {
        coEvery { exploreRepository.getEventDetails("evt1") } returns
            Result.failure(RuntimeException("Error"))

        val viewModel = createViewModel()
        viewModel.loadEventDetails("evt1")
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.error)

        viewModel.clearError()

        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `isLoading is true during enroll operation`() = runTest {
        val event = freeEvent()
        coEvery { exploreRepository.joinFreeEvent(event.id) } returns Result.success(Unit)

        val viewModel = createViewModel()

        var isLoadingDuringOperation = false
        viewModel.enroll(event)

        viewModel.uiState.value.let {
            if (it.isLoading) isLoadingDuringOperation = true
        }

        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `loadEventDetails clears previous error`() = runTest {
        coEvery { exploreRepository.getEventDetails("evt1") } returns
            Result.failure(RuntimeException("First error"))

        val viewModel = createViewModel()
        viewModel.loadEventDetails("evt1")
        advanceUntilIdle()

        assertEquals("First error", viewModel.uiState.value.error)

        val event = freeEvent()
        coEvery { exploreRepository.getEventDetails("evt1") } returns Result.success(event)

        viewModel.loadEventDetails("evt1")
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `enroll clears previous error`() = runTest {
        coEvery { exploreRepository.getEventDetails("evt1") } returns
            Result.failure(RuntimeException("Load error"))

        val viewModel = createViewModel()
        viewModel.loadEventDetails("evt1")
        advanceUntilIdle()

        val event = freeEvent()
        coEvery { exploreRepository.joinFreeEvent(event.id) } returns Result.success(Unit)

        viewModel.enroll(event)
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.error)
    }
}
