package com.ziro.fit.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

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
    var isVisible: Boolean,
    var order: Int
)
