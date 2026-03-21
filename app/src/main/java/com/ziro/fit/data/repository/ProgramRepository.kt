package com.ziro.fit.data.repository

import com.ziro.fit.data.remote.ZiroApi
import com.ziro.fit.model.WorkoutTemplateDto
import com.ziro.fit.util.ApiErrorParser
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProgramRepository @Inject constructor(
    private val api: ZiroApi
) {
    suspend fun searchPrograms(query: String): Result<List<WorkoutTemplateDto>> {
        return try {
            val response = api.getWorkoutTemplates()
            val allTemplates = response.data?.templates
                ?.plus(response.data.systemTemplates)
                ?: emptyList()
            
            val filtered = allTemplates.filter { template ->
                template.name.contains(query, ignoreCase = true) ||
                template.description?.contains(query, ignoreCase = true) == true
            }
            Result.success(filtered)
        } catch (e: Exception) {
            val apiError = ApiErrorParser.parse(e)
            Result.failure(Exception(ApiErrorParser.getErrorMessage(apiError)))
        }
    }
}
