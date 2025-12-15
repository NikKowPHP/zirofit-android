package com.ziro.fit.ui.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ziro.fit.model.WorkoutExerciseUi
import com.ziro.fit.model.WorkoutSetUi

@Composable
fun LiveWorkoutScreen(
    viewModel: LiveWorkoutViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    // Handle Finish Navigation
    LaunchedEffect(state.isFinished) {
        if (state.isFinished) onNavigateBack()
    }

    Scaffold(
        topBar = {
            LiveWorkoutHeader(
                templateName = state.session?.title ?: "Workout",
                elapsedSeconds = state.elapsedSeconds,
                onFinish = viewModel::finishWorkout,
                isFinishing = state.isFinishing
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(state.session?.exercises ?: emptyList()) { exercise ->
                    ExerciseCard(
                        exercise = exercise,
                        onInputChange = viewModel::updateSetInput,
                        onSetToggle = { set -> 
                            viewModel.onToggleSet(exercise.exerciseId, set) 
                        }
                    )
                }
                
                item {
                    Spacer(Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun LiveWorkoutHeader(
    templateName: String,
    elapsedSeconds: Long,
    onFinish: () -> Unit,
    isFinishing: Boolean
) {
    // Format seconds to MM:SS
    val timeString = String.format("%02d:%02d", elapsedSeconds / 60, elapsedSeconds % 60)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = templateName, style = MaterialTheme.typography.titleMedium)
            Text(
                text = timeString,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
        
        Button(
            onClick = onFinish,
            enabled = !isFinishing,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            if (isFinishing) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
            } else {
                Text("FINISH")
            }
        }
    }
}

@Composable
fun ExerciseCard(
    exercise: WorkoutExerciseUi,
    onInputChange: (String, Int, String, String) -> Unit, // exerciseId, index, weight, reps
    onSetToggle: (WorkoutSetUi) -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = exercise.exerciseName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            if (exercise.targetReps != null) {
                Text(
                    text = "Target: ${exercise.targetReps} reps",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            } else {
                 Spacer(modifier = Modifier.height(8.dp))
            }

            // Header Row
            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)) {
                Text("Set", modifier = Modifier.width(40.dp), style = MaterialTheme.typography.labelMedium)
                Text("Previous", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium)
                Text("kg", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium)
                Text("Reps", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.width(48.dp)) // For Checkbox
            }

            exercise.sets.forEachIndexed { index, set ->
                SetRow(
                    set = set,
                    onWeightChange = { w -> onInputChange(exercise.exerciseId, index, w, set.reps) },
                    onRepsChange = { r -> onInputChange(exercise.exerciseId, index, set.weight, r) },
                    onCheck = { onSetToggle(set) }
                )
            }
        }
    }
}

@Composable
fun SetRow(
    set: WorkoutSetUi,
    onWeightChange: (String) -> Unit,
    onRepsChange: (String) -> Unit,
    onCheck: () -> Unit
) {
    val backgroundColor = if (set.isCompleted) Color(0xFFE8F5E9) else Color.Transparent // Light Green if done

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor, RoundedCornerShape(4.dp))
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Set Number
        Text(
            text = set.setNumber.toString(),
            modifier = Modifier.width(40.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            fontWeight = FontWeight.Bold
        )

        // Previous data (Ghost text) - Placeholder
        Text(
            text = "-",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )

        // Inputs
        OutlinedTextField(
            value = set.weight,
            onValueChange = onWeightChange,
            modifier = Modifier.weight(1f).padding(end = 4.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            )
        )

        OutlinedTextField(
            value = set.reps,
            onValueChange = onRepsChange,
            modifier = Modifier.weight(1f).padding(end = 4.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
             colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            )
        )

        // Check Button
        IconButton(
            onClick = onCheck,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = if (set.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = "Log Set",
                tint = if (set.isCompleted) Color(0xFF4CAF50) else Color.Gray,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
      