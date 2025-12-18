package com.ziro.fit.ui.checkins

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ziro.fit.data.model.CheckInDetailWrapper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientCheckInDetailScreen(
    checkInId: String,
    viewModel: ClientCheckInViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(checkInId) {
        viewModel.loadCheckInDetails(checkInId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Check-In Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.error != null -> {
                    Text(
                        text = "Error: ${uiState.error}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.selectedCheckIn != null -> {
                    val current = uiState.selectedCheckIn!!
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        Text("Date: ${current.date}", style = MaterialTheme.typography.titleLarge)
                        Text("Status: ${current.status}", style = MaterialTheme.typography.labelMedium)
                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Metrics", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(8.dp))
                                DetailItem("Weight", "${current.weight ?: "N/A"} kg")
                                DetailItem("Waist", "${current.waistCm ?: "N/A"} cm")
                                DetailItem("Sleep", "${current.sleepHours ?: "N/A"} hrs")
                            }
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                             Column(modifier = Modifier.padding(16.dp)) {
                                Text("Wellness", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(8.dp))
                                DetailItem("Energy", "${current.energyLevel ?: "N/A"}/10")
                                DetailItem("Stress", "${current.stressLevel ?: "N/A"}/10")
                                DetailItem("Hunger", "${current.hungerLevel ?: "N/A"}/10")
                                DetailItem("Digestion", "${current.digestionLevel ?: "N/A"}/10")
                                DetailItem("Nutrition", current.nutritionCompliance ?: "N/A")
                            }
                        }
                        
                        if (!current.clientNotes.isNullOrBlank()) {
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("My Notes", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(current.clientNotes, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }

                        if (!current.photos.isNullOrEmpty()) {
                            Text("Photos:", style = MaterialTheme.typography.titleMedium)
                            current.photos.forEach { photo ->
                                Text("• ${photo.imagePath} (${photo.caption ?: "No caption"})")
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        
                        if (!current.trainerResponse.isNullOrBlank()) {
                             Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Trainer Feedback", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSecondaryContainer)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(current.trainerResponse, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSecondaryContainer)
                                }
                            }
                        } else {
                            Text(
                                "No feedback from trainer yet.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
