package com.ziro.fit.util

import org.junit.Assert.*
import org.junit.Test
import java.time.Instant
import java.time.ZoneId

class DateTimeUtilsTest {

    @Test
    fun `formatDate parses valid ISO string`() {
        val result = DateTimeUtils.formatDate("2024-03-15T10:30:00Z")
        assertTrue(result.contains("Mar"))
        assertTrue(result.contains("15") && result.contains("2024"))
    }

    @Test
    fun `formatDate returns not available for null`() {
        assertEquals("N/A", DateTimeUtils.formatDate(null))
    }

    @Test
    fun `formatDate returns not available for blank string`() {
        assertEquals("N/A", DateTimeUtils.formatDate(""))
        assertEquals("N/A", DateTimeUtils.formatDate("   "))
    }

    @Test
    fun `formatDate returns raw string on invalid format`() {
        val invalid = "not-a-date"
        val result = DateTimeUtils.formatDate(invalid)
        assertEquals(invalid, result)
    }

    @Test
    fun `formatDate handles various timezones`() {
        val instant = Instant.parse("2024-06-01T12:00:00Z")
        val zone = ZoneId.of("America/New_York")
        val zoned = instant.atZone(zone)
        val formatter = java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy")
        val expected = zoned.format(formatter)
        assertEquals(expected, DateTimeUtils.formatDate("2024-06-01T12:00:00Z"))
    }

    @Test
    fun `formatDateTime parses valid ISO string with time`() {
        val result = DateTimeUtils.formatDateTime("2024-03-15T10:30:00Z")
        assertTrue(result.contains("Mar"))
        assertTrue(result.contains("15") && result.contains("2024"))
        assertTrue(result.contains("30"))
    }

    @Test
    fun `formatDateTime returns not available for null`() {
        assertEquals("N/A", DateTimeUtils.formatDateTime(null))
    }

    @Test
    fun `formatDateTime returns not available for blank string`() {
        assertEquals("N/A", DateTimeUtils.formatDateTime(""))
    }

    @Test
    fun `formatDateTime returns raw string on invalid format`() {
        val invalid = "invalid-date-string"
        val result = DateTimeUtils.formatDateTime(invalid)
        assertEquals(invalid, result)
    }
}
