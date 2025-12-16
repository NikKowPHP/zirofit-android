package com.ziro.fit.data.repository

import com.ziro.fit.util.ApiErrorParser
import com.ziro.fit.data.remote.ZiroApi
import com.ziro.fit.model.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookingsRepository @Inject constructor(
    private val api: ZiroApi
) {

    suspend fun getBookings(): Result<List<Booking>> {
        return try {
            val response = api.getBookings()
            if ((response.success ?: true) && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.error ?: "Failed to fetch bookings"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(ApiErrorParser.getErrorMessage(ApiErrorParser.parse(e))))
        }
    }

    suspend fun createBooking(
        trainerId: String,
        startTime: String,
        endTime: String,
        clientName: String?,
        clientEmail: String?,
        clientNotes: String?
    ): Result<Booking> {
        val request = CreateBookingRequest(
            trainerId = trainerId,
            startTime = startTime,
            endTime = endTime,
            clientName = clientName,
            clientEmail = clientEmail,
            clientNotes = clientNotes
        )
        return try {
            val response = api.createBooking(request)
            if ((response.success ?: true) && response.data != null) {
                Result.success(response.data!!.booking)
            } else {
                Result.failure(Exception(response.error ?: "Failed to create booking"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(ApiErrorParser.getErrorMessage(ApiErrorParser.parse(e))))
        }
    }

    suspend fun updateBooking(
        bookingId: String,
        request: UpdateBookingRequest
    ): Result<Booking> {
        return try {
            val response = api.updateBooking(bookingId, request)
            if ((response.success ?: true) && response.data != null) {
                Result.success(response.data!!.booking)
            } else {
                Result.failure(Exception(response.error ?: "Failed to update booking"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(ApiErrorParser.getErrorMessage(ApiErrorParser.parse(e))))
        }
    }

    suspend fun deleteBooking(bookingId: String): Result<Unit> {
        return try {
            val response = api.deleteBooking(bookingId)
            if (response.success ?: true) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.error ?: "Failed to delete booking"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(ApiErrorParser.getErrorMessage(ApiErrorParser.parse(e))))
        }
    }
}
