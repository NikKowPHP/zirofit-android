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
            if ((response.success ?: true) && response.data != null) {
                emit(Result.success(response.data!!.assessments))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to fetch assessments")))
            }
        } catch (e: Exception) {
            emit(Result.failure(Exception(ApiErrorParser.getErrorMessage(ApiErrorParser.parse(e)))))
        }
    }

    suspend fun createAssessment(name: String, description: String?, unit: String): Flow<Result<Assessment>> = flow {
        try {
            val request = CreateAssessmentRequest(name, description, unit)
            val response = api.createAssessment(request)
            if ((response.success ?: true) && response.data != null) {
                emit(Result.success(response.data!!.newAssessment))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to create assessment")))
            }
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
                // If data is null but success is true (some APIs might do this), consider it success but we need the object.
                // Assuming the API returns the updated object or we might need to re-fetch if it doesn't.
                // For now, fail if no data, or if the API returns just "success" without data, we might need to handle it.
                // Based on ZiroApi definition: ApiResponse<Assessment>
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
