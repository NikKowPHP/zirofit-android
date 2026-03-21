package com.ziro.fit.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text

/**
 * A compact badge that indicates a percentage trend with an arrow icon and color.
 *
 * Displays:
 * - An up-arrow for non-negative trends and a down-arrow for negative trends
 * - The percentage value with an explicit sign (e.g., +15%, -5%, +0%)
 * - Color-coding: Green for positive, Red for negative, Gray for zero
 *
 * This badge is designed to fit inline within analytics cards.
 *
 * @param trend The percentage trend. Positive for growth, negative for decline.
 * @param modifier Optional modifier to apply to the badge container.
 */
@Composable
fun TrendBadge(
    trend: Double,
    modifier: Modifier = Modifier
) {
    // Choose icon and color based on the trend value
    val isNonNegative = trend >= 0
    val icon = if (isNonNegative) Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward

    // Use a semantic color: green for positive, red for negative, gray for zero
    val color = when {
        trend > 0 -> Color(0xFF16A34A) // Strong Green
        trend < 0 -> Color(0xFFDC2626) // Strong Red
        else -> Color.Gray
    }

    // Format percentage with explicit sign
    val sign = if (trend >= 0) "+" else ""
    val text = "$sign${trend.toInt()}%"

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color
        )
        Text(
            text = text,
            color = color
        )
    }
}
