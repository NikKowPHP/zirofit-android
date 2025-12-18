package com.ziro.fit.ui.checkins

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ziro.fit.data.model.CheckInHistoryItem
import com.ziro.fit.util.DateTimeUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientCheckInListScreen(
    viewModel: ClientCheckInViewModel = hiltViewModel(),
    onNavigateToDetail: (String) -> Unit,
    onNavigateToSubmit: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadConfig()
        viewModel.loadHistory()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Check-Ins") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            // Show FAB if check-in is due? or always allow?
            // Usually restricted to weekly. relying on config.
            if (uiState.config?.isCheckInDue == true || uiState.config?.overdue == true) {
                ExtendedFloatingActionButton(
                    onClick = onNavigateToSubmit,
                    icon = { Icon(Icons.Default.Add, "Check-In") },
                    text = { Text(if (uiState.config?.overdue == true) "Check-In Overdue" else "Check-In Now") },
                    containerColor = if (uiState.config?.overdue == true) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer
                )
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
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Error: ${uiState.error}", color = MaterialTheme.colorScheme.error)
                        Button(onClick = { 
                            viewModel.loadConfig()
                            viewModel.loadHistory() 
                        }) {
                            Text("Retry")
                        }
                    }
                }
                uiState.history.isEmpty() -> {
                     Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No check-ins yet.",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        if (uiState.config?.isCheckInDue != true) {
                            Spacer(modifier = Modifier.height(8.dp))
                             Text(
                                text = "Next check-in: ${DateTimeUtils.formatDate(uiState.config?.nextCheckInDate)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 80.dp) // Space for FAB
                    ) {
                        // Display next check-in info if not due yet?
                        if (uiState.config?.isCheckInDue == false && uiState.config?.nextCheckInDate != null) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text("Next Check-In", style = MaterialTheme.typography.titleSmall)
                                        Text(
                                            text = DateTimeUtils.formatDate(uiState.config!!.nextCheckInDate!!),
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }
                                }
                            }
                        }

                        items(uiState.history) { item ->
                            ClientCheckInHistoryItem(
                                item = item,
                                onClick = { onNavigateToDetail(item.id) }
                            )
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ClientCheckInHistoryItem(
    item: CheckInHistoryItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Check-In: ${DateTimeUtils.formatDate(item.date)}",
                style = MaterialTheme.typography.titleMedium
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
            // Status indicator
            if (item.hasFeedback) {
                 Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Reviewed",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Reviewed",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                     Text(
                        text = "Pending Review",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
            }
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
