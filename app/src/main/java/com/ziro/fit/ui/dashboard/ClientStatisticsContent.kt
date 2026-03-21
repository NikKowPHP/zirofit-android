package com.ziro.fit.ui.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.LocalDate
import com.ziro.fit.model.ClientProgressResponse
import com.ziro.fit.model.Measurement
import com.ziro.fit.model.VolumeDataPoint
import com.ziro.fit.model.ExercisePerformance
import com.ziro.fit.model.FavoriteExercise
import com.ziro.fit.model.WorstPerformingExercise
import com.ziro.fit.model.AnalyticsWidget
import com.ziro.fit.model.AnalyticsWidgetType
import com.ziro.fit.model.FitnessGoal
import com.ziro.fit.service.WidgetStateManager


@Composable
fun ClientStatisticsContent(
    progress: ClientProgressResponse?,
    measurements: List<Measurement>,
    isLoading: Boolean,
    widgets: List<AnalyticsWidget>? = null
) {
    val scrollState = rememberScrollState()

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Determine active widgets order
            val activeWidgets = (widgets ?: emptyList()).filter { it.isVisible }.sortedBy { it.order }
            
            // Fallback to standard render if widgets are empty
            if (activeWidgets.isEmpty()) {
                // 1. Overview Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatCard(
                        title = "Recent Volume",
                        value = progress?.volumeHistory?.lastOrNull()?.totalVolume?.let { "%.0f kg".format(it) } ?: "0 kg",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Latest Weight",
                        value = measurements.maxByOrNull { it.measurementDate }?.weightKg?.let { "${it}kg" } ?: "N/A",
                        modifier = Modifier.weight(1f)
                    )
                }

                // 2. Favorite Exercises
                if (progress?.favoriteExercises?.isNotEmpty() == true) {
                    Text(
                        text = "Favorite Exercises",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        progress.favoriteExercises.forEach { exercise ->
                            FavoriteExerciseItem(exercise)
                        }
                    }
                }

                // 3. Worst Performing Exercises
                if (progress?.worstPerformingExercises?.isNotEmpty() == true) {
                    Text(
                        text = "Areas for Improvement",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        progress.worstPerformingExercises.forEach { exercise ->
                            WorstPerformingExerciseItem(exercise)
                        }
                    }
                }

                // 4. Exercise Performance
                if (progress?.exercisePerformance?.isNotEmpty() == true) {
                    Text(
                        text = "Exercise Performance",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        progress.exercisePerformance.forEach { exercise ->
                            ExercisePerformanceItem(exercise)
                        }
                    }
                }

                 // 5. Volume Chart
                 if (progress?.volumeHistory?.isNotEmpty() == true) {
                     Text(
                         text = "Volume Progress (Last 30 Sessions)",
                         style = MaterialTheme.typography.titleLarge
                     )
                     VolumeChart(progress.volumeHistory)
                 } else {
                      Text(text = "No volume data available.", style = MaterialTheme.typography.bodyMedium)
                 }

                 // 6. Personal Insights (if available)
                 val insightsMessage = progress?.insightsMessage
                 if (!insightsMessage.isNullOrBlank()) {
                     Card(
                         modifier = Modifier.fillMaxWidth(),
                         colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                     ) {
                         Column(
                             modifier = Modifier.padding(16.dp),
                             horizontalAlignment = Alignment.CenterHorizontally
                         ) {
                             Text(
                                 text = "Personal Insights",
                                 style = MaterialTheme.typography.titleMedium,
                                 fontWeight = FontWeight.Bold
                             )
                             Spacer(modifier = Modifier.height(8.dp))
                             Text(
                                 text = insightsMessage,
                                 style = MaterialTheme.typography.bodyLarge,
                                 textAlign = TextAlign.Center
                             )
                         }
                     }
                 }

                 // 7. Weight Chart
                if (measurements.isNotEmpty()) {
                    Text(
                        text = "Weight Progress",
                        style = MaterialTheme.typography.titleLarge
                    )
                    WeightChart(measurements)
                }
            } else {
                // Dynamic rendering
                activeWidgets.forEach { widget ->
                    when (widget.type) {
                        AnalyticsWidgetType.WORKOUTS_PER_WEEK -> {
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                StatCard("Recent Volume", progress?.volumeHistory?.lastOrNull()?.totalVolume?.let { "%.0f kg".format(it) } ?: "0 kg", Modifier.weight(1f))
                                StatCard("Latest Weight", measurements.maxByOrNull { it.measurementDate }?.weightKg?.let { "${it}kg" } ?: "N/A", Modifier.weight(1f))
                            }
                        }
                        AnalyticsWidgetType.VOLUME_PROGRESSION -> {
                            if (progress?.volumeHistory?.isNotEmpty() == true) {
                                Text(widget.type.title, style = MaterialTheme.typography.titleLarge)
                                InteractiveLineChart(
                                    data = progress.volumeHistory,
                                    primaryColor = com.ziro.fit.ui.theme.StrongBlue,
                                    gradientColors = listOf(com.ziro.fit.ui.theme.StrongBlue.copy(alpha = 0.3f), com.ziro.fit.ui.theme.StrongBlue.copy(alpha = 0.0f))
                                )
                            }
                        }
                        AnalyticsWidgetType.MUSCLE_FOCUS -> {
                            if (progress?.favoriteExercises?.isNotEmpty() == true) {
                                Text(widget.type.title, style = MaterialTheme.typography.titleLarge)
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    progress.favoriteExercises.forEach { FavoriteExerciseItem(it) }
                                }
                            }
                        }
                        AnalyticsWidgetType.PRS -> {
                            if (progress?.exercisePerformance?.isNotEmpty() == true) {
                                Text(widget.type.title, style = MaterialTheme.typography.titleLarge)
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    progress.exercisePerformance.forEach { ExercisePerformanceItem(it) }
                                }
                            }
                        }
                        AnalyticsWidgetType.HEAT_MAP -> {
                            Text(widget.type.title, style = MaterialTheme.typography.titleLarge)
                            HeatMapWidget(progress)
                        }
                        AnalyticsWidgetType.INSIGHTS -> {
                            val message = progress?.insightsMessage
                            if (!message.isNullOrBlank()) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "Personal Insights",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = message,
                                            style = MaterialTheme.typography.bodyLarge,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            } else {
                                // Fallback if no message (shouldn't happen ideally)
                                Text("Personal Insights", style = MaterialTheme.typography.titleLarge)
                                Text(
                                    "Log more workouts to see personalized insights.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        AnalyticsWidgetType.GOAL -> {
                            var showGoalSheet by remember { mutableStateOf(false) }
                            val coroutineScope = rememberCoroutineScope()
                            
                            val context = androidx.compose.ui.platform.LocalContext.current
                            val widgetStateManager = remember { 
                                com.ziro.fit.service.WidgetStateManager(
                                    context.applicationContext as android.content.Context
                                ) 
                            }
                            
                            val fitnessGoal by widgetStateManager.fitnessGoal.collectAsState(initial = null)
                            
                            GoalWidget(
                                goalTitle = fitnessGoal?.title ?: "Your Goal",
                                targetValue = fitnessGoal?.targetValue ?: 100.0,
                                currentValue = fitnessGoal?.currentValue ?: 0.0,
                                unit = fitnessGoal?.unit ?: "kg",
                                onEditClick = { showGoalSheet = true }
                            )
                            
                            if (showGoalSheet) {
                                GoalSettingSheet(
                                    existingGoal = fitnessGoal,
                                    onDismiss = { showGoalSheet = false },
                                    onSave = { goal ->
                                        coroutineScope.launch {
                                            widgetStateManager.saveFitnessGoal(goal)
                                        }
                                        showGoalSheet = false
                                    }
                                )
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun FavoriteExerciseItem(exercise: FavoriteExercise) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = exercise.exerciseName, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = "${exercise.frequency} times",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun WorstPerformingExerciseItem(exercise: WorstPerformingExercise) {
    Card(
        modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.error.copy(alpha=0.5f), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = exercise.exerciseName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error
            )
            Text(
                text = exercise.issue,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

@Composable
fun ExercisePerformanceItem(exercise: ExercisePerformance) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = exercise.exerciseName,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                 Column {
                     Text(text = "Max Wgt", style = MaterialTheme.typography.labelSmall)
                     Text(text = exercise.maxWeight?.let { "$it kg" } ?: "-", style = MaterialTheme.typography.bodyMedium)
                 }
                 Column {
                     Text(text = "Max Reps", style = MaterialTheme.typography.labelSmall)
                     Text(text = exercise.maxReps?.let { "$it" } ?: "-", style = MaterialTheme.typography.bodyMedium)
                 }
                 Column {
                     Text(text = "Max Vol", style = MaterialTheme.typography.labelSmall)
                     Text(text = exercise.maxVolume?.let { "$it kg" } ?: "-", style = MaterialTheme.typography.bodyMedium)
                 }
                 Column {
                     Text(text = "Last", style = MaterialTheme.typography.labelSmall)
                     Text(text = exercise.lastPerformed?.let { it.take(10) } ?: "-", style = MaterialTheme.typography.bodyMedium)
                 }
            }
        }
    }
}

@Composable
fun VolumeChart(volumeData: List<VolumeDataPoint>) {
    val volumes = volumeData.map { it.totalVolume }

    if (volumes.size < 2) {
        Text("Not enough data for chart.", style = MaterialTheme.typography.bodySmall)
        return
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Box(modifier = Modifier.padding(16.dp).fillMaxSize()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val maxVolume = volumes.maxOrNull() ?: 100.0
                val minVolume = volumes.minOrNull() ?: 0.0
                val range = (maxVolume - minVolume).coerceAtLeast(1.0)

                val width = size.width
                val height = size.height

                val path = Path()

                volumes.forEachIndexed { index, volume ->
                    val x = index * (width / (volumes.size - 1))
                    val normalizedVolume = (volume - minVolume) / range
                    val y = height - (normalizedVolume * height)

                    if (index == 0) {
                        path.moveTo(x, y.toFloat())
                    } else {
                        path.lineTo(x, y.toFloat())
                    }

                    // Draw points
                    drawCircle(
                        color = Color(0xFF6200EE), // Primary Purple
                        radius = 4.dp.toPx(),
                        center = Offset(x, y.toFloat())
                    )
                }

                drawPath(
                    path = path,
                    color = Color(0xFF6200EE),
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }
    }
}

@Composable
fun WeightChart(measurements: List<Measurement>) {
    val sortedMeasurements = measurements.sortedBy { it.measurementDate }
    val weights = sortedMeasurements.mapNotNull { it.weightKg }

    if (weights.size < 2) {
        Text("Not enough data for chart.", style = MaterialTheme.typography.bodySmall)
        return
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Box(modifier = Modifier.padding(16.dp).fillMaxSize()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val maxWeight = weights.maxOrNull() ?: 100.0
                val minWeight = weights.minOrNull() ?: 0.0
                val range = (maxWeight - minWeight).coerceAtLeast(1.0)

                val width = size.width
                val height = size.height

                val path = Path()

                weights.forEachIndexed { index, weight ->
                    val x = index * (width / (weights.size - 1))
                    val normalizedWeight = (weight - minWeight) / range
                    val y = height - (normalizedWeight * height)

                    if (index == 0) {
                        path.moveTo(x, y.toFloat())
                    } else {
                        path.lineTo(x, y.toFloat())
                    }

                    // Draw points
                    drawCircle(
                        color = Color.Blue,
                        radius = 4.dp.toPx(),
                        center = Offset(x, y.toFloat())
                    )
                }

                drawPath(
                    path = path,
                    color = Color.Blue,
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }
    }
}

@Composable
fun HeatMapWidget(progress: ClientProgressResponse?) {
    val heatmapData = progress?.heatmap ?: emptyList()
    val totalWorkouts = heatmapData.sumOf { it.count }
    val today = java.time.LocalDate.now()
    val dateCountMap = heatmapData.associate { it.date to it.count }
    val days = (0 until 365).map { today.minusDays(it.toLong()) }.reversed()
    val weeks = days.chunked(7)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Activity Heat Map",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "$totalWorkouts workouts",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Column(
                    modifier = Modifier.width(20.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    listOf("Mon", "", "Wed", "", "Fri", "", "").forEach { label ->
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.height(10.dp)
                        )
                    }
                }

                weeks.forEach { week ->
                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        val firstDayOfWeek = week.first()
                        val monthLabel = if (firstDayOfWeek.dayOfMonth <= 7) {
                            firstDayOfWeek.month.toString().take(3)
                        } else {
                            ""
                        }
                        if (monthLabel.isNotEmpty()) {
                            Text(
                                text = monthLabel,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.width(10.dp)
                            )
                        } else {
                            Spacer(modifier = Modifier.width(10.dp))
                        }

                        week.forEach { date ->
                            val count = dateCountMap[date.toString()] ?: 0
                            val color = getHeatmapColor(count)
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(color, RoundedCornerShape(2.dp))
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Less",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                listOf(0, 1, 2, 3, 4).forEach { level ->
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(getHeatmapColor(level), RoundedCornerShape(2.dp))
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "More",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

fun getHeatmapColor(count: Int): Color {
    return when {
        count == 0 -> Color.Gray.copy(alpha = 0.1f)
        count == 1 -> Color.Blue.copy(alpha = 0.3f)
        count == 2 -> Color.Blue.copy(alpha = 0.5f)
        count == 3 -> Color.Blue.copy(alpha = 0.7f)
        count >= 4 -> Color.Blue.copy(alpha = 1.0f)
        else -> Color.Gray.copy(alpha = 0.1f)
    }
}
