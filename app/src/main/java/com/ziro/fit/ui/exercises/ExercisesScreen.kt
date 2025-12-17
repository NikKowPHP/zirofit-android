package com.ziro.fit.ui.exercises

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ziro.fit.viewmodel.ExercisesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExercisesScreen(
    onNavigateBack: (() -> Unit)? = null, // Optional if we use it in a tab
    onStartFreestyleWorkout: () -> Unit,
    viewModel: ExercisesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Exercises") }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onStartFreestyleWorkout,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Start Empty Workout") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { 
                    searchQuery = it
                    viewModel.loadExercises(it.takeIf { s -> s.isNotBlank() })
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search exercises...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                singleLine = true
            )

            // We only show full screen loading if we have NO data yet
            if (uiState.isLoading && uiState.exercises.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.error != null && uiState.exercises.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Error: ${uiState.error}", color = MaterialTheme.colorScheme.error)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp) // Space for FAB
                ) {
                    itemsIndexed(uiState.exercises) { index, exercise ->
                        // Trigger next page load when we reach the end
                        if (index == uiState.exercises.lastIndex && !uiState.isLoading) {
                            LaunchedEffect(Unit) {
                                viewModel.loadNextPage()
                            }
                        }

                        ListItem(
                            headlineContent = { Text(exercise.name) },
                            supportingContent = { 
                                val details = listOfNotNull(exercise.muscleGroup, exercise.equipment).joinToString(" • ")
                                if (details.isNotEmpty()) Text(details)
                            },
                        )
                        HorizontalDivider()
                    }
                    
                    if (uiState.isLoading && uiState.exercises.isNotEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            }
                        }
                    }

                    if (uiState.exercises.isEmpty() && !uiState.isLoading) {
                         item {
                            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text("No exercises found", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}
