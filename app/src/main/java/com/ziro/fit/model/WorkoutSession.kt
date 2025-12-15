package com.ziro.fit.model

import com.google.gson.annotations.SerializedName

// --- 1. SERVER RESPONSE DTOs ---

data class ServerLiveSessionResponse(
    val id: String,
    val startTime: String,
    val status: String,
    val notes: String?,
    val workoutTemplate: ServerTemplate?, // The Plan
    val exerciseLogs: List<ServerExerciseLog> // The Progress
)

data class ServerTemplate(
    val id: String,
    val name: String,
    val exercises: List<ServerTemplateExercise>
)

data class ServerTemplateExercise(
    val id: String, // Template Step ID
    val exerciseId: String,
    val order: Int,
    val targetSets: Int, // Important: How many sets are planned?
    val targetReps: String?,
    val exercise: ServerExerciseInfo
)

data class ServerExerciseLog(
    val id: String,
    val exerciseId: String,
    val reps: Int,
    val weight: Double?,
    val order: Int,
    val isCompleted: Boolean,
    val exercise: ServerExerciseInfo
)

data class ServerExerciseInfo(
    val id: String,
    val name: String,
    val equipment: String?
)

// --- 2. UI DOMAIN MODELS (The "Source of Truth" for the UI) ---

data class LiveWorkoutUiModel(
    val id: String,
    val title: String,
    val startTime: String,
    val exercises: List<WorkoutExerciseUi>
)

data class WorkoutExerciseUi(
    val exerciseId: String,
    val exerciseName: String,
    val targetReps: String?, // Visual guide: "8-12"
    val sets: List<WorkoutSetUi>
)

data class WorkoutSetUi(
    val logId: String?, // Nullable: If null, it's a "Ghost Set" (planned but not saved)
    val setNumber: Int,
    val weight: String,
    val reps: String,
    val isCompleted: Boolean,
    val order: Int // 0-indexed position in the list
)

// --- 3. API REQUEST ---

data class LogSetRequest(
    val workoutSessionId: String,
    val exerciseId: String,
    val reps: Int,
    val weight: Double,
    @SerializedName("order") val order: Int
)
      