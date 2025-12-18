package com.ziro.fit.data.repository

import com.ziro.fit.data.remote.ZiroApi
import com.ziro.fit.model.ClientDashboardData
import com.ziro.fit.util.ApiErrorParser
import javax.inject.Inject

class ClientDashboardRepository @Inject constructor(
    private val api: ZiroApi
) {
    suspend fun getClientDashboard(): Result<ClientDashboardData> {
        return try {
            val response = api.getClientDashboard()
            if (response.success != false && response.data != null) {
                Result.success(response.data.clientData)
            } else {
                Result.failure(Exception("Failed to load dashboard data"))
            }
        } catch (e: retrofit2.HttpException) {
            if (e.code() == 404) {
                Result.failure(Exception("ProfileNotFound"))
            } else {
                val apiError = ApiErrorParser.parse(e)
                Result.failure(Exception(ApiErrorParser.getErrorMessage(apiError)))
            }
        } catch (e: Exception) {
            val apiError = ApiErrorParser.parse(e)
            Result.failure(Exception(ApiErrorParser.getErrorMessage(apiError)))
        }
    }
    suspend fun getWorkoutHistory(limit: Int = 20, cursor: String? = null): Result<com.ziro.fit.model.WorkoutHistoryResponse> {
        return try {
            val response = api.getWorkoutHistory(limit = limit, cursor = cursor)
             if (response.success != false && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception("Failed to load workout history"))
            }
        } catch (e: Exception) {
            val apiError = ApiErrorParser.parse(e)
            Result.failure(Exception(ApiErrorParser.getErrorMessage(apiError)))
        }
    }

    suspend fun getClientProgress(): Result<com.ziro.fit.model.ClientProgressResponse> {
        return try {
            val response = api.getClientProgress()
            if (response.success != false && response.data != null) {
                 Result.success(response.data)
            } else {
                Result.failure(Exception("Failed to load client progress"))
            }
        } catch (e: Exception) {
            val apiError = ApiErrorParser.parse(e)
            Result.failure(Exception(ApiErrorParser.getErrorMessage(apiError)))
        }
    }
}
