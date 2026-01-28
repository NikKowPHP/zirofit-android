package com.ziro.fit.ui.program

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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

                        // Display flattened templates with status using the interactive card
                        if (uiState.templatesWithStatus.isNotEmpty()) {
                             item {
                                 Text(
                                     text = "Workouts",
                                     style = MaterialTheme.typography.titleLarge,
                                     modifier = Modifier.padding(vertical = 8.dp)
                                 )
                             }
                             items(uiState.templatesWithStatus) { templateWithStatus ->
                                 TemplateStatusCard(
                                     templateWithStatus = templateWithStatus,
                                     onExpand = { id -> viewModel.loadTemplateDetails(id) }
                                 )
                             }
                        } else if (!program.templates.isNullOrEmpty()) {
                             // Fallback for simple templates without status
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

@Composable
fun TemplateStatusCard(
    templateWithStatus: com.ziro.fit.viewmodel.TemplateWithStatus,
    onExpand: (String) -> Unit
) {
    val template = templateWithStatus.template
    val status = templateWithStatus.status
    var expanded by remember { mutableStateOf(false) }
    
    // Trigger data load when expanded if exercises are missing
    LaunchedEffect(expanded) {
        if (expanded && template.exercises.isNullOrEmpty() && !templateWithStatus.isLoadingDetails) {
            onExpand(template.id)
        }
    }
    
    // Determine card appearance based on status
    val borderColor = when (status) {
        "NEXT" -> MaterialTheme.colorScheme.primary
        else -> Color.Transparent
    }
    
    val backgroundColor = when (status) {
        "COMPLETED" -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        "NEXT" -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f) // Grayed out for PENDING
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .animateContentSize()
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = if (status == "NEXT") androidx.compose.foundation.BorderStroke(2.dp, borderColor) else null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = template.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (status == "PENDING") 
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f) 
                            else MaterialTheme.colorScheme.onSurface
                        )
                        
                        // Show checkmark for completed workouts
                        if (status == "COMPLETED") {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Completed",
                                tint = Color(0xFF4CAF50), // Green checkmark
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        
                        // Show "Next" badge
                        if (status == "NEXT") {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.primary,
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text(
                                    text = "NEXT",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                    
                    if (!template.description.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = template.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (status == "PENDING") 
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    if (templateWithStatus.lastCompletedAt != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Last completed: ${templateWithStatus.lastCompletedAt}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
                
                // Action button based on status
                when (status) {
                    "COMPLETED" -> {
                        OutlinedButton(
                            onClick = { /* TODO: Allow doing workout again */ },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                            )
                        ) {
                            Text("Do Again")
                        }
                    }
                    "NEXT" -> {
                        Button(
                            onClick = { /* TODO: Start this workout */ },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Start")
                        }
                    }
                    "PENDING" -> {
                        // Locked logic if sequential enforcement is enabled
                    }
                }
            }

            // Expanded Exercises List
            if (expanded) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "Exercises",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                if (templateWithStatus.isLoadingDetails) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                } else if (!template.exercises.isNullOrEmpty()) {
                    template.exercises.forEachIndexed { index, exercise ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "${index + 1}.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.width(24.dp)
                            )
                            Text(
                                text = exercise.name,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                } else {
                    Text(
                        text = "No exercises available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }
        }
    }
}

