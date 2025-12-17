package com.ziro.fit.ui.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.ziro.fit.model.Exercise
import com.ziro.fit.model.WorkoutExerciseUi
import com.ziro.fit.model.WorkoutSetUi
import com.ziro.fit.viewmodel.WorkoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveWorkoutScreen(
    viewModel: WorkoutViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var showExerciseSheet by remember { mutableStateOf(false) }

    // Handle back navigation if session is gone
    if (state.activeSession == null && !state.isLoading) {
        LaunchedEffect(Unit) {
            onNavigateBack()
        }
    }
    
    // Load exercises initially
    LaunchedEffect(Unit) {
        viewModel.loadExercises()
    }

    Scaffold(
        topBar = {
            LiveWorkoutHeader(
                templateName = state.activeSession?.title ?: "Workout",
                elapsedSeconds = state.elapsedSeconds
            )
        },
        bottomBar = {
             LiveWorkoutBottomBar(
                 onFinish = { viewModel.finishWorkout() },
                 onMinimize = onNavigateBack,
                 isFinishing = state.isFinishing
             )
        }
    ) { padding ->
        // Swipe to dismiss/minimize gesture
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .pointerInput(Unit) {
                    detectVerticalDragGestures { _, dragAmount ->
                        if (dragAmount > 50) { // Threshold for swipe down
                            onNavigateBack()
                        }
                    }
                }
        ) {
            if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 80.dp) // Add padding for Fab/Sheet overlap if needed, or just visual
            ) {
                items(state.activeSession?.exercises ?: emptyList()) { exercise ->
                    val isResting = state.isRestActive && state.restingExerciseId == exercise.exerciseId
                    
                    ExerciseCard(
                        exercise = exercise,
                        isResting = isResting,
                        restSecondsRemaining = state.restSecondsRemaining,
                        restTotalSeconds = state.restTotalSeconds,
                        onInputChange = viewModel::updateSetInput,
                        onSetToggle = { set -> viewModel.logSet(exercise.exerciseId, set) },
                        onAddSet = { viewModel.addSetToExercise(exercise.exerciseId) },
                        onAddRestTime = { viewModel.adjustRestTime(30) },
                        onSkipRest = { viewModel.stopRestTimer() }
                    )
                }
                
                item {
                    Button(
                        onClick = { showExerciseSheet = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Add Exercise")
                    }
                }
            }
        }
        
        // Exercise Browser Sheet
        if (showExerciseSheet) {
            ModalBottomSheet(onDismissRequest = { showExerciseSheet = false }) {
                ExerciseBrowserContent(
                    exercises = state.availableExercises,
                    isLoading = state.isExercisesLoading,
                    onSearch = viewModel::loadExercises,
                    onAddExercises = { exercises ->
                        viewModel.addExercisesToSession(exercises)
                        showExerciseSheet = false
                    }
                )
            }
        }
    }
}
}

@Composable
fun LiveWorkoutHeader(
    templateName: String,
    elapsedSeconds: Long
) {
    // Format seconds to MM:SS
    val timeString = String.format("%02d:%02d", elapsedSeconds / 60, elapsedSeconds % 60)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = templateName, style = MaterialTheme.typography.titleSmall, color = Color.Gray)
            Text(
                text = timeString,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun LiveWorkoutBottomBar(
    onFinish: () -> Unit,
    onMinimize: () -> Unit,
    isFinishing: Boolean
) {
    Surface(
        tonalElevation = 8.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = onMinimize,
                modifier = Modifier.weight(1f)
            ) {
                 Icon(
                     imageVector = Icons.Default.KeyboardArrowDown,
                     contentDescription = null
                 )
                 Spacer(Modifier.width(8.dp))
                 Text("Minimize")
            }
            
            Button(
                onClick = onFinish,
                enabled = !isFinishing,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                if (isFinishing) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                } else {
                    Text("Finish Workout")
                }
            }
        }
    }
}

@Composable
fun ExerciseCard(
    exercise: WorkoutExerciseUi,
    isResting: Boolean,
    restSecondsRemaining: Int,
    restTotalSeconds: Int,
    onInputChange: (String, Int, String, String) -> Unit, // exerciseId, index, weight, reps
    onSetToggle: (WorkoutSetUi) -> Unit,
    onAddSet: () -> Unit,
    onAddRestTime: () -> Unit,
    onSkipRest: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = exercise.exerciseName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
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
            
            // Inline Rest Timer
            if (isResting) {
                Spacer(Modifier.height(16.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Resting: ${restSecondsRemaining}s",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    LinearProgressIndicator(
                        progress = { if (restTotalSeconds > 0) restSecondsRemaining.toFloat() / restTotalSeconds else 0f },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        TextButton(onClick = onAddRestTime) { Text("+30s") }
                        TextButton(onClick = onSkipRest) { Text("Skip") }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
            
            TextButton(onClick = onAddSet, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Add Set")
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
    // Dark mode compatible background color (Green with transparency)
    val backgroundColor = if (set.isCompleted) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.Transparent
    
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

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
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next
            ),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )

        OutlinedTextField(
            value = set.reps,
            onValueChange = onRepsChange,
            modifier = Modifier.weight(1f).padding(end = 4.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Go // Or Done, but user asked for "Next submit logs exercise" which implies a distinct action
            ),
            keyboardActions = KeyboardActions(
                onGo = {
                    onCheck()
                    keyboardController?.hide()
                    focusManager.clearFocus() 
                }
            ),
            singleLine = true,
             colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )

        // Check Button
        IconButton(
            onClick = {
                onCheck()
                keyboardController?.hide()
                focusManager.clearFocus()
            },
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

@Composable
fun ExerciseBrowserContent(
    exercises: List<Exercise>,
    isLoading: Boolean,
    onSearch: (String) -> Unit,
    onAddExercises: (List<Exercise>) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val selectedExercises = remember { mutableStateListOf<Exercise>() }
    
    // Clear selection when search changes or list reloads? 
    // Usually better to keep selection.
    
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { 
                searchQuery = it
                onSearch(it) 
            },
            label = { Text("Search Exercises") },
            leadingIcon = { Icon(Icons.Default.Search, null) },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(Modifier.height(16.dp))
        
        if (isLoading) {
            Box(Modifier
                .fillMaxWidth()
                .weight(1f), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(exercises) { exercise ->
                    val isSelected = selectedExercises.any { it.id == exercise.id }
                    ListItem(
                        headlineContent = { Text(exercise.name) },
                        supportingContent = { Text("${exercise.muscleGroup ?: ""} • ${exercise.equipment ?: ""}") },
                        leadingContent = {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = { checked ->
                                    if (checked) {
                                        selectedExercises.add(exercise)
                                    } else {
                                        selectedExercises.removeAll { it.id == exercise.id }
                                    }
                                }
                            )
                        },
                        modifier = Modifier
                            .clickable {
                                if (isSelected) {
                                    selectedExercises.removeAll { it.id == exercise.id }
                                } else {
                                    selectedExercises.add(exercise)
                                }
                            }
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                    )
                }
                if (exercises.isEmpty()) {
                    item {
                        Text("No exercises found", modifier = Modifier.padding(16.dp), color = Color.Gray)
                    }
                }
            }
            
            // Add Button Footer
            if (selectedExercises.isNotEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 8.dp
                ) {
                    Button(
                        onClick = { onAddExercises(selectedExercises.toList()) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Text("Add (${selectedExercises.size}) Exercises")
                    }
                }
            }
        }
    }
}
      