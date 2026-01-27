package com.ziro.fit.ui.program

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ziro.fit.model.ProgramDto
import com.ziro.fit.viewmodel.ClientProgramsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientProgramsScreen(
    clientId: String,
    onNavigateBack: () -> Unit,
    onNavigateToProgramDetail: (String) -> Unit,
    viewModel: ClientProgramsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showGenerateDialog by remember { mutableStateOf(false) }

    LaunchedEffect(clientId) {
        viewModel.loadPrograms(clientId)
    }

    LaunchedEffect(uiState.generatedProgramId) {
        uiState.generatedProgramId?.let { programId ->
            showGenerateDialog = false
            onNavigateToProgramDetail(programId)
            viewModel.clearGeneratedProgramId()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Client Programs") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showGenerateDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Generate AI Program") }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.userPrograms.isEmpty() && uiState.trainerPrograms.isEmpty() && uiState.systemPrograms.isEmpty()) {
                Text(
                    text = "No programs found. Generate one!",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Gray
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (uiState.trainerPrograms.isNotEmpty()) {
                        item {
                            Text(
                                "Assigned Programs",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        items(uiState.trainerPrograms) { program ->
                            ProgramItem(program, onClick = { onNavigateToProgramDetail(program.id) })
                        }
                    }

                    if (uiState.userPrograms.isNotEmpty()) {
                        item {
                            Text(
                                "Client Created",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        items(uiState.userPrograms) { program ->
                            ProgramItem(program, onClick = { onNavigateToProgramDetail(program.id) })
                        }
                    }
                }
            }
        }
        
        if (showGenerateDialog) {
            GenerateProgramDialog(
                onDismiss = { if (!uiState.isGenerating) showGenerateDialog = false },
                onConfirm = { duration, focus ->
                    viewModel.generateAiProgram(clientId, duration, focus)
                },
                isLoading = uiState.isGenerating
            )
        }
        
        if (uiState.error != null) {
            // Simple error handling for now - toast or snackbar would be better but keeping it simple
             AlertDialog(
                onDismissRequest = { /* Dismiss handled by viewmodel? or just ignore */ },
                title = { Text("Error") },
                text = { Text(uiState.error ?: "Unknown error") },
                confirmButton = {
                    TextButton(onClick = { viewModel.loadPrograms(clientId) }) { // Refresh works as dismiss of sorts?
                        Text("OK")
                    }
                }
            )
        }
    }
}

@Composable
fun ProgramItem(
    program: ProgramDto,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = program.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (program.description != null) {
                    Text(
                        text = program.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        maxLines = 2
                    )
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}
