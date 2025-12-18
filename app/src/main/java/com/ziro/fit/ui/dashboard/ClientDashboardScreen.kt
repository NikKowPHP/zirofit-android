package com.ziro.fit.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientDashboardScreen(
    onLogout: () -> Unit,
    onNavigateToDiscovery: () -> Unit,
    viewModel: com.ziro.fit.viewmodel.ClientDashboardViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val uiState = viewModel.uiState

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Client Dashboard") },
                actions = {
                    TextButton(onClick = onLogout) {
                        Text("Logout")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (uiState) {
                is com.ziro.fit.viewmodel.ClientDashboardUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is com.ziro.fit.viewmodel.ClientDashboardUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Error: ${uiState.message}", color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.fetchDashboard() }) {
                            Text("Retry")
                        }
                    }
                }
                is com.ziro.fit.viewmodel.ClientDashboardUiState.Success -> {
                    val data = uiState.data
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Welcome, ${data.name}!",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Your Trainer",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                if (uiState.linkedTrainer != null || data.trainer != null) {
                                    val trainer = uiState.linkedTrainer
                                    val dashboardTrainer = data.trainer
                                    
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (trainer?.profile?.profilePhotoPath != null) {
                                            coil.compose.AsyncImage(
                                                model = trainer.profile.profilePhotoPath,
                                                contentDescription = "Trainer Photo",
                                                modifier = Modifier
                                                    .size(64.dp)
                                                    .clip(androidx.compose.foundation.shape.CircleShape),
                                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                            )
                                            Spacer(modifier = Modifier.width(16.dp))
                                        }
                                        Column {
                                            Text(
                                                text = "Name: ${trainer?.name ?: dashboardTrainer?.name ?: "N/A"}",
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                            Text(
                                                text = "Email: ${trainer?.email ?: dashboardTrainer?.email ?: "N/A"}",
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                    }
                                    
                                    if (trainer?.profile?.aboutMe != null) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = trainer.profile.aboutMe,
                                            style = MaterialTheme.typography.bodySmall,
                                            maxLines = 3,
                                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))
                                    OutlinedButton(
                                        onClick = { viewModel.unlinkTrainer() },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                                    ) {
                                        Text("Unlink Trainer")
                                    }
                                } else {
                                    Text(text = "No trainer linked.", style = MaterialTheme.typography.bodyLarge)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(onClick = onNavigateToDiscovery) {
                                        Text("Find a Trainer")
                                    }
                                }
                            }
                        }
                        // Add more dashboard sections here (e.g., sessions, measurements)
                    }
                }
            }
        }
    }
}
