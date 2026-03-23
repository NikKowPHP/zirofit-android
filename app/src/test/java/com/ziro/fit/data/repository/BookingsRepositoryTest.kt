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

class BookingsRepositoryTest {
    private val api: ZiroApi = mockk(relaxed = true)
    private val repository = BookingsRepository(api)

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.e(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>(), any<Throwable>()) } returns 0
    }

    @Test
    fun `getBookings success returns list of bookings`() = runBlocking {
        val bookings = listOf(
            Booking(id = "1", startTime = "2026-03-22T10:00:00Z", endTime = "2026-03-22T11:00:00Z", status = BookingStatus.PENDING, trainerId = "t1"),
            Booking(id = "2", startTime = "2026-03-23T10:00:00Z", endTime = "2026-03-23T11:00:00Z", status = BookingStatus.CONFIRMED, trainerId = "t1")
        )
        coEvery { api.getBookings() } returns ApiResponse(success = true, data = bookings)

        val result = repository.getBookings()

        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
        coVerify { api.getBookings() }
    }

    @Test
    fun `getBookings failure returns Result failure`() = runBlocking {
        coEvery { api.getBookings() } throws RuntimeException("Network Error")

        val result = repository.getBookings()

        assertTrue(result.isFailure)
        coVerify { api.getBookings() }
    }

    @Test
    fun `getBookings null data returns Result failure`() = runBlocking {
        coEvery { api.getBookings() } returns ApiResponse(success = true, data = null)

        val result = repository.getBookings()

        assertTrue(result.isFailure)
        assertEquals("Failed to fetch bookings", result.exceptionOrNull()?.message)
        coVerify { api.getBookings() }
    }

    @Test
    fun `getBookings success false returns Result failure`() = runBlocking {
        coEvery { api.getBookings() } returns ApiResponse(success = false, data = null, error = "Failed to fetch bookings")

        val result = repository.getBookings()

        assertTrue(result.isFailure)
        assertEquals("Failed to fetch bookings", result.exceptionOrNull()?.message)
        coVerify { api.getBookings() }
    }

    @Test
    fun `createBooking success returns booking`() = runBlocking {
        val newBooking = Booking(id = "3", trainerId = "t1", startTime = "2026-03-24T10:00:00Z", endTime = "2026-03-24T11:00:00Z", status = BookingStatus.PENDING)
        val response = ApiResponse(success = true, data = CreateBookingResponse(booking = newBooking))
        coEvery { api.createBooking(any()) } returns response

        val result = repository.createBooking(
            trainerId = "t1",
            startTime = "2026-03-24T10:00:00Z",
            endTime = "2026-03-24T11:00:00Z",
            clientName = "John",
            clientEmail = "john@example.com",
            clientNotes = "First session"
        )

        assertTrue(result.isSuccess)
        assertEquals(newBooking.id, result.getOrNull()?.id)
        coVerify { api.createBooking(match { it.trainerId == "t1" && it.startTime == "2026-03-24T10:00:00Z" }) }
    }

    @Test
    fun `createBooking failure returns Result failure`() = runBlocking {
        coEvery { api.createBooking(any()) } throws RuntimeException("Network Error")

        val result = repository.createBooking(
            trainerId = "t1",
            startTime = "2026-03-24T10:00:00Z",
            endTime = "2026-03-24T11:00:00Z",
            clientName = "John",
            clientEmail = "john@example.com",
            clientNotes = null
        )

        assertTrue(result.isFailure)
        coVerify { api.createBooking(any()) }
    }

    @Test
    fun `createBooking null data returns Result failure`() = runBlocking {
        coEvery { api.createBooking(any()) } returns ApiResponse(success = true, data = null)

        val result = repository.createBooking(
            trainerId = "t1",
            startTime = "2026-03-24T10:00:00Z",
            endTime = "2026-03-24T11:00:00Z",
            clientName = null,
            clientEmail = null,
            clientNotes = null
        )

        assertTrue(result.isFailure)
        assertEquals("Failed to create booking", result.exceptionOrNull()?.message)
        coVerify { api.createBooking(any()) }
    }

    @Test
    fun `updateBooking success returns updated booking`() = runBlocking {
        val updatedBooking = Booking(id = "1", trainerId = "t1", startTime = "2026-03-22T12:00:00Z", endTime = "2026-03-22T13:00:00Z", status = BookingStatus.CONFIRMED)
        val response = ApiResponse(success = true, data = BookingResponse(booking = updatedBooking))
        coEvery { api.updateBooking("1", any()) } returns response

        val request = UpdateBookingRequest(status = BookingStatus.CONFIRMED)
        val result = repository.updateBooking("1", request)

        assertTrue(result.isSuccess)
        assertEquals(updatedBooking.id, result.getOrNull()?.id)
        assertEquals(BookingStatus.CONFIRMED, result.getOrNull()?.status)
        coVerify { api.updateBooking("1", match { it.status == BookingStatus.CONFIRMED }) }
    }

    @Test
    fun `updateBooking failure returns Result failure`() = runBlocking {
        coEvery { api.updateBooking("1", any()) } throws RuntimeException("Network Error")

        val request = UpdateBookingRequest(status = BookingStatus.CONFIRMED)
        val result = repository.updateBooking("1", request)

        assertTrue(result.isFailure)
        coVerify { api.updateBooking("1", any()) }
    }

    @Test
    fun `updateBooking null data returns Result failure`() = runBlocking {
        coEvery { api.updateBooking("1", any()) } returns ApiResponse(success = true, data = null)

        val request = UpdateBookingRequest(status = BookingStatus.CONFIRMED)
        val result = repository.updateBooking("1", request)

        assertTrue(result.isFailure)
        assertEquals("Failed to update booking", result.exceptionOrNull()?.message)
        coVerify { api.updateBooking("1", any()) }
    }

    @Test
    fun `deleteBooking success returns Unit`() = runBlocking {
        coEvery { api.deleteBooking("1") } returns ApiResponse(success = true, data = null)

        val result = repository.deleteBooking("1")

        assertTrue(result.isSuccess)
        assertEquals(Unit, result.getOrNull())
        coVerify { api.deleteBooking("1") }
    }

    @Test
    fun `deleteBooking failure returns Result failure`() = runBlocking {
        coEvery { api.deleteBooking("1") } throws RuntimeException("Network Error")

        val result = repository.deleteBooking("1")

        assertTrue(result.isFailure)
        coVerify { api.deleteBooking("1") }
    }

    @Test
    fun `deleteBooking success false returns Result failure`() = runBlocking {
        coEvery { api.deleteBooking("1") } returns ApiResponse(success = false, data = null, error = "Failed to delete booking")

        val result = repository.deleteBooking("1")

        assertTrue(result.isFailure)
        assertEquals("Failed to delete booking", result.exceptionOrNull()?.message)
        coVerify { api.deleteBooking("1") }
    }

    // ===== Confirm Booking Tests =====

    @Test
    fun `confirmBooking with data sharing success returns booking with dataSharingApproved true`() = runBlocking {
        val confirmedBooking = Booking(
            id = "1",
            trainerId = "t1",
            startTime = "2026-03-22T10:00:00Z",
            endTime = "2026-03-22T11:00:00Z",
            status = BookingStatus.CONFIRMED,
            dataSharingApproved = true,
            dataSharingApprovedAt = "2026-03-22T09:00:00Z"
        )
        val response = ApiResponse(success = true, data = BookingResponse(booking = confirmedBooking))
        coEvery { api.confirmBooking("1", any()) } returns response

        val result = repository.confirmBooking("1", dataSharingApproved = true)

        assertTrue(result.isSuccess)
        assertEquals(BookingStatus.CONFIRMED, result.getOrNull()?.status)
        assertEquals(true, result.getOrNull()?.dataSharingApproved)
        coVerify { api.confirmBooking("1", match { it.dataSharingApproved == true }) }
    }

    @Test
    fun `confirmBooking without data sharing success returns booking with dataSharingApproved false`() = runBlocking {
        val confirmedBooking = Booking(
            id = "1",
            trainerId = "t1",
            startTime = "2026-03-22T10:00:00Z",
            endTime = "2026-03-22T11:00:00Z",
            status = BookingStatus.CONFIRMED,
            dataSharingApproved = false
        )
        val response = ApiResponse(success = true, data = BookingResponse(booking = confirmedBooking))
        coEvery { api.confirmBooking("1", any()) } returns response

        val result = repository.confirmBooking("1", dataSharingApproved = false)

        assertTrue(result.isSuccess)
        assertEquals(BookingStatus.CONFIRMED, result.getOrNull()?.status)
        assertEquals(false, result.getOrNull()?.dataSharingApproved)
        coVerify { api.confirmBooking("1", match { it.dataSharingApproved == false }) }
    }

    @Test
    fun `confirmBooking failure returns Result failure`() = runBlocking {
        coEvery { api.confirmBooking("1", any()) } throws RuntimeException("Network Error")

        val result = repository.confirmBooking("1", dataSharingApproved = true)

        assertTrue(result.isFailure)
        coVerify { api.confirmBooking("1", any()) }
    }

    @Test
    fun `confirmBooking null data returns Result failure`() = runBlocking {
        coEvery { api.confirmBooking("1", any()) } returns ApiResponse(success = true, data = null)

        val result = repository.confirmBooking("1", dataSharingApproved = true)

        assertTrue(result.isFailure)
        assertEquals("Failed to confirm booking", result.exceptionOrNull()?.message)
        coVerify { api.confirmBooking("1", any()) }
    }

    @Test
    fun `confirmBooking success false returns Result failure`() = runBlocking {
        coEvery { api.confirmBooking("1", any()) } returns ApiResponse(success = false, data = null, error = "Booking not found")

        val result = repository.confirmBooking("1", dataSharingApproved = true)

        assertTrue(result.isFailure)
        assertEquals("Booking not found", result.exceptionOrNull()?.message)
        coVerify { api.confirmBooking("1", any()) }
    }

    // ===== Decline Booking Tests =====

    @Test
    fun `declineBooking success returns booking with CANCELLED status`() = runBlocking {
        val declinedBooking = Booking(
            id = "1",
            trainerId = "t1",
            startTime = "2026-03-22T10:00:00Z",
            endTime = "2026-03-22T11:00:00Z",
            status = BookingStatus.CANCELLED
        )
        val response = ApiResponse(success = true, data = BookingResponse(booking = declinedBooking))
        coEvery { api.declineBooking("1") } returns response

        val result = repository.declineBooking("1")

        assertTrue(result.isSuccess)
        assertEquals(BookingStatus.CANCELLED, result.getOrNull()?.status)
        coVerify { api.declineBooking("1") }
    }

    @Test
    fun `declineBooking failure returns Result failure`() = runBlocking {
        coEvery { api.declineBooking("1") } throws RuntimeException("Network Error")

        val result = repository.declineBooking("1")

        assertTrue(result.isFailure)
        coVerify { api.declineBooking("1") }
    }

    @Test
    fun `declineBooking null data returns Result failure`() = runBlocking {
        coEvery { api.declineBooking("1") } returns ApiResponse(success = true, data = null)

        val result = repository.declineBooking("1")

        assertTrue(result.isFailure)
        assertEquals("Failed to decline booking", result.exceptionOrNull()?.message)
        coVerify { api.declineBooking("1") }
    }

    @Test
    fun `declineBooking success false returns Result failure`() = runBlocking {
        coEvery { api.declineBooking("1") } returns ApiResponse(success = false, data = null, error = "Booking already processed")

        val result = repository.declineBooking("1")

        assertTrue(result.isFailure)
        assertEquals("Booking already processed", result.exceptionOrNull()?.message)
        coVerify { api.declineBooking("1") }
    }
}
