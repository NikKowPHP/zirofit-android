package com.ziro.fit.data.repository

import com.ziro.fit.model.ApiResponse
import com.ziro.fit.data.model.CheckInContext
import com.ziro.fit.data.model.CheckInPendingItem
import com.ziro.fit.data.model.ReviewCheckInRequest
import com.ziro.fit.data.remote.ZiroApi
import com.ziro.fit.util.ApiErrorParser
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CheckInRepository @Inject constructor(
    private val api: ZiroApi
) {
    suspend fun getPendingCheckIns(): Result<List<CheckInPendingItem>> {
        return try {
            val response = api.getPendingCheckIns() // This endpoint returns List directly based on my previous read, verify?
            // Wait, api_reference said: 200 -> content -> ... schema type: array.
            // If it returns array directly, then `api.getPendingCheckIns()` returning `List<CheckInPendingItem>` is correct.
            Result.success(response)
        } catch (e: Exception) {
            val apiError = ApiErrorParser.parse(e)
            Result.failure(Exception(ApiErrorParser.getErrorMessage(apiError)))
        }
    }

    suspend fun getCheckInDetails(id: String): Result<CheckInContext> {
        return try {
            val response = api.getCheckInDetails(id)
            if (response.success != false && response.data != null) {
                 Result.success(response.data)
            } else {
                 Result.failure(Exception(response.message ?: "Failed to fetch check-in details"))
            }
        } catch (e: Exception) {
            val apiError = ApiErrorParser.parse(e)
            Result.failure(Exception(ApiErrorParser.getErrorMessage(apiError)))
        }
    }

    suspend fun reviewCheckIn(id: String, review: String): Result<Unit> {
        return try {
            val request = ReviewCheckInRequest(trainerResponse = review)
            val response = api.reviewCheckIn(id, request)
            if (response.success != false) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message ?: "Failed to submit review"))
            }
        } catch (e: Exception) {
            val apiError = ApiErrorParser.parse(e)
            Result.failure(Exception(ApiErrorParser.getErrorMessage(apiError)))
        }
    }
}
