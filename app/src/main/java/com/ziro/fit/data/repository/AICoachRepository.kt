package com.ziro.fit.data.repository

import com.ziro.fit.data.remote.ZiroApi
import com.ziro.fit.model.GenerateProgramFromGoalRequest
import com.ziro.fit.model.GenerateProgramFromGoalResponse
import com.ziro.fit.model.RefineGoalRequest
import com.ziro.fit.model.RefineGoalResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AICoachRepository @Inject constructor(
    private val api: ZiroApi
) {
    suspend fun refineGoal(userInput: String): Result<RefineGoalResponse> {
        return try {
            val response = api.refineGoal(RefineGoalRequest(userInput))
            // The AI Coach endpoint might return data without explicit success=true
            if ((response.success == true || response.data != null)) {
                Result.success(response.data!!)
            } else {
                Result.failure(Exception(response.error ?: response.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCurrentUserId(): Result<String> {
        return try {
             val response = api.getMe()
             if (response.data != null) {
                 Result.success(response.data.id)
             } else {
                 Result.failure(Exception("Failed to get user info"))
             }
        } catch (e: Exception) {
             Result.failure(e)
        }
    }

    suspend fun generateProgram(clientId: String, goal: String, metrics: Map<String, Any>): Result<String> {
        return try {
            val response = api.generateProgramFromGoal(
                GenerateProgramFromGoalRequest(
                    clientId = clientId,
                    selectedGoal = goal,
                    metrics = metrics
                )
            )

            if ((response.success == true || response.data != null)) {
                Result.success(response.data!!.programId)
            } else {
                Result.failure(Exception(response.error ?: response.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
