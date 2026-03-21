package com.ziro.fit.ui.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ziro.fit.model.VolumeDataPoint
import kotlin.math.roundToLong

@Composable
fun InteractiveBarChart(
    data: List<VolumeDataPoint>,
    barColor: Color,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) {
        Card(
            modifier = modifier.fillMaxWidth().height(200.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No data available", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        return
    }

    var selectedIndex by remember { mutableIntStateOf(-1) }
    var touchX by remember { mutableFloatStateOf(0f) }

    val sortedData = data.sortedBy { it.date }
    val volumes = sortedData.map { it.totalVolume }

    val maxVolume = volumes.maxOrNull() ?: 1.0
    val minVolume = volumes.minOrNull() ?: 0.0
    val range = (maxVolume - minVolume).coerceAtLeast(1.0)

    Card(
        modifier = modifier.fillMaxWidth().height(250.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (selectedIndex >= 0 && selectedIndex < sortedData.size) {
                val selectedPoint = sortedData[selectedIndex]
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier.wrapContentSize(),
                        colors = CardDefaults.cardColors(containerColor = barColor.copy(alpha = 0.9f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = selectedPoint.date,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "%.1f kg".format(selectedPoint.totalVolume),
                                style = MaterialTheme.typography.titleSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures { tapOffset ->
                            val chartWidth = size.width
                            val chartHeight = size.height
                            val padding = 16.dp.toPx()

                            val drawWidth = chartWidth - 2 * padding
                            val drawHeight = chartHeight - 2 * padding

                            if (tapOffset.x in padding..(chartWidth - padding) &&
                                tapOffset.y in padding..(chartHeight - padding)) {
                                val x = tapOffset.x - padding
                                val barWidth = drawWidth / sortedData.size
                                val index = (x / barWidth).toInt().coerceIn(0, sortedData.size - 1)
                                selectedIndex = index
                                touchX = padding + index * barWidth + barWidth / 2
                            } else {
                                selectedIndex = -1
                            }
                        }
                    }
                    .pointerInput(Unit) {
                        detectDragGestures { change, _ ->
                            val chartWidth = size.width
                            val padding = 16.dp.toPx()

                            val drawWidth = chartWidth - 2 * padding
                            val barWidth = drawWidth / sortedData.size

                            if (change.position.x in padding..(chartWidth - padding)) {
                                val x = change.position.x - padding
                                val index = (x / barWidth).toInt().coerceIn(0, sortedData.size - 1)
                                selectedIndex = index
                                touchX = padding + index * barWidth + barWidth / 2
                            } else {
                                selectedIndex = -1
                            }
                        }
                    }
            ) {
                val width = size.width
                val height = size.height
                val padding = 16.dp.toPx()

                val drawWidth = width - 2 * padding
                val drawHeight = height - 2 * padding
                val barWidth = drawWidth / sortedData.size
                val barSpacing = 4.dp.toPx()
                val actualBarWidth = barWidth - barSpacing

                sortedData.forEachIndexed { index, volume ->
                    val x = padding + index * barWidth + barSpacing / 2
                    val normalizedVolume = ((volume.totalVolume - minVolume) / range).toFloat()
                    val barHeight = normalizedVolume * drawHeight
                    val y = height - padding - barHeight

                    val isSelected = index == selectedIndex
                    val alpha = if (isSelected) 1.0f else if (selectedIndex >= 0) 0.3f else 1.0f
                    val currentColor = barColor.copy(alpha = alpha)

                    drawRoundRect(
                        color = currentColor,
                        topLeft = Offset(x, y),
                        size = androidx.compose.ui.geometry.Size(actualBarWidth, barHeight),
                        cornerRadius = CornerRadius(4.dp.toPx())
                    )
                }

                if (selectedIndex >= 0 && selectedIndex < sortedData.size) {
                    val x = touchX
                    drawLine(
                        color = barColor.copy(alpha = 0.6f),
                        start = Offset(x, padding),
                        end = Offset(x, height - padding),
                        strokeWidth = 2.dp.toPx()
                    )
                }
            }
        }
    }
}
