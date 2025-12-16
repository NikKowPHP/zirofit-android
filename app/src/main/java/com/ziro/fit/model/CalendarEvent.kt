package com.ziro.fit.model

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

data class CalendarEvent(
    val id: String,
    val title: String,
    val start: String, // Keep as String for GSON/Serialization
    val end: String,
    val type: EventType,
    val clientName: String?,
    val notes: String?
) {
    // Helper for UI logic
    val startTime: LocalDateTime get() = try {
        Instant.parse(start)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
    } catch (e: Exception) {
        // Fallback for non-standard formats
        LocalDateTime.parse(start.removeSuffix("Z"))
    }

    val endTime: LocalDateTime get() = try {
        Instant.parse(end)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
    } catch (e: Exception) {
        LocalDateTime.parse(end.removeSuffix("Z"))
    }
}

data class CalendarResponse(
    val events: List<CalendarEvent>
)

enum class EventType {
    booking,
    session_planned,
    session_completed,
    session_in_progress
}
      