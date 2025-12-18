package com.ziro.fit.util

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object DateTimeUtils {
    private val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")

    fun formatDate(isoString: String?): String {
        if (isoString.isNullOrBlank()) return "N/A"
        return try {
            Instant.parse(isoString)
                .atZone(ZoneId.systemDefault())
                .format(dateFormatter)
        } catch (e: Exception) {
            isoString
        }
    }

    fun formatDateTime(isoString: String?): String {
        if (isoString.isNullOrBlank()) return "N/A"
        return try {
            Instant.parse(isoString)
                .atZone(ZoneId.systemDefault())
                .format(dateTimeFormatter)
        } catch (e: Exception) {
            isoString
        }
    }
}
