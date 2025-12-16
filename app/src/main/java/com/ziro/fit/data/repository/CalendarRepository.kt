package com.ziro.fit.data.repository

import com.ziro.fit.data.remote.ZiroApi
import com.ziro.fit.model.CalendarEvent
import com.ziro.fit.model.CreateSessionRequest
import com.ziro.fit.util.ApiErrorParser
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
            
            Result.success(response.data!!.events)
        } catch (e: Exception) {
            val apiError = ApiErrorParser.parse(e)
            Result.failure(Exception(ApiErrorParser.getErrorMessage(apiError)))
        }
    }

    suspend fun createSession(request: CreateSessionRequest): Result<String> {
        return try {
            val response = api.createCalendarSession(request)
            Result.success(response.data!!.message)
        } catch (e: Exception) {
            val apiError = ApiErrorParser.parse(e)
            Result.failure(Exception(ApiErrorParser.getErrorMessage(apiError)))
        }
    }

    suspend fun getCalendarSummary(date: LocalDate): Result<List<com.ziro.fit.model.ClientSummaryItem>> {
        return try {
            // Buffer: 1 month back, 1 month forward for smooth month view scrolling
            val start = date.minusMonths(1).withDayOfMonth(1).atStartOfDay()
            val end = date.plusMonths(1).withDayOfMonth(date.plusMonths(1).lengthOfMonth()).atTime(23, 59, 59)

            val formatter = DateTimeFormatter.ISO_DATE_TIME

            val response = api.getCalendarClientsSummary(
                startDate = start.format(formatter),
                endDate = end.format(formatter)
            )

            Result.success(response.data?.summary ?: emptyList())
        } catch (e: Exception) {
            // Log error but don't crash UI, maybe return empty list or propagate error
            val apiError = ApiErrorParser.parse(e)
            Result.failure(Exception(ApiErrorParser.getErrorMessage(apiError)))
        }
    }
}
      