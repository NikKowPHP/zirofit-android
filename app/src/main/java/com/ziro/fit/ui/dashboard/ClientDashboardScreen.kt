package com.ziro.fit.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientDashboardScreen(
    onLogout: () -> Unit,
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
                                if (data.trainer != null) {
                                    Text(text = "Name: ${data.trainer.name ?: "N/A"}", style = MaterialTheme.typography.bodyLarge)
                                    Text(text = "Email: ${data.trainer.email}", style = MaterialTheme.typography.bodyMedium)
                                    Text(text = "Username: ${data.trainer.username}", style = MaterialTheme.typography.bodyMedium)
                                } else {
                                    Text(text = "No trainer linked.", style = MaterialTheme.typography.bodyLarge)
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
