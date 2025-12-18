package com.ziro.fit.data.repository

import com.ziro.fit.model.ApiResponse
import com.ziro.fit.data.model.CheckInContext
import com.ziro.fit.data.model.CheckInDetailWrapper
import com.ziro.fit.data.model.CheckInPendingItem
import com.ziro.fit.data.model.ReviewCheckInRequest
import com.ziro.fit.data.model.CheckInConfig
import com.ziro.fit.data.model.CheckInSubmissionRequest
import com.ziro.fit.data.model.CheckInHistoryItem
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

    // Client Methods
    suspend fun getCheckInConfig(): Result<CheckInConfig> {
        return try {
            val response = api.getCheckInConfig()
            if (response.success != false && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Failed to fetch check-in config"))
            }
        } catch (e: Exception) {
            val apiError = ApiErrorParser.parse(e)
            Result.failure(Exception(ApiErrorParser.getErrorMessage(apiError)))
        }
    }

    suspend fun submitCheckIn(request: com.ziro.fit.data.model.CheckInSubmissionRequest): Result<Unit> {
        return try {
            val response = api.submitCheckIn(request)
            if (response.success != false) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message ?: "Failed to submit check-in"))
            }
        } catch (e: Exception) {
            val apiError = ApiErrorParser.parse(e)
            Result.failure(Exception(ApiErrorParser.getErrorMessage(apiError)))
        }
    }

    suspend fun getClientCheckInHistory(): Result<List<com.ziro.fit.data.model.CheckInHistoryItem>> {
        return try {
            val response = api.getClientCheckInHistory()
            if (response.success != false && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Failed to fetch check-in history"))
            }
        } catch (e: Exception) {
            val apiError = ApiErrorParser.parse(e)
            Result.failure(Exception(ApiErrorParser.getErrorMessage(apiError)))
        }
    }

    suspend fun getClientCheckInDetails(id: String): Result<CheckInDetailWrapper> {
        return try {
            val response = api.getClientCheckInDetails(id)
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
}
