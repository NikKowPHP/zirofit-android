package com.ziro.fit.ui.components.overlays

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ziro.fit.util.BarbellLogic
import com.ziro.fit.util.PlateCalculator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlateCalculatorBottomSheet(
    onDismiss: () -> Unit,
    initialWeight: Double = 60.0,
    useKg: Boolean = true
) {
    var targetWeight by remember { mutableStateOf(initialWeight.toString()) }
    var barWeight by remember { mutableStateOf(if (useKg) 20.0 else 45.0) }
    var isKg by remember { mutableStateOf(useKg) }

    val plates = remember(targetWeight, barWeight, isKg) {
        val weight = targetWeight.toDoubleOrNull() ?: 0.0
        PlateCalculator.calculatePlatesPerSide(
            targetWeight = weight,
            barWeight = barWeight,
            availablePlates = if (isKg) BarbellLogic.availablePlatesKg else BarbellLogic.availablePlatesLbs
        )
    }

    val actualWeight = remember(plates, barWeight) {
        PlateCalculator.getTotalWeight(plates, barWeight)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Plate Calculator",
                    style = MaterialTheme.typography.headlineSmall
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = targetWeight,
                    onValueChange = { targetWeight = it },
                    label = { Text("Target Weight") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    suffix = { Text(if (isKg) "kg" else "lbs") }
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("kg")
                    Switch(
                        checked = isKg,
                        onCheckedChange = { 
                            isKg = it
                            barWeight = if (it) 20.0 else 45.0
                        },
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    Text("lbs")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(15.0, 20.0, 25.0).filter { it <= (if (isKg) 25.0 else 55.0) }.forEach { weight ->
                    FilterChip(
                        selected = barWeight == weight,
                        onClick = { barWeight = weight },
                        label = { Text("${weight.toInt()} ${if (isKg) "kg" else "lbs"}") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Plates per side:",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    if (plates.isEmpty()) {
                        Text(
                            text = "Bar only",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    } else {
                        PlateVisualization(plates = plates, isKg = isKg)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = "Total: ${"%.1f".format(actualWeight)} ${if (isKg) "kg" else "lbs"}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = PlateCalculator.platesToString(plates),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PlateVisualization(plates: List<Double>, isKg: Boolean) {
    val plateColors = mapOf(
        25.0 to Color(0xFFE53935),
        20.0 to Color(0xFF1E88E5),
        15.0 to Color(0xFFFFEB3B),
        10.0 to Color(0xFF4CAF50),
        5.0 to Color(0xFF9C27B0),
        2.5 to Color(0xFFFF9800),
        1.25 to Color(0xFF607D8B),
        45.0 to Color(0xFFE53935),
        35.0 to Color(0xFF1E88E5),
    )

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        plates.forEach { plateWeight ->
            val color = plateColors[plateWeight] ?: Color.Gray
            val height = when {
                plateWeight >= 20 -> 80
                plateWeight >= 15 -> 70
                plateWeight >= 10 -> 60
                plateWeight >= 5 -> 50
                else -> 40
            }
            
            Box(
                modifier = Modifier
                    .padding(horizontal = 2.dp)
                    .width(20.dp)
                    .height(height.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(color)
            )
        }
    }
}
