package com.ziro.fit.data.repository

import com.ziro.fit.data.remote.ZiroApi
import com.ziro.fit.model.CreateExerciseRequest
import com.ziro.fit.model.Exercise
import com.ziro.fit.util.ApiErrorParser
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExerciseRepository @Inject constructor(
    private val api: ZiroApi
) {
    suspend fun getExercises(query: String?): Result<List<Exercise>> {
        return try {
            val response = api.getExercises(search = query)
            Result.success(response.data!!.exercises)
        } catch (e: Exception) {
            val apiError = ApiErrorParser.parse(e)
            Result.failure(Exception(ApiErrorParser.getErrorMessage(apiError)))
        }
    }

    suspend fun createExercise(name: String, muscleGroup: String?, equipment: String?, videoUrl: String?): Result<Exercise> {
        return try {
            val request = CreateExerciseRequest(name, muscleGroup, equipment, videoUrl)
            val response = api.createExercise(request)
            Result.success(response.data!!.exercise)
        } catch (e: Exception) {
            val apiError = ApiErrorParser.parse(e)
            Result.failure(Exception(ApiErrorParser.getErrorMessage(apiError)))
        }
    }

    suspend fun updateExercise(id: String, name: String, muscleGroup: String?, equipment: String?, videoUrl: String?): Result<Unit> {
        return try {
            val request = CreateExerciseRequest(name, muscleGroup, equipment, videoUrl)
            api.updateExercise(id, request)
            Result.success(Unit)
        } catch (e: Exception) {
            val apiError = ApiErrorParser.parse(e)
            Result.failure(Exception(ApiErrorParser.getErrorMessage(apiError)))
        }
    }

    suspend fun deleteExercise(id: String): Result<Unit> {
        return try {
            api.deleteExercise(id)
            Result.success(Unit)
        } catch (e: Exception) {
            val apiError = ApiErrorParser.parse(e)
            Result.failure(Exception(ApiErrorParser.getErrorMessage(apiError)))
        }
    }
}
