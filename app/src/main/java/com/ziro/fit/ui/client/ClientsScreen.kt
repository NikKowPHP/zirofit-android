package com.ziro.fit.ui.client

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ziro.fit.viewmodel.ClientsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientsScreen(
    viewModel: ClientsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Clients") })
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.error != null) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Error: ${uiState.error}", color = MaterialTheme.colorScheme.error)
                    Button(onClick = { viewModel.fetchClients() }) {
                        Text("Retry")
                    }
                }
            } else {
                LazyColumn {
                    items(
                        items = uiState.clients,
                        key = { it.id }
                    ) { client ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = {
                                if (it == SwipeToDismissBoxValue.EndToStart) {
                                    viewModel.deleteClient(client.id)
                                    true
                                } else {
                                    // For now, Edit (StartToEnd) just resets, we can add nav later
                                    false
                                }
                            }
                        )

                        SwipeToDismissBox(
                            state = dismissState,
                            backgroundContent = {
                                val direction = dismissState.dismissDirection
                                val color by animateColorAsState(
                                    when (dismissState.targetValue) {
                                        SwipeToDismissBoxValue.Settled -> Color.Transparent
                                        SwipeToDismissBoxValue.StartToEnd -> Color.Blue // Edit
                                        SwipeToDismissBoxValue.EndToStart -> Color.Red // Delete
                                    }
                                )
                                val icon = when (dismissState.targetValue) {
                                    SwipeToDismissBoxValue.StartToEnd -> Icons.Default.Edit
                                    else -> Icons.Default.Delete
                                }
                                val alignment = when (direction) {
                                    SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                                    else -> Alignment.CenterEnd
                                }
                                val scale by animateFloatAsState(
                                    if (dismissState.targetValue == SwipeToDismissBoxValue.Settled) 0.75f else 1f
                                )

                                Box(
                                    Modifier
                                        .fillMaxSize()
                                        .background(color)
                                        .padding(horizontal = 20.dp),
                                    contentAlignment = alignment
                                ) {
                                    Icon(
                                        icon,
                                        contentDescription = null,
                                        modifier = Modifier.scale(scale),
                                        tint = Color.White
                                    )
                                }
                            },
                            content = {
                                Card(
                                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                                ) {
                                    ListItem(
                                        headlineContent = { Text(client.name) },
                                        supportingContent = { Text(client.email) },
                                        leadingContent = {
                                            Icon(Icons.Default.Person, contentDescription = null)
                                        },
                                        trailingContent = {
                                            Text(
                                                client.status,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = if (client.status == "active") Color.Green else Color.Gray
                                            )
                                        }
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
