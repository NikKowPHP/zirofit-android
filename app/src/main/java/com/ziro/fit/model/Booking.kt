package com.ziro.fit.model

data class Booking(
    val id: String,
    val trainerId: String,
    val startTime: String,
    val endTime: String,
    val status: BookingStatus,
    val clientName: String? = null,
    val clientEmail: String? = null,
    val clientNotes: String? = null,
    val clientId: String? = null
)

enum class BookingStatus {
    PENDING,
    CONFIRMED,
    CANCELLED
}
