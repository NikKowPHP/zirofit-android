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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ziro.fit.model.VolumeDataPoint
import kotlin.math.roundToLong

@Composable
fun InteractiveLineChart(
    data: List<VolumeDataPoint>,
    primaryColor: Color,
    gradientColors: List<Color>,
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
                        colors = CardDefaults.cardColors(containerColor = primaryColor.copy(alpha = 0.9f)),
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
                                val index = ((x / drawWidth) * (sortedData.size - 1)).coerceIn(0f, (sortedData.size - 1).toFloat())
                                selectedIndex = index.roundToLong().toInt()
                                touchX = padding + index * (drawWidth / (sortedData.size - 1))
                            } else {
                                selectedIndex = -1
                            }
                        }
                    }
                    .pointerInput(Unit) {
                        detectDragGestures { change, _ ->
                            val chartWidth = size.width
                            val chartHeight = size.height
                            val padding = 16.dp.toPx()

                            val drawWidth = chartWidth - 2 * padding

                            if (change.position.x in padding..(chartWidth - padding)) {
                                val x = change.position.x - padding
                                val index = ((x / drawWidth) * (sortedData.size - 1)).coerceIn(0f, (sortedData.size - 1).toFloat())
                                selectedIndex = index.roundToLong().toInt()
                                touchX = padding + index * (drawWidth / (sortedData.size - 1))
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

                if (sortedData.size >= 2) {
                    val gradientPath = Path().apply {
                        moveTo(padding, height - padding)
                        sortedData.forEachIndexed { index, volume ->
                            val x = padding + index * (drawWidth / (sortedData.size - 1))
                            val normalizedVolume = ((volume.totalVolume - minVolume) / range).toFloat()
                            val y = height - padding - (normalizedVolume * drawHeight)
                            lineTo(x, y)
                        }
                        lineTo(padding + (sortedData.size - 1) * (drawWidth / (sortedData.size - 1)), height - padding)
                        close()
                    }

                    drawPath(
                        path = gradientPath,
                        brush = Brush.verticalGradient(
                            colors = gradientColors.map { it.copy(alpha = 0.4f) },
                            startY = padding,
                            endY = height - padding
                        )
                    )
                }

                if (sortedData.size >= 2) {
                    val linePath = Path().apply {
                        sortedData.forEachIndexed { index, volume ->
                            val x = padding + index * (drawWidth / (sortedData.size - 1))
                            val normalizedVolume = ((volume.totalVolume - minVolume) / range).toFloat()
                            val y = height - padding - (normalizedVolume * drawHeight)

                            if (index == 0) {
                                moveTo(x, y)
                            } else {
                                lineTo(x, y)
                            }
                        }
                    }

                    drawPath(
                        path = linePath,
                        color = primaryColor,
                        style = Stroke(width = 3.dp.toPx())
                    )
                }

                sortedData.forEachIndexed { index, volume ->
                    val x = padding + index * (drawWidth / (sortedData.size - 1))
                    val normalizedVolume = ((volume.totalVolume - minVolume) / range).toFloat()
                    val y = height - padding - (normalizedVolume * drawHeight)

                    val isSelected = index == selectedIndex
                    val pointRadius = if (isSelected) 8.dp.toPx() else 4.dp.toPx()
                    val pointColor = if (isSelected) Color.White else primaryColor

                    drawCircle(
                        color = pointColor,
                        radius = pointRadius,
                        center = Offset(x, y)
                    )

                    if (isSelected) {
                        drawCircle(
                            color = primaryColor,
                            radius = 4.dp.toPx(),
                            center = Offset(x, y)
                        )
                    }
                }

                if (selectedIndex >= 0 && selectedIndex < sortedData.size) {
                    val x = touchX
                    drawLine(
                        color = primaryColor.copy(alpha = 0.6f),
                        start = Offset(x, padding),
                        end = Offset(x, height - padding),
                        strokeWidth = 2.dp.toPx()
                    )
                }
            }
        }
    }
}
