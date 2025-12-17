package com.ziro.fit.data.repository

import com.ziro.fit.data.remote.ZiroApi
import com.ziro.fit.model.*
import com.ziro.fit.util.ApiErrorParser
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrainerRepository @Inject constructor(
    private val api: ZiroApi
) {
    suspend fun getTrainers(search: String? = null): Result<List<TrainerSummary>> {
        return try {
            val response = api.getTrainers(search)
            Result.success(response.data?.trainers ?: emptyList())
        } catch (e: Exception) {
            val apiError = ApiErrorParser.parse(e)
            Result.failure(Exception(ApiErrorParser.getErrorMessage(apiError)))
        }
    }

    suspend fun getPublicTrainerProfile(trainerId: String): Result<PublicTrainerProfileResponse> {
        return try {
            val response = api.getPublicTrainerProfile(trainerId)
            if (response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception("Trainer not found"))
            }
        } catch (e: Exception) {
            val apiError = ApiErrorParser.parse(e)
            Result.failure(Exception(ApiErrorParser.getErrorMessage(apiError)))
        }
    }

    suspend fun getTrainerSchedule(username: String): Result<TrainerScheduleResponse> {
        return try {
            val response = api.getTrainerSchedule(username)
            if (response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception("Schedule not found"))
            }
        } catch (e: Exception) {
            val apiError = ApiErrorParser.parse(e)
            Result.failure(Exception(ApiErrorParser.getErrorMessage(apiError)))
        }
    }
}
