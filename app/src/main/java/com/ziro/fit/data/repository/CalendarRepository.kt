package com.ziro.fit.data.repository

import com.ziro.fit.data.remote.ZiroApi
import com.ziro.fit.model.CalendarEvent
import com.ziro.fit.model.CreateSessionRequest
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalendarRepository @Inject constructor(
    private val api: ZiroApi
) {
    suspend fun getEvents(date: LocalDate): Result<List<CalendarEvent>> {
        return try {
            // Fetch a buffer around the selected date (e.g., 2 weeks back, 2 weeks forward)
            // to allow smooth swiping without constant loading
            val start = date.minusWeeks(2).atStartOfDay()
            val end = date.plusWeeks(2).atTime(23, 59, 59)
            
            // Format to ISO 8601
            val formatter = DateTimeFormatter.ISO_DATE_TIME
            
            val response = api.getCalendarEvents(
                startDate = start.format(formatter),
                endDate = end.format(formatter)
            )
            
            Result.success(response.data.events)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createSession(request: CreateSessionRequest): Result<String> {
        return try {
            val response = api.createCalendarSession(request)
            Result.success(response.data.message)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
      