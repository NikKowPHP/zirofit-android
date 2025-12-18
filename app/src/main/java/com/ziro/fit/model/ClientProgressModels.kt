package com.ziro.fit.model

data class WorkoutHistoryResponse(
    val sessions: PaginatedSessionData
)

data class PaginatedSessionData(
    val sessions: List<HistorySession>,
    val nextCursor: String?,
    val hasMore: Boolean
)

data class HistorySession(
    val id: String,
    val startTime: String,
    val endTime: String?,
    val status: String,
    val workoutTemplate: HistoryTemplate?,
    val exerciseLogs: List<HistoryExerciseLog>?
)

data class HistoryExerciseLog(
    val id: String,
    val reps: Int?,
    val weight: Double?,
    val exercise: HistoryExercise
)

data class HistoryExercise(
    val name: String
)

data class HistoryTemplate(
    val name: String
)

data class ClientProgressResponse(
    val volumeHistory: List<VolumeDataPoint>
)

data class VolumeDataPoint(
    val date: String,
    val totalVolume: Double
)
