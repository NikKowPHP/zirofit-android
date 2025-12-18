package com.ziro.fit.ui.workouts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ziro.fit.model.WorkoutStats
import com.ziro.fit.ui.theme.ZirofitTheme

@Composable
fun WorkoutSuccessContent(
    stats: WorkoutStats,
    onDone: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min) // Allow bottom sheet to be tall
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Success Icon / Header
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color(0xFF4CAF50).copy(alpha = 0.2f)), // Greenish glow background
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Success",
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(48.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Workout Complete!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        if (!stats.message.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stats.message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 2. Stats Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Timer,
                label = "Duration",
                value = formatDuration(stats.durationSeconds),
                color = Color(0xFF2196F3) 
            )
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.FitnessCenter,
                label = "Volume",
                value = "${stats.volumeKg.toInt()} kg",
                color = Color(0xFF9C27B0)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Check, 
                label = "Sets",
                value = stats.setsCompleted.toString(),
                color = Color(0xFFFF9800)
            )
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.EmojiEvents,
                label = "Records",
                value = stats.recordsBroken.toString(),
                color = Color(0xFFFFC107)
            )
        }

        if (stats.exerciseSummaries.isNotEmpty()) {
            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Workout Summary",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Use a Column for exercises instead of a nested LazyColumn if inside a BottomSheet that might scroll
            stats.exerciseSummaries.forEach { summary ->
                ExerciseSummaryRow(summary)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // 3. Actions
        Button(
            onClick = onDone,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Done",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun ExerciseSummaryRow(summary: com.ziro.fit.model.ExerciseSummary) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = summary.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${summary.sets} sets • ${summary.repsCount} total reps",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        if (summary.maxWeight > 0) {
            Text(
                text = "${summary.maxWeight.toInt()} kg",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatDuration(seconds: Int): String {
    val minutes = seconds / 60
    return "${minutes}m"
}

@Preview
@Composable
fun WorkoutSuccessPreview() {
    ZirofitTheme {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
             WorkoutSuccessContent(
                stats = WorkoutStats(
                    durationSeconds = 2700,
                    volumeKg = 15000.0,
                    setsCompleted = 24,
                    recordsBroken = 3,
                    message = "Great job! crushed it!"
                ),
                onDone = {}
            )
        }
    }
}
