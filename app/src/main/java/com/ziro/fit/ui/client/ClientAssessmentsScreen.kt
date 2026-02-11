package com.ziro.fit.ui.client

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ziro.fit.model.Assessment
import com.ziro.fit.model.AssessmentResult
import com.ziro.fit.util.DateTimeUtils
import com.ziro.fit.viewmodel.ClientAssessmentsViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientAssessmentsScreen(
    clientId: String,
    onNavigateBack: () -> Unit,
    viewModel: ClientAssessmentsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    LaunchedEffect(clientId) {
        viewModel.loadAssessments(clientId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Assessments") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Assessment")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when {
                uiState.isLoading && uiState.assessments.isEmpty() -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.error != null && uiState.assessments.isEmpty() -> {
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
                        Button(onClick = { viewModel.loadAssessments(clientId) }) {
                            Text("Retry")
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(uiState.assessments) { assessment ->
                            AssessmentItemFull(
                                assessment = assessment,
                                onDelete = { viewModel.deleteAssessment(clientId, assessment.id) }
                            )
                            HorizontalDivider()
                        }
                        if (uiState.assessments.isEmpty()) {
                            item {
                                Text(
                                    text = "No assessments found.",
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
                AssessmentSelectionDialog(
                    availableAssessments = uiState.availableAssessmentTypes,
                    onDismiss = { showCreateDialog = false },
                    onConfirm = { assessmentId, date, value, notes ->
                        viewModel.createAssessment(clientId, assessmentId, date, value, notes)
                        showCreateDialog = false
                    },
                    onCreateNewType = { name, unit, onTypeCreated ->
                        viewModel.createAssessmentType(name, unit) { newAssessment ->
                            onTypeCreated(newAssessment)
                        }
                    },
                    isCreatingType = uiState.isCreatingType
                )
            }
        }
    }
}

@Composable
fun AssessmentItemFull(
    assessment: AssessmentResult,
    onDelete: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = assessment.assessmentName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = DateTimeUtils.formatDate(assessment.date),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
             if (!assessment.notes.isNullOrBlank()) {
                Text(
                    text = assessment.notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${assessment.value} ${assessment.unit ?: ""}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
             IconButton(onClick = { showDeleteConfirm = true }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray)
            }
        }
    }
    
     if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Assessment") },
            text = { Text("Are you sure you want to delete this result?") },
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
fun AssessmentSelectionDialog(
    availableAssessments: List<Assessment>,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Double, String?) -> Unit,
    onCreateNewType: (String, String, (Assessment) -> Unit) -> Unit, // Updated signature
    isCreatingType: Boolean
) {
    var isAddingNewType by remember { mutableStateOf(false) }
    
    // Selection State
    var selectedAssessment by remember { mutableStateOf<Assessment?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var value by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    val today = java.time.LocalDate.now().toString()
    var date by remember { mutableStateOf(today) }

    // Creation State
    var newTypeName by remember { mutableStateOf("") }
    var newTypeUnit by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isAddingNewType) "Create Assessment Type" else "Add Result") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (isAddingNewType) {
                    // Create New Type Form
                    Text("Define a new assessment type to add to your library.", style = MaterialTheme.typography.bodyMedium)
                    OutlinedTextField(
                        value = newTypeName,
                        onValueChange = { newTypeName = it },
                        label = { Text("Name (e.g. Plank)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newTypeUnit,
                        onValueChange = { newTypeUnit = it },
                        label = { Text("Unit (e.g. Seconds)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    // Select & Record Form
                    
                    // Dropdown
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedAssessment?.name ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Assessment") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            if (availableAssessments.isEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("No assessments found") },
                                    onClick = { expanded = false },
                                    enabled = false
                                )
                            }
                            
                            availableAssessments.forEach { assessment ->
                                DropdownMenuItem(
                                    text = { Text(assessment.name) },
                                    onClick = {
                                        selectedAssessment = assessment
                                        expanded = false
                                    }
                                )
                            }
                            
                            HorizontalDivider()
                            
                            DropdownMenuItem(
                                text = { Text("Create New Assessment...", color = MaterialTheme.colorScheme.primary) },
                                onClick = {
                                    isAddingNewType = true
                                    expanded = false
                                }
                            )
                        }
                    }
                    
                    OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        label = { Text("Date (YYYY-MM-DD)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    OutlinedTextField(
                        value = value,
                        onValueChange = { value = it },
                        label = { Text("Value ${selectedAssessment?.unit?.let { "($it)" } ?: ""}") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            if (isAddingNewType) {
                Button(
                    onClick = {
                        onCreateNewType(newTypeName, newTypeUnit) { newAssessment ->
                            selectedAssessment = newAssessment
                            isAddingNewType = false // Switch back to selection
                        }
                    },
                    enabled = newTypeName.isNotBlank() && newTypeUnit.isNotBlank() && !isCreatingType
                ) {
                    if (isCreatingType) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp))
                    } else {
                        Text("Create")
                    }
                }
            } else {
                Button(
                    onClick = {
                        val v = value.toDoubleOrNull()
                        if (selectedAssessment != null && v != null) {
                             val isoDate = try {
                                java.time.LocalDate.parse(date).atStartOfDay(ZoneId.systemDefault()).toInstant().toString()
                            } catch (e: Exception) {
                                if(date.contains("T")) date else Instant.now().toString()
                            }
                            onConfirm(selectedAssessment!!.id, isoDate, v, notes)
                        }
                    },
                    enabled = selectedAssessment != null && value.toDoubleOrNull() != null
                ) {
                    Text("Save")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = {
                if (isAddingNewType) isAddingNewType = false else onDismiss()
            }) {
                Text(if (isAddingNewType) "Back" else "Cancel")
            }
        }
    )
}
