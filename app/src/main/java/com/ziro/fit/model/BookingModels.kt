package com.ziro.fit.model



data class CreateBookingRequest(
    val trainerId: String,
    val startTime: String,
    val endTime: String,
    val clientName: String?,
    val clientEmail: String?,
    val clientNotes: String?
)

data class CreateBookingResponse(
    val booking: Booking
)

data class UpdateBookingRequest(
    val startTime: String? = null,
    val endTime: String? = null,
    val status: BookingStatus? = null,
    val clientName: String? = null,
    val clientEmail: String? = null,
    val clientNotes: String? = null
)

// Response for single booking update if the API returns the updated object
data class BookingResponse(
    val booking: Booking
)
