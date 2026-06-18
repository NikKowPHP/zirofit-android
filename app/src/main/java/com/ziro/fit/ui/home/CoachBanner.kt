package com.ziro.fit.ui.home

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun CoachBanner(
    onTap: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    SwipeableGradientBanner(
        onTap = onTap,
        onDismiss = onDismiss,
        modifier = modifier,
        colors = listOf(Color(0xFF2196F3), Color(0xFF9C27B0)),
        title = "Need a Coach?",
        subtitle = "Browse pro trainers or try our AI.",
        emoji = "\uD83C\uDFC3"
    )
}

@Composable
fun CheckInBanner(
    isComplete: Boolean,
    onTap: () -> Unit,
    onDismiss: () -> Unit,
    hasTrainer: Boolean,
    modifier: Modifier = Modifier
) {
    if (isComplete) {
        SwipeableGradientBanner(
            onTap = onTap,
            onDismiss = onDismiss,
            modifier = modifier,
            colors = listOf(Color(0xFF4CAF50), Color(0xFF388E3C)),
            title = "Check-in Complete",
            subtitle = "Great job! Your trainer will review it shortly.",
            emoji = "\u2714\uFE0F"
        )
    } else {
        SwipeableGradientBanner(
            onTap = onTap,
            onDismiss = onDismiss,
            modifier = modifier,
            colors = listOf(Color(0xFFFF9800), Color(0xFFE91E63)),
            title = "Weekly Check-in",
            subtitle = if (hasTrainer) "Update your trainer on your progress"
                       else "Track your weekly progress",
            emoji = "\u2705",
            showShadow = true
        )
    }
}

@Composable
private fun SwipeableGradientBanner(
    onTap: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    colors: List<Color>,
    title: String,
    subtitle: String,
    emoji: String,
    showShadow: Boolean = false
) {
    var offsetX by remember { mutableStateOf(0f) }
    var isDismissed by remember { mutableStateOf(false) }

    if (isDismissed) return

    val animatedOffset by animateDpAsState(
        targetValue = offsetX.dp,
        label = "swipeOffset"
    )

    val opacity = (1f - abs(offsetX) / 300f).coerceIn(0.4f, 1f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .offset { IntOffset(offsetX.roundToInt(), 0) }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (abs(offsetX) > 60) {
                            isDismissed = true
                            onDismiss()
                        } else {
                            offsetX = 0f
                        }
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        offsetX += dragAmount
                    }
                )
            }
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            shadowElevation = if (showShadow) 8.dp else 0.dp,
            color = Color.Transparent
        ) {
            Button(
                onClick = onTap,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 80.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(colors = colors),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = title,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = subtitle,
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                        Text(
                            text = emoji,
                            fontSize = 32.sp
                        )
                    }
                }
            }
        }
    }
}
