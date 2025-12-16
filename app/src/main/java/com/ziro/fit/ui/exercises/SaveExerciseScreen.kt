package com.ziro.fit.ui.exercises

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ziro.fit.model.Exercise
import com.ziro.fit.viewmodel.ExerciseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveExerciseScreen(
    onNavigateBack: () -> Unit,
    exerciseId: String? = null,
    viewModel: ExerciseViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // If editing, find the exercise from the loaded list. 
    // Ideally this should be a robust fetch by ID from DB, but for now we rely on the list.
    val exerciseToEdit = remember(exerciseId, uiState.exercises) {
        if (exerciseId != null) {
            uiState.exercises.find { it.id == exerciseId }
        } else null
    }

    // Local state for form fields
    // Initialize with existing data if editing, or empty if creating
    // We use derivedStateOf or LaunchedEffect to set initial values once exerciseToEdit is loaded?
    // Actually, simple mutableStateOf initialized with exerciseToEdit properties works 
    // BUT we need to handle the case where exerciseToEdit might load asynchronously if the list wasn't loaded.
    // For this implementation, we assume the list is loaded or we might default to empty strings.
    
    var name by remember { mutableStateOf("") }
    var muscleGroup by remember { mutableStateOf("") }
    var equipment by remember { mutableStateOf("") }
    var videoUrl by remember { mutableStateOf("") }
    var isInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(exerciseToEdit) {
        if (!isInitialized && exerciseToEdit != null) {
            name = exerciseToEdit.name
            muscleGroup = exerciseToEdit.muscleGroup ?: ""
            equipment = exerciseToEdit.equipment ?: ""
            videoUrl = exerciseToEdit.videoUrl ?: ""
            isInitialized = true
        }
    }

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (exerciseId != null) "Edit Exercise" else "Create Exercise") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name *") },
                modifier = Modifier.fillMaxWidth(),
                isError = name.isBlank() && uiState.error != null // Simple error indication
            )
            
            OutlinedTextField(
                value = muscleGroup,
                onValueChange = { muscleGroup = it },
                label = { Text("Muscle Group") },
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = equipment,
                onValueChange = { equipment = it },
                label = { Text("Equipment") },
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = videoUrl,
                onValueChange = { videoUrl = it },
                label = { Text("Video URL") },
                modifier = Modifier.fillMaxWidth()
            )

            if (uiState.error != null) {
                Text(
                    text = uiState.error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Button(
                onClick = {
                    if (name.isNotBlank()) {
                         if (exerciseId != null) {
                             viewModel.updateExercise(exerciseId, name, muscleGroup.ifBlank { null }, equipment.ifBlank { null }, videoUrl.ifBlank { null })
                         } else {
                             viewModel.createExercise(name, muscleGroup.ifBlank { null }, equipment.ifBlank { null }, videoUrl.ifBlank { null })
                         }
                    }
                },
                enabled = name.isNotBlank() && !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("Save")
                }
            }
        }
    }
}
