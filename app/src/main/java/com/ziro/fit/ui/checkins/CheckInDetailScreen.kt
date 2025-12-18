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
import com.ziro.fit.util.DateTimeUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckInDetailScreen(
    checkInId: String,
    viewModel: CheckInViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.detailUiState.collectAsState()
    var reviewText by remember { mutableStateOf("") }
    
    LaunchedEffect(checkInId) {
        viewModel.loadCheckInDetails(checkInId)
    }

    LaunchedEffect(uiState.reviewSuccess) {
        if (uiState.reviewSuccess) {
             onNavigateBack()
             viewModel.resetReviewState()
        }
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
                uiState.checkInContext != null -> {
                    val current = uiState.checkInContext!!.current
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        Text("Client: ${current.client.name}", style = MaterialTheme.typography.titleLarge)
                        Text("Date: ${DateTimeUtils.formatDate(current.date)}", style = MaterialTheme.typography.labelMedium)
                        Spacer(modifier = Modifier.height(16.dp))

                        DetailItem("Weight", "${current.weight ?: "N/A"}")
                        DetailItem("Waist", "${current.waistCm ?: "N/A"} cm")
                        DetailItem("Sleep", "${current.sleepHours ?: "N/A"} hrs")
                        DetailItem("Energy", "${current.energyLevel ?: "N/A"}/10")
                        DetailItem("Stress", "${current.stressLevel ?: "N/A"}/10")
                        DetailItem("Hunger", "${current.hungerLevel ?: "N/A"}/10")
                        DetailItem("Digestion", "${current.digestionLevel ?: "N/A"}/10")
                        DetailItem("Nutrition", current.nutritionCompliance ?: "N/A")
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Notes:", style = MaterialTheme.typography.titleMedium)
                        Text(current.clientNotes ?: "No notes", style = MaterialTheme.typography.bodyMedium)

                        if (!current.photos.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Photos:", style = MaterialTheme.typography.titleMedium)
                            current.photos.forEach { photo ->
                                Text("• ${photo.imagePath} (${photo.caption ?: "No caption"})")
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Trainer Review", style = MaterialTheme.typography.titleMedium)
                        OutlinedTextField(
                            value = reviewText,
                            onValueChange = { reviewText = it },
                            label = { Text("Response") },
                            modifier = Modifier.fillMaxWidth().height(150.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        if (uiState.reviewError != null) {
                            Text(uiState.reviewError!!, color = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        Button(
                            onClick = { viewModel.submitReview(current.id, reviewText) },
                            enabled = !uiState.isReviewSubmitting && reviewText.isNotBlank(),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (uiState.isReviewSubmitting) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp))
                            } else {
                                Text("Submit Review")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(label, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
    }
}
