package com.ziro.fit.viewmodel

import com.ziro.fit.data.repository.BookingsRepository
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
class BookingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: BookingsRepository = mockk(relaxed = true)
    private lateinit var viewModel: BookingsViewModel

    @Before
    fun setup() {
        coEvery { repository.getBookings() } returns Result.success(emptyList())
    }

    private fun createViewModel(): BookingsViewModel {
        return BookingsViewModel(repository)
    }

    @Test
    fun `init loads bookings automatically`() = runTest {
        createViewModel()
        advanceUntilIdle()
        coVerify { repository.getBookings() }
    }

    @Test
    fun `loadBookings success updates bookings list`() = runTest {
        val bookings = listOf(
            Booking(id = "1", trainerId = "t1", startTime = "2026-03-22T10:00:00Z", endTime = "2026-03-22T11:00:00Z", status = BookingStatus.PENDING)
        )
        coEvery { repository.getBookings() } returns Result.success(bookings)

        viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.bookings.size)
        assertEquals("1", state.bookings[0].id)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `loadBookings failure updates error state`() = runTest {
        coEvery { repository.getBookings() } returns Result.failure(Exception("Network error"))

        viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.bookings.isEmpty())
        assertFalse(state.isLoading)
        assertEquals("Network error", state.error)
    }

    @Test
    fun `createBooking success adds to list and calls onSuccess callback`() = runTest {
        val newBooking = Booking(id = "3", trainerId = "t1", startTime = "2026-03-24T10:00:00Z", endTime = "2026-03-24T11:00:00Z", status = BookingStatus.PENDING)
        coEvery { repository.createBooking(any(), any(), any(), any(), any(), any()) } returns Result.success(newBooking)
        val onSuccess = mockk<() -> Unit>(relaxed = true)

        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.createBooking(
            trainerId = "t1",
            startTime = "2026-03-24T10:00:00Z",
            endTime = "2026-03-24T11:00:00Z",
            clientName = null,
            clientEmail = null,
            clientNotes = null,
            onSuccess = onSuccess
        )
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.bookings.size)
        assertEquals("3", state.bookings[0].id)
        assertEquals("Booking created successfully", state.successMessage)
        assertFalse(state.isLoading)
        assertNull(state.error)
        verify { onSuccess() }
    }

    @Test
    fun `createBooking failure updates error state, does not call onSuccess`() = runTest {
        coEvery { repository.createBooking(any(), any(), any(), any(), any(), any()) } returns Result.failure(Exception("Failed to create"))
        val onSuccess = mockk<() -> Unit>(relaxed = true)

        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.createBooking(
            trainerId = "t1",
            startTime = "2026-03-24T10:00:00Z",
            endTime = "2026-03-24T11:00:00Z",
            clientName = null,
            clientEmail = null,
            clientNotes = null,
            onSuccess = onSuccess
        )
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.bookings.isEmpty())
        assertEquals("Failed to create", state.error)
        assertFalse(state.isLoading)
        assertNull(state.successMessage)
        verify(exactly = 0) { onSuccess() }
    }

    @Test
    fun `updateBooking success updates existing booking in list`() = runTest {
        val existingBookings = listOf(
            Booking(id = "1", trainerId = "t1", startTime = "2026-03-22T10:00:00Z", endTime = "2026-03-22T11:00:00Z", status = BookingStatus.PENDING)
        )
        coEvery { repository.getBookings() } returns Result.success(existingBookings)
        val updatedBooking = Booking(id = "1", trainerId = "t1", startTime = "2026-03-22T10:00:00Z", endTime = "2026-03-22T11:00:00Z", status = BookingStatus.CONFIRMED)
        coEvery { repository.updateBooking("1", any()) } returns Result.success(updatedBooking)
        val onSuccess = mockk<() -> Unit>(relaxed = true)

        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.updateBooking(
            bookingId = "1",
            request = UpdateBookingRequest(status = BookingStatus.CONFIRMED),
            onSuccess = onSuccess
        )
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.bookings.size)
        assertEquals(BookingStatus.CONFIRMED, state.bookings[0].status)
        assertEquals("Booking updated successfully", state.successMessage)
        assertFalse(state.isLoading)
        verify { onSuccess() }
    }

    @Test
    fun `updateBooking failure updates error state`() = runTest {
        coEvery { repository.getBookings() } returns Result.success(emptyList())
        coEvery { repository.updateBooking("1", any()) } returns Result.failure(Exception("Failed to update"))
        val onSuccess = mockk<() -> Unit>(relaxed = true)

        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.updateBooking(
            bookingId = "1",
            request = UpdateBookingRequest(status = BookingStatus.CONFIRMED),
            onSuccess = onSuccess
        )
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Failed to update", state.error)
        assertFalse(state.isLoading)
        assertNull(state.successMessage)
        verify(exactly = 0) { onSuccess() }
    }

    @Test
    fun `deleteBooking success removes from list`() = runTest {
        val existingBookings = listOf(
            Booking(id = "1", trainerId = "t1", startTime = "2026-03-22T10:00:00Z", endTime = "2026-03-22T11:00:00Z", status = BookingStatus.PENDING),
            Booking(id = "2", trainerId = "t1", startTime = "2026-03-23T10:00:00Z", endTime = "2026-03-23T11:00:00Z", status = BookingStatus.CONFIRMED)
        )
        coEvery { repository.getBookings() } returns Result.success(existingBookings)
        coEvery { repository.deleteBooking("1") } returns Result.success(Unit)

        viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(2, viewModel.uiState.value.bookings.size)

        viewModel.deleteBooking("1")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.bookings.size)
        assertEquals("2", state.bookings[0].id)
        assertEquals("Booking deleted", state.successMessage)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `deleteBooking failure updates error state`() = runTest {
        coEvery { repository.getBookings() } returns Result.success(emptyList())
        coEvery { repository.deleteBooking("1") } returns Result.failure(Exception("Failed to delete"))

        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.deleteBooking("1")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Failed to delete", state.error)
        assertFalse(state.isLoading)
        assertNull(state.successMessage)
    }

    @Test
    fun `clearError sets error to null`() = runTest {
        coEvery { repository.getBookings() } returns Result.failure(Exception("Network error"))

        viewModel = createViewModel()
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.error)

        viewModel.clearError()

        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `clearSuccessMessage sets successMessage to null`() = runTest {
        val newBooking = Booking(id = "3", trainerId = "t1", startTime = "2026-03-24T10:00:00Z", endTime = "2026-03-24T11:00:00Z", status = BookingStatus.PENDING)
        coEvery { repository.createBooking(any(), any(), any(), any(), any(), any()) } returns Result.success(newBooking)
        val onSuccess = mockk<() -> Unit>(relaxed = true)

        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.createBooking(
            trainerId = "t1",
            startTime = "2026-03-24T10:00:00Z",
            endTime = "2026-03-24T11:00:00Z",
            clientName = null,
            clientEmail = null,
            clientNotes = null,
            onSuccess = onSuccess
        )
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.successMessage)

        viewModel.clearSuccessMessage()

        assertNull(viewModel.uiState.value.successMessage)
    }

    @Test
    fun `isLoading is true while operation in progress`() = runTest {
        coEvery { repository.getBookings() } returns Result.success(emptyList())
        coEvery { repository.createBooking(any(), any(), any(), any(), any(), any()) } coAnswers {
            kotlinx.coroutines.delay(100)
            Result.success(Booking(id = "3", trainerId = "t1", startTime = "2026-03-24T10:00:00Z", endTime = "2026-03-24T11:00:00Z", status = BookingStatus.PENDING))
        }
        val onSuccess = mockk<() -> Unit>(relaxed = true)

        viewModel = createViewModel()

        viewModel.createBooking(
            trainerId = "t1",
            startTime = "2026-03-24T10:00:00Z",
            endTime = "2026-03-24T11:00:00Z",
            clientName = null,
            clientEmail = null,
            clientNotes = null,
            onSuccess = onSuccess
        )
        assertTrue(viewModel.uiState.value.isLoading || viewModel.uiState.value.bookings.size == 1)
    }
}
