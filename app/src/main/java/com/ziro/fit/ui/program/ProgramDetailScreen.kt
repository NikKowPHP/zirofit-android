package com.ziro.fit.ui.program

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ziro.fit.viewmodel.ProgramDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgramDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProgramDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Program Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.error != null) {
                Text(
                    text = uiState.error ?: "Unknown Error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                val program = uiState.program
                if (program != null) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Text(
                                text = program.name,
                                style = MaterialTheme.typography.headlineMedium
                            )
                            if (!program.description.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = program.description,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider()
                        }

                        // Display Weeks if available
                        if (!program.weeks.isNullOrEmpty()) {
                            program.weeks.sortedBy { it.weekNumber }.forEach { week ->
                                item {
                                    Text(
                                        text = "Week ${week.weekNumber}",
                                        style = MaterialTheme.typography.titleLarge,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                                items(week.workouts) { workout ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Text(
                                                text = workout.name,
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                            if (!workout.focus.isNullOrBlank()) {
                                                Text(
                                                    text = "Focus: ${workout.focus}",
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = "${workout.exercises.size} Exercises",
                                                style = MaterialTheme.typography.labelMedium
                                            )
                                        }
                                    }
                                }
                            }
                        } else if (!program.templates.isNullOrEmpty()) {
                             // Display flattened templates if no weeks structure
                             item {
                                 Text(
                                     text = "Workouts",
                                     style = MaterialTheme.typography.titleLarge,
                                     modifier = Modifier.padding(vertical = 8.dp)
                                 )
                             }
                             items(program.templates) { template ->
                                 Card(
                                     modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                     colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                 ) {
                                     Column(modifier = Modifier.padding(16.dp)) {
                                         Text(
                                             text = template.name,
                                             style = MaterialTheme.typography.titleMedium
                                         )
                                         if (!template.description.isNullOrBlank()) {
                                             Text(
                                                 text = template.description,
                                                 style = MaterialTheme.typography.bodyMedium
                                             )
                                         }
                                     }
                                 }
                             }
                        }
                    }
                }
            }
        }
    }
}
