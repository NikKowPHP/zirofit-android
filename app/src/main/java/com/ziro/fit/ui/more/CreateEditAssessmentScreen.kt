package com.ziro.fit.ui.more

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
import com.ziro.fit.viewmodel.AssessmentsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditAssessmentScreen(
    onNavigateBack: () -> Unit,
    assessmentId: String? = null,
    viewModel: AssessmentsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // If editing, find the assessment from the loaded list
    val assessmentToEdit = remember(assessmentId, uiState.assessments) {
        if (assessmentId != null) {
            uiState.assessments.find { it.id == assessmentId }
        } else null
    }

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("") }
    
    // Initialize fields if editing
    LaunchedEffect(assessmentToEdit) {
        if (assessmentToEdit != null) {
            name = assessmentToEdit.name
            description = assessmentToEdit.description ?: ""
            unit = assessmentToEdit.unit
        }
    }

    // Handle success navigation
    LaunchedEffect(uiState.operationSuccess) {
        if (uiState.operationSuccess) {
             viewModel.resetOperationSuccess()
             onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (assessmentId == null) "New Assessment" else "Edit Assessment") },
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
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                placeholder = { Text("e.g. Body Fat, Plank Hold") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            OutlinedTextField(
                value = unit,
                onValueChange = { unit = it },
                label = { Text("Unit") },
                placeholder = { Text("e.g. %, sec, kg, score") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            if (uiState.error != null) {
                Text(
                    text = uiState.error ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { 
                    if (assessmentId == null) {
                        viewModel.createAssessment(name, description.takeIf { it.isNotBlank() }, unit)
                    } else {
                        viewModel.updateAssessment(assessmentId, name, description.takeIf { it.isNotBlank() }, unit)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank() && unit.isNotBlank() && !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Save")
                }
            }
        }
    }
}
