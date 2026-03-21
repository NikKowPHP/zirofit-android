package com.ziro.fit.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ziro.fit.model.FitnessGoal
import com.ziro.fit.model.FitnessGoalType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalSettingSheet(
    existingGoal: FitnessGoal? = null,
    onDismiss: () -> Unit,
    onSave: (FitnessGoal) -> Unit
) {
    var title by remember { mutableStateOf(existingGoal?.title ?: "Weight Goal") }
    var targetValue by remember { mutableStateOf(existingGoal?.targetValue?.toString() ?: "") }
    var currentValue by remember { mutableStateOf(existingGoal?.currentValue?.toString() ?: "") }
    var selectedType by remember { mutableStateOf(existingGoal?.type ?: FitnessGoalType.WEIGHT) }

    val units = when (selectedType) {
        FitnessGoalType.WEIGHT -> listOf("kg", "lbs")
        FitnessGoalType.WORKOUTS -> listOf("sessions", "workouts")
        FitnessGoalType.VOLUME -> listOf("kg", "lbs", "tonnes")
        FitnessGoalType.PR -> listOf("kg", "lbs", "reps")
    }

    var selectedUnit by remember { mutableStateOf(existingGoal?.unit ?: units.firstOrNull() ?: "") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (existingGoal != null) "Edit Goal" else "Create Goal",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Goal Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Text(
                text = "Goal Type",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FitnessGoalType.entries.forEach { type ->
                    FilterChip(
                        selected = selectedType == type,
                        onClick = {
                            selectedType = type
                            selectedUnit = when (type) {
                                FitnessGoalType.WEIGHT -> "kg"
                                FitnessGoalType.WORKOUTS -> "sessions"
                                FitnessGoalType.VOLUME -> "kg"
                                FitnessGoalType.PR -> "kg"
                            }
                        },
                        label = {
                            Text(
                                type.name,
                                style = MaterialTheme.typography.bodySmall
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            OutlinedTextField(
                value = targetValue,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                        targetValue = newValue
                    }
                },
                label = { Text("Target Value") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                isError = targetValue.isNotEmpty() && targetValue.toDoubleOrNull() == null
            )

            OutlinedTextField(
                value = currentValue,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                        currentValue = newValue
                    }
                },
                label = { Text("Current Value") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                isError = currentValue.isNotEmpty() && currentValue.toDoubleOrNull() == null
            )

            Text(
                text = "Unit",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                units.forEach { unit ->
                    FilterChip(
                        selected = selectedUnit == unit,
                        onClick = { selectedUnit = unit },
                        label = { Text(unit, style = MaterialTheme.typography.bodySmall) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val target = targetValue.toDoubleOrNull()
                    val current = currentValue.toDoubleOrNull()
                    if (target != null && current != null && selectedUnit.isNotEmpty()) {
                        val goal = FitnessGoal(
                            id = existingGoal?.id ?: "",
                            title = title.ifBlank { "Weight Goal" },
                            targetValue = target,
                            currentValue = current,
                            unit = selectedUnit,
                            type = selectedType
                        )
                        onSave(goal)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = targetValue.isNotEmpty() && currentValue.isNotEmpty() &&
                        targetValue.toDoubleOrNull() != null &&
                        currentValue.toDoubleOrNull() != null &&
                        selectedUnit.isNotEmpty()
            ) {
                Text(
                    text = if (existingGoal != null) "Update Goal" else "Create Goal",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
