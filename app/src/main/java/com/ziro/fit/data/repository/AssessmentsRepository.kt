package com.ziro.fit.data.repository

import com.ziro.fit.data.remote.ZiroApi
import com.ziro.fit.model.Assessment
import com.ziro.fit.model.CreateAssessmentRequest
import com.ziro.fit.model.UpdateAssessmentRequest
import com.ziro.fit.util.ApiErrorParser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AssessmentsRepository @Inject constructor(
    private val api: ZiroApi
) {
    suspend fun getAssessments(): Flow<Result<List<Assessment>>> = flow {
        try {
            val response = api.getAssessments()
            // Direct response without ApiResponse wrapper check
            emit(Result.success(response.assessments))
        } catch (e: Exception) {
            emit(Result.failure(Exception(ApiErrorParser.getErrorMessage(ApiErrorParser.parse(e)))))
        }
    }

    suspend fun createAssessment(name: String, description: String?, unit: String): Flow<Result<Assessment>> = flow {
        try {
            val request = CreateAssessmentRequest(name, description, unit)
            val response = api.createAssessment(request)
            // Direct response
            emit(Result.success(response.assessment))
        } catch (e: Exception) {
            emit(Result.failure(Exception(ApiErrorParser.getErrorMessage(ApiErrorParser.parse(e)))))
        }
    }

    suspend fun updateAssessment(id: String, name: String, description: String?, unit: String): Flow<Result<Assessment>> = flow {
        try {
            val request = UpdateAssessmentRequest(name, description, unit)
            val response = api.updateAssessment(id, request)
            if ((response.success ?: true) && response.data != null) {
                emit(Result.success(response.data!!))
            } else {
                 emit(Result.failure(Exception(response.message ?: "Failed to update assessment")))
            }
        } catch (e: Exception) {
             emit(Result.failure(Exception(ApiErrorParser.getErrorMessage(ApiErrorParser.parse(e)))))
        }
    }

    suspend fun deleteAssessment(id: String): Flow<Result<Unit>> = flow {
        try {
            val response = api.deleteAssessment(id)
            if (response.success ?: true) {
                emit(Result.success(Unit))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to delete assessment")))
            }
        } catch (e: Exception) {
             emit(Result.failure(Exception(ApiErrorParser.getErrorMessage(ApiErrorParser.parse(e)))))
        }
    }
}
