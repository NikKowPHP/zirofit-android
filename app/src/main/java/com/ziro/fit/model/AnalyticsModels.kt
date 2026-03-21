package com.ziro.fit.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.ziro.fit.model.Exercise

enum class AnalyticsWidgetType(val title: String, val icon: ImageVector) {
    WORKOUTS_PER_WEEK("Workouts Per Week", Icons.Default.DirectionsRun),
    CONSISTENCY("Consistency", Icons.Default.BarChart),
    VOLUME_PROGRESSION("Volume Progression", Icons.Default.TrendingUp),
    MUSCLE_FOCUS("Muscle Focus", Icons.Default.Accessibility),
    PRS("Around the World (PRs)", Icons.Default.EmojiEvents),
    HEAT_MAP("Activity Heat Map", Icons.Default.DateRange),
    INSIGHTS("Personal Insights", Icons.Default.AutoAwesome),
    RECOVERY("Recovery & Load", Icons.Default.Favorite),
    GOAL("Fitness Goal", Icons.Default.TrackChanges)
}

data class AnalyticsWidget(
    val id: String,
    val type: AnalyticsWidgetType,
    val isVisible: Boolean,
    val order: Int
)

data class ClientAnalyticsResponse(
    val heatmap: List<HeatmapData>,
    val volumeHistory: List<VolumeDataPoint>,
    val totalWorkouts: Int,
    val totalVolume: Double,
    val streak: Int,
    val personalRecords: List<PersonalRecord>
)

data class HeatmapData(
    val date: String,
    val count: Int
)

data class PersonalRecord(
    val exerciseName: String,
    val weight: Double,
    val reps: Int,
    val date: String
)

data class DailyTarget(
    val id: String,
    val type: String,
    val goal: Int,
    val current: Int,
    val exerciseId: String?,
    val exerciseName: String?,
    val isCompleted: Boolean,
    val date: String
)

data class CreateDailyTargetRequest(
    val type: String,
    val goal: Int,
    val exerciseId: String?
)

data class UpdateDailyTargetRequest(
    val goal: Int,
    val isCompleted: Boolean
)

data class SignOutRequest(
    val mode: String
)

data class SyncExercisesResponse(
    val exercises: List<Exercise>,
    val deletedIds: List<String>,
    val timestamp: Long
)
