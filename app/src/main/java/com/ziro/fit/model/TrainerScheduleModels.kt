package com.ziro.fit.model

/**
 * Response from GET /api/trainers/{username}/schedule
 * Contains trainer availability rules and existing bookings
 */
data class TrainerScheduleResponse(
    val availability: Map<String, Any>, // Flexible structure for availability rules
    val bookings: List<BookedTimeSlot>
)

/**
 * Represents a time slot that's already booked
 */
data class BookedTimeSlot(
    val startTime: String, // ISO-8601 datetime
    val endTime: String    // ISO-8601 datetime
)
