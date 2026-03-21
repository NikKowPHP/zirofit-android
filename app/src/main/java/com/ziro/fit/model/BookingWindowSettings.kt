package com.ziro.fit.model

/**
 * Trainer booking window settings.
 * @param advanceNoticeHours Minimum hours before a session can be booked (e.g., 24)
 * @param bookingHorizonHours How far in advance clients can book (e.g., 168 = 1 week)
 */
data class BookingWindowSettings(
    val advanceNoticeHours: Int = 24,
    val bookingHorizonHours: Int = 168
)
