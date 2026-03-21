package com.ziro.fit.model

import com.google.gson.annotations.SerializedName

// --- 1. SERVER RESPONSE DTOs ---

data class ServerLiveSessionResponse(
    val id: String,
    val startTime: String,
    val endTime: String?,
    val status: String,
    val notes: String?,
    val createdAt: String?,
    val updatedAt: String?,
    val workoutTemplateId: String?,
    val plannedDate: String?,
    val client: ServerClient?,
    val workoutTemplate: ServerTemplate?, // The Plan
    val exerciseLogs: List<ServerExerciseLog> // The Progress
)

data class ServerClient(
    val id: String,
    val name: String,
    val trainerId: String?,
    val userId: String?
)

data class GetActiveSessionResponse(
    val session: ServerLiveSessionResponse?
)

data class ServerTemplate(
    val id: String,
    val name: String,
    val description: String? = null,
    val exercises: List<ServerTemplateExercise>
)

data class ServerTemplateExercise(
    val id: String, // Template Step ID
    val exerciseId: String?, // Changed to Nullable (missing in JSON)
    val order: Int,
    val targetSets: Int?, // Changed to Nullable (null in JSON)
    val targetReps: String?,
    val restSeconds: Int?,
    val isRest: Boolean? = null, // Indicates if this step is a REST period
    val durationSeconds: Int? = null, // Duration for REST steps
    val notes: String?, // Added notes field to extract data if needed
    val exercise: ServerExerciseInfo? // Changed to Nullable (null in JSON)
)

data class ServerExerciseLog(
    val id: String,
    val createdAt: String?,
    val reps: Int,
    val weight: Double?,
    val order: Int,
    val isCompleted: Boolean?,
    val supersetKey: String?,
    val orderInSuperset: Int?,
    val exercise: ServerExerciseInfo
)

data class ServerExerciseInfo(
    val id: String,
    val name: String,
    val equipment: String?,
    val videoUrl: String?,
    val description: String?
)

// --- 1b. SET STATUS (mirrors iOS SetStatus enum) ---
/**
 * Set type indicators, mirroring iOS SetStatus:
 *   normal   → no indicator
 *   warmUp   → "W" badge
 *   dropSet  → "D" badge
 *   failure  → "F" badge
 */
enum class SetStatus(val indicator: String, val label: String) {
    NORMAL("", "Normal"),
    WARM_UP("W", "Warm Up"),
    DROP_SET("D", "Drop Set"),
    FAILURE("F", "Failure");

    companion object {
        fun fromString(value: String?): SetStatus {
            return when (value?.lowercase()) {
                "warmup", "warm_up", "warm-up", "w" -> WARM_UP
                "dropset", "drop_set", "drop-set", "d" -> DROP_SET
                "failure", "f" -> FAILURE
                else -> NORMAL
            }
        }
    }
}

// --- 1c. NEW RECORD (mirrors iOS NewRecord struct) ---
/**
 * Tracks a personal record broken during a workout.
 * Mirrors iOS NewRecord: recordType, exerciseId, oldRecord, newRecord
 */
data class NewRecord(
    val recordType: RecordType,
    val exerciseId: String,
    val exerciseName: String,
    val oldValue: String,
    val newValue: String
)

enum class RecordType {
    MAX_WEIGHT,
    MAX_REPS,
    MAX_VOLUME
}

// --- 2. UI DOMAIN MODELS (The "Source of Truth" for the UI) ---

data class LiveWorkoutUiModel(
    val id: String,
    val title: String,
    val startTime: String,
    val exercises: List<WorkoutExerciseUi>,
    val clientId: String? = null,
    val clientName: String? = null,
    val totalPackageSessions: Int? = null,
    val remainingPackageSessions: Int? = null
)

data class WorkoutExerciseUi(
    val exerciseId: String,
    val exerciseName: String,
    val targetReps: String?, // Visual guide: "8-12"
    val restSeconds: Int?,
    val sets: List<WorkoutSetUi>,
    // Superset grouping: all exercises in the same superset share the same superSetId
    val superSetId: String? = null
)

data class WorkoutSetUi(
    val logId: String?, // Nullable: If null, it's a "Ghost Set" (planned but not saved)
    val setNumber: Int,
    val weight: String,
    val reps: String,
    val isCompleted: Boolean,
    val order: Int, // 0-indexed position in the list
    val rpe: Double? = null,
    // Set type (warm-up, drop set, failure) — mirrors iOS SetStatus
    val status: SetStatus = SetStatus.NORMAL
)

// --- 3. API REQUEST ---

data class LogSetRequest(
    val workoutSessionId: String,
    val exerciseId: String,
    val reps: Int,
    val weight: Double,
    @SerializedName("order") val order: Int,
    val isCompleted: Boolean? = null,
    val logId: String? = null,
    val rpe: Double? = null,
    // Mirrors iOS logSet parameters: setStatus (warm_up, drop_set, failure)
    val status: String? = null
)

data class LogSetResponse(
    // The server may return newRecords when a PR is broken
    val newRecords: List<NewRecord>? = null
)

data class StartWorkoutRequest(
    val clientId: String?,
    val templateId: String?,
    val plannedSessionId: String?
)

data class FinishWorkoutRequest(
    val workoutSessionId: String,
    val notes: String?
)

data class FinishWorkoutResponse(
    val session: ServerLiveSessionResponse,
    val stats: WorkoutStats?
)

data class WorkoutStats(
    val durationSeconds: Int,
    val volumeKg: Double,
    val setsCompleted: Int,
    val recordsBroken: Int,
    val message: String?,
    val exerciseSummaries: List<ExerciseSummary> = emptyList()
)

data class ExerciseSummary(
    val exerciseId: String,
    val name: String,
    val sets: Int,
    val repsCount: Int,
    val maxWeight: Double
)
      