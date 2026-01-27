package com.ziro.fit.ui.workouts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ziro.fit.model.WorkoutTemplate
import com.ziro.fit.viewmodel.WorkoutsViewModel

@Composable
fun WorkoutsScreen(
    onNavigateBack: (() -> Unit)? = null,
    onStartFreestyleWorkout: () -> Unit,
    onNavigateToProgramDetail: (String) -> Unit,
    viewModel: WorkoutsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            Column {
                // Custom Top Bar to match design
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Workout",
                        style = MaterialTheme.typography.displaySmall
                    )
                    Row {
                         IconButton(onClick = { /* TODO: Search */ }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
        ) {
            // Quick Start Section
            Text(
                text = "Quick start",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Button(
                onClick = onStartFreestyleWorkout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = "START AN EMPTY WORKOUT",
                    style = MaterialTheme.typography.labelLarge
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Spacer(modifier = Modifier.height(24.dp))

            // Programs Section
            if (uiState.programs.isNotEmpty()) {
                Text(
                    text = "Programs",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(bottom = 24.dp)
                ) {
                    uiState.programs.forEach { program ->
                         ProgramCard(
                            program = program, 
                            onClick = { 
                                onNavigateToProgramDetail(program.id)
                            }
                        )
                    }
                }
            }
            
            // Templates Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Templates",
                    style = MaterialTheme.typography.titleLarge
                )
                Row {
                    IconButton(onClick = { /* TODO: Create Template */ }) {
                         Icon(Icons.Default.Add, contentDescription = "Add Template")
                    }
                    IconButton(onClick = { /* TODO: Folders */ }) {
                         Icon(Icons.Default.Folder, contentDescription = "Folders")
                    }
                    IconButton(onClick = { /* TODO: More */ }) {
                         Icon(Icons.Default.MoreHoriz, contentDescription = "More")
                    }
                }
            }

            // My Templates Section
            if (uiState.userTemplates.isNotEmpty() || uiState.trainerTemplates.isNotEmpty()) {
                val combinedTemplates = uiState.userTemplates + uiState.trainerTemplates
                Text(
                    text = "My Templates (${combinedTemplates.size})",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                if (combinedTemplates.isEmpty()) {
                     // Empty state for My Templates if logic permits (though we checked isNotEmpty above)
                     Box(
                         modifier = Modifier
                             .fillMaxWidth()
                             .height(120.dp)
                             .clickable { /* TODO: Create Template */ },
                             contentAlignment = Alignment.Center
                     ) {
                         // Dashed border or similar would be better here
                         Text("Tap to Add", color = MaterialTheme.colorScheme.primary)
                     }
                } else {
                    Row(
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState())
                            .padding(vertical = 8.dp)
                    ) {
                        combinedTemplates.forEach { template -> 
                            TemplateCard(
                                template = template, 
                                onClick = { /* TODO: Start from template */ },
                                onMenuClick = { /* TODO: Template options */ }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            } else {
                 // Empty state placeholder matching screenshot
                 Text(
                    text = "My Templates (0)",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                 )
                 Card(
                    modifier = Modifier
                        .size(width = 160.dp, height = 150.dp)
                        .padding(vertical = 8.dp)
                        .clickable { /* TODO: Trigger add */ },
                     colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.3f))
                 ) {
                     Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                         Column(horizontalAlignment = Alignment.CenterHorizontally) {
                             Text("Tap to Add", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
                             Text("or drag", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelMedium)
                             Text("template here", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelMedium)
                             Text("to move", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelMedium)
                         }
                     }
                 }
                Spacer(modifier = Modifier.height(16.dp))
            }


            // Example Templates Section
            Text(
                text = "Example Templates (${uiState.systemTemplates.size})",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
             Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 8.dp)
            ) {
                uiState.systemTemplates.forEach { template -> 
                     TemplateCard(
                        template = template, 
                        onClick = { /* TODO: Start from template */ },
                        onMenuClick = { /* TODO: Template options */ }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(80.dp)) // Bottom padding
        }
    }
}
