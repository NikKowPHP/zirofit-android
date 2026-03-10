package com.ziro.fit.model

enum class SyncActionType {
    LOG_SET,
    FINISH_WORKOUT
}

data class SyncAction(
    val id: String,
    val type: SyncActionType,
    val payload: String, // JSON encoded payload
    val createdAt: Long
)

data class LogSetPayload(
    val workoutSessionId: String,
    val exerciseId: String,
    val reps: Int,
    val weight: Double,
    val rpe: Double?,
    val order: Int,
    val isCompleted: Boolean,
    val logId: String?
)

data class FinishWorkoutPayload(
    val sessionId: String,
    val notes: String?
)
