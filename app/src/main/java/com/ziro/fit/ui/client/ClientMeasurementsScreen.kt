package com.ziro.fit.ui.client

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ziro.fit.model.Measurement
import com.ziro.fit.viewmodel.ClientMeasurementsViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientMeasurementsScreen(
    clientId: String,
    onNavigateBack: () -> Unit,
    viewModel: ClientMeasurementsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    LaunchedEffect(clientId) {
        viewModel.loadMeasurements(clientId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Measurements") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Measurement")
            }
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
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = uiState.error ?: "Unknown error",
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadMeasurements(clientId) }) {
                            Text("Retry")
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(uiState.measurements) { measurement ->
                            MeasurementItemFull(
                                measurement = measurement,
                                onDelete = { viewModel.deleteMeasurement(clientId, measurement.id) }
                            )
                            HorizontalDivider()
                        }
                        if (uiState.measurements.isEmpty()) {
                            item {
                                Text(
                                    text = "No measurements found.",
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
            
            if (showCreateDialog) {
                 MeasurementFormDialog(
                     onDismiss = { showCreateDialog = false },
                     onConfirm = { date, weight, bodyFat, notes ->
                         viewModel.createMeasurement(clientId, date, weight, bodyFat, notes)
                         showCreateDialog = false
                     }
                 )
            }
        }
    }
}

@Composable
fun MeasurementItemFull(
    measurement: Measurement,
    onDelete: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }
    
    val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    val date = try {
        Instant.parse(measurement.measurementDate)
            .atZone(ZoneId.systemDefault())
            .format(dateFormatter)
    } catch (e: Exception) {
        measurement.measurementDate
    }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = date,
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray
            )
            IconButton(onClick = { showDeleteConfirm = true }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray)
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (measurement.weightKg != null) {
                MetricChip(label = "Weight", value = "${measurement.weightKg} kg")
            }
            if (measurement.bodyFatPercentage != null) {
                MetricChip(label = "Body Fat", value = "${measurement.bodyFatPercentage}%")
            }
        }
        if (!measurement.notes.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                 text = measurement.notes,
                 style = MaterialTheme.typography.bodySmall,
                 color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
    
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Measurement") },
            text = { Text("Are you sure you want to delete this measurement?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteConfirm = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeasurementFormDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Double?, Double?, String?) -> Unit
) {
    var weight by remember { mutableStateOf("") }
    var bodyFat by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    // Ideally use a DatePicker, simplifying to Today for now or text input
    // For MVP, letting backend handle date or defaulting to instant in Repository if needed, 
    // but API expects date string. Let's use current ISO string or simple input.
    // For better experience, let's just default to "now" in the Logic layer if not provided, 
    // but the Dialog signature requires a date.
    // Let's autopopulate with today's date in ISO format.
    val today = java.time.LocalDate.now().toString() // YYYY-MM-DD
    var date by remember { mutableStateOf(today) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Measurement") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Date (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Weight (kg)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = bodyFat,
                    onValueChange = { bodyFat = it },
                    label = { Text("Body Fat %") },
                    modifier = Modifier.fillMaxWidth()
                )
                 OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val w = weight.toDoubleOrNull()
                    val bf = bodyFat.toDoubleOrNull()
                    // Append T00:00:00Z to date if simple date provided to satisfy ISO instant if needed,
                    // or just pass as is. API usually expects ISO 8601.
                    // A simple approximation:
                    val isoDate = try {
                        java.time.LocalDate.parse(date).atStartOfDay(ZoneId.systemDefault()).toInstant().toString()
                    } catch (e: Exception) {
                        // Fallback to now if parse fails or just send string
                        if(date.contains("T")) date else Instant.now().toString()
                    }
                    onConfirm(isoDate, w, bf, notes)
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
