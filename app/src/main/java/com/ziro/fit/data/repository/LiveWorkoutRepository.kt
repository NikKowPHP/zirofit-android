package com.ziro.fit.data.repository

import com.ziro.fit.data.remote.ZiroApi
import com.ziro.fit.model.Exercise
import com.ziro.fit.model.GetExercisesResponse
import com.ziro.fit.model.LiveWorkoutUiModel
import com.ziro.fit.model.LogSetRequest
import com.ziro.fit.model.ServerLiveSessionResponse
import com.ziro.fit.model.StartWorkoutRequest
import com.ziro.fit.model.WorkoutExerciseUi
import com.ziro.fit.model.WorkoutSetUi
import com.ziro.fit.util.ApiErrorParser
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max

@Singleton
class LiveWorkoutRepository @Inject constructor(
    private val api: ZiroApi
) {
    suspend fun getActiveSession(): Result<LiveWorkoutUiModel?> {
        return try {
            val response = api.getActiveSession()
            val data = response.data!!
            if (data.session != null) {
                Result.success(mapResponseToUiModel(data.session))
            } else {
                // Return null when no session is active
                Result.success(null)
            }
        } catch (e: Exception) {
            val apiError = ApiErrorParser.parse(e)
            Result.failure(Exception(ApiErrorParser.getErrorMessage(apiError)))
        }
    }

    suspend fun startWorkout(clientId: String?, templateId: String?, plannedSessionId: String?): Result<LiveWorkoutUiModel> {
        return try {
            val response = api.startWorkout(StartWorkoutRequest(clientId, templateId, plannedSessionId))
            Result.success(mapResponseToUiModel(response.data!!.session))
        } catch (e: Exception) {
            val apiError = ApiErrorParser.parse(e)
            Result.failure(Exception(ApiErrorParser.getErrorMessage(apiError)))
        }
    }

    suspend fun logSet(sessionId: String, exerciseId: String, reps: Int, weight: Double, order: Int): Result<Unit> {
        return try {
            api.logSet(LogSetRequest(sessionId, exerciseId, reps, weight, order))
            Result.success(Unit)
        } catch (e: Exception) {
            val apiError = ApiErrorParser.parse(e)
            Result.failure(Exception(ApiErrorParser.getErrorMessage(apiError)))
        }
    }

    suspend fun finishSession(sessionId: String, notes: String?): Result<Unit> {
        return try {
            api.finishSession(mapOf("workoutSessionId" to sessionId, "notes" to (notes ?: "")))
            Result.success(Unit)
        } catch (e: Exception) {
            val apiError = ApiErrorParser.parse(e)
            Result.failure(Exception(ApiErrorParser.getErrorMessage(apiError)))
        }
    }
    
    suspend fun getExercises(query: String?, page: Int = 1): Result<GetExercisesResponse> {
        return try {
            val response = api.getExercises(search = query, page = page)
            Result.success(response.data!!)
        } catch (e: Exception) {
            val apiError = ApiErrorParser.parse(e)
            Result.failure(Exception(ApiErrorParser.getErrorMessage(apiError)))
        }
    }

    private fun mapResponseToUiModel(data: ServerLiveSessionResponse): LiveWorkoutUiModel {
        // 1. Group actual logs by Exercise ID
        val logsByExercise = data.exerciseLogs.groupBy { it.exercise.id }

        // 2. Build list from Template (The "Planned" Exercises)
        val uiExercises = mutableListOf<WorkoutExerciseUi>()
        
        // Track which exercises we've handled to identify ad-hoc ones later
        val processedExerciseIds = mutableSetOf<String>()

        data.workoutTemplate?.exercises?.sortedBy { it.order }?.forEach { templateStep ->
            processedExerciseIds.add(templateStep.exerciseId)
            
            val logsForThisExercise = logsByExercise[templateStep.exerciseId] ?: emptyList()
            
            // CRITICAL LOGIC: Merging Plan + Logs
            // If plan says 3 sets, but we logged 1, we show 1 real + 2 ghosts.
            // If plan says 3 sets, but we logged 4, we show 4 real.
            
            val targetSetsCount = templateStep.targetSets
            val actualLogsCount = logsForThisExercise.size
            
            // Determine total rows to show (at least as many as logged, or up to target)
            val setsToShowCount = max(targetSetsCount, actualLogsCount)
            // Ensure at least one set is shown if everything else is empty
            val finalSetsCount = max(setsToShowCount, 1)

            val setsUi = (0 until finalSetsCount).map { index ->
                // Try to find a real log for this index
                val existingLog = logsForThisExercise.find { it.order == index }
                
                if (existingLog != null) {
                    // Real Log (Saved in DB)
                    WorkoutSetUi(
                        logId = existingLog.id,
                        setNumber = index + 1,
                        weight = existingLog.weight?.toString() ?: "",
                        reps = existingLog.reps.toString(),
                        isCompleted = true, // It exists, so it's logged
                        order = index
                    )
                } else {
                    // Ghost Set (Placeholder based on template)
                    WorkoutSetUi(
                        logId = null, // No ID yet
                        setNumber = index + 1,
                        weight = "", // Start empty
                        reps = "", // Start empty
                        isCompleted = false,
                        order = index
                    )
                }
            }

            uiExercises.add(
                WorkoutExerciseUi(
                    exerciseId = templateStep.exerciseId,
                    exerciseName = templateStep.exercise.name,
                    targetReps = templateStep.targetReps,
                    restSeconds = templateStep.restSeconds,
                    sets = setsUi
                )
            )
        }

        // 3. Handle Ad-Hoc Exercises (Logged but not in Template)
        logsByExercise.forEach { (exerciseId, logs) ->
            if (!processedExerciseIds.contains(exerciseId)) {
                val firstLog = logs.first()
                
                val setsUi = logs.sortedBy { it.order }.mapIndexed { index, log ->
                    WorkoutSetUi(
                        logId = log.id,
                        setNumber = index + 1,
                        weight = (log.weight ?: 0.0).toString(),
                        reps = log.reps.toString(),
                        isCompleted = true,
                        order = log.order
                    )
                }

                uiExercises.add(
                    WorkoutExerciseUi(
                        exerciseId = exerciseId,
                        exerciseName = firstLog.exercise.name,
                        targetReps = null, // No target for ad-hoc
                        restSeconds = null, // No rest target for ad-hoc by default, effectively freestyle
                        sets = setsUi
                    )
                )
            }
        }

        return LiveWorkoutUiModel(
            id = data.id,
            title = data.workoutTemplate?.name ?: "Freestyle Workout",
            startTime = data.startTime,
            exercises = uiExercises
        )
    }
}