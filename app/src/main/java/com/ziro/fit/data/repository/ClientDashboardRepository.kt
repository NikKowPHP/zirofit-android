package com.ziro.fit.data.repository

 import com.ziro.fit.data.remote.ZiroApi
 import com.ziro.fit.model.ClientDashboardData
 import com.ziro.fit.model.ClientAnalyticsResponse
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
                 val enriched = enrichWithVolumeInsights(response.data)
                 Result.success(enriched)
            } else {
                Result.failure(Exception("Failed to load client progress"))
            }
        } catch (e: Exception) {
            val apiError = ApiErrorParser.parse(e)
            Result.failure(Exception(ApiErrorParser.getErrorMessage(apiError)))
        }
    }

    private fun enrichWithVolumeInsights(data: com.ziro.fit.model.ClientProgressResponse): com.ziro.fit.model.ClientProgressResponse {
        val sortedHistory = data.volumeHistory
            .sortedBy { it.date }
            .takeLast(2)

        val insightsMessage: String? = when (sortedHistory.size) {
            0, 1 -> "Log more workouts to see insights."
            else -> {
                val currentVolume = sortedHistory.last().totalVolume
                val previousVolume = sortedHistory.first().totalVolume
                val diff = kotlin.math.round(currentVolume - previousVolume).toInt()
                when {
                    diff > 0 -> "You lifted $diff kg more than last time!"
                    diff < 0 -> "You lifted ${-diff} kg less than last time."
                    else -> "Same volume as last session."
                }
            }
        }

        return data.copy(insightsMessage = insightsMessage)
    }

    suspend fun getActiveProgramProgress(): Result<com.ziro.fit.model.ActiveProgramProgress> {
        return try {
            val response = api.getActiveProgramProgress()
            if (response.success != false && response.data != null) {
                val uiModel = com.ziro.fit.model.ActiveProgramProgress.fromApiResponse(response.data)
                Result.success(uiModel)
            } else {
                Result.failure(Exception("Failed to load active program progress"))
            }
        } catch (e: Exception) {
            val apiError = ApiErrorParser.parse(e)
            Result.failure(Exception(ApiErrorParser.getErrorMessage(apiError)))
        }
    }

    suspend fun getClientAnalytics(): Result<com.ziro.fit.model.ClientAnalyticsResponse> {
        return try {
            val response = api.getClientAnalytics()
            if (response.success != false && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception("Failed to load client analytics"))
            }
        } catch (e: Exception) {
            val apiError = ApiErrorParser.parse(e)
            Result.failure(Exception(ApiErrorParser.getErrorMessage(apiError)))
        }
    }
}
