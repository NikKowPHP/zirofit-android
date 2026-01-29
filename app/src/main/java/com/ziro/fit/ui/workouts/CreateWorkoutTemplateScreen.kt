package com.ziro.fit.ui.workouts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ziro.fit.ui.components.ExerciseBrowserContent
import com.ziro.fit.viewmodel.AttachedExerciseUi
import com.ziro.fit.viewmodel.CreateTemplateViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWorkoutTemplateScreen(
    onNavigateBack: () -> Unit,
    viewModel: CreateTemplateViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showExerciseSheet by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Template") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = viewModel::saveTemplate) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        } else {
                            Text("Save", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 80.dp) // Space for FAB if used, or just bottom padding
            ) {
                item {
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = uiState.name,
                        onValueChange = viewModel::onNameChange,
                        label = { Text("Template Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = uiState.description,
                        onValueChange = viewModel::onDescriptionChange,
                        label = { Text("Description (Optional)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        maxLines = 5
                    )
                    Spacer(Modifier.height(24.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Exercises",
                            style = MaterialTheme.typography.titleLarge
                        )
                        IconButton(onClick = { showExerciseSheet = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Add Exercise", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }

                if (uiState.attachedExercises.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .clickable { showExerciseSheet = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Tap to add exercises", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                } else {
                    itemsIndexed(uiState.attachedExercises) { index, exercise ->
                        AttachedExerciseCard(
                            exercise = exercise,
                            onRemove = { viewModel.removeExercise(index) },
                            onUpdate = { sets, reps, rest, notes -> 
                                viewModel.updateExerciseDetails(index, sets, reps, rest, notes)
                            }
                        )
                        Spacer(Modifier.height(16.dp))
                    }
                }
            }
            
            if (uiState.error != null) {
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    action = {
                        TextButton(onClick = viewModel::clearError) {
                            Text("Dismiss", color = MaterialTheme.colorScheme.onSurface) // Or inverse
                        }
                    }
                ) {
                    Text(uiState.error!!)
                }
            }
        }

        if (showExerciseSheet) {
            ModalBottomSheet(
                onDismissRequest = { showExerciseSheet = false },
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                ExerciseBrowserContent(
                    exercises = uiState.availableExercises,
                    isLoading = uiState.isExercisesLoading,
                    onSearch = viewModel::loadExercises,
                    onAddExercises = { exercises ->
                        viewModel.addExercises(exercises)
                        showExerciseSheet = false
                    }
                )
            }
        }
    }
}

@Composable
fun AttachedExerciseCard(
    exercise: AttachedExerciseUi,
    onRemove: () -> Unit,
    onUpdate: (String, String, String, String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Remove")
                }
            }
            
            Spacer(Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Sets
                OutlinedTextField(
                    value = exercise.sets,
                    onValueChange = { onUpdate(it, exercise.reps, exercise.restSeconds, exercise.notes) },
                    label = { Text("Sets") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                    singleLine = true
                )
                // Reps
                OutlinedTextField(
                    value = exercise.reps,
                    onValueChange = { onUpdate(exercise.sets, it, exercise.restSeconds, exercise.notes) },
                    label = { Text("Reps") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next), // Text allowed for "8-12"
                    singleLine = true
                )
                // Rest
                OutlinedTextField(
                    value = exercise.restSeconds,
                    onValueChange = { onUpdate(exercise.sets, exercise.reps, it, exercise.notes) },
                    label = { Text("Rest (s)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                    singleLine = true
                )
            }
            
            Spacer(Modifier.height(8.dp))
            
            OutlinedTextField(
                value = exercise.notes,
                onValueChange = { onUpdate(exercise.sets, exercise.reps, exercise.restSeconds, it) },
                label = { Text("Notes (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
    }
}
