package com.ziro.fit.ui.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ziro.fit.model.ClientProgressResponse
import com.ziro.fit.model.Measurement
import com.ziro.fit.model.VolumeDataPoint
import com.ziro.fit.model.ExercisePerformance
import com.ziro.fit.model.FavoriteExercise
import com.ziro.fit.model.WorstPerformingExercise
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ClientStatisticsContent(
    progress: ClientProgressResponse?,
    measurements: List<Measurement>,
    isLoading: Boolean
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

            // 6. Weight Chart
            if (measurements.isNotEmpty()) {
                Text(
                    text = "Weight Progress",
                    style = MaterialTheme.typography.titleLarge
                )
                WeightChart(measurements)
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
