package com.ziro.fit.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ziro.fit.model.DailyTarget
import com.ziro.fit.ui.theme.StrongTextPrimary
import com.ziro.fit.ui.theme.StrongTextSecondary

@Composable
fun DailyTargetsSection(
    targets: List<DailyTarget>,
    onAddTarget: () -> Unit,
    onDeleteTarget: (DailyTarget) -> Unit,
    onAddProgress: (DailyTarget, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Daily Targets",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = StrongTextPrimary,
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = onAddTarget) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Target",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        if (targets.isEmpty()) {
            // Empty state
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No daily targets set",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = onAddTarget) {
                        Text("Set a Daily Target", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                targets.forEach { target ->
                    DailyTargetCard(
                        target = target,
                        onDelete = { onDeleteTarget(target) },
                        onAddProgress = { amount -> onAddProgress(target, amount) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DailyTargetCard(
    target: DailyTarget,
    onDelete: () -> Unit,
    onAddProgress: (Int) -> Unit
) {
    val progress = if (target.goal > 0) (target.current.toFloat() / target.goal).coerceIn(0f, 1f) else 0f
    val progressPercent = (progress * 100).toInt()

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = androidx.compose.ui.graphics.SolidColor(Color.White.copy(alpha = 0.05f))
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = target.exerciseName ?: target.type,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = StrongTextPrimary
                        )
                    }

                    Text(
                        text = "${target.current} / ${target.goal} reps today",
                        fontSize = 11.sp,
                        color = StrongTextSecondary
                    )
                }

                // Progress ring
                Box(
                    modifier = Modifier.size(44.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxSize(),
                        color = Color.Transparent,
                        trackColor = Color.Gray.copy(alpha = 0.1f),
                        strokeWidth = 4.dp,
                        strokeCap = StrokeCap.Round
                    )
                    // Overlay gradient progress
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = Color.Transparent,
                        strokeWidth = 4.dp,
                        strokeCap = StrokeCap.Round
                    )
                    Text(
                        text = "$progressPercent%",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick-add buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(5, 10, 20).forEach { amount ->
                    OutlinedButton(
                        onClick = { onAddProgress(amount) },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 8.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("+$amount", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                IconButton(
                    onClick = { onAddProgress(1) },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primary),
                    colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add 1",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
