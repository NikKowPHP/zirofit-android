package com.ziro.fit.ui.profile.subscreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ziro.fit.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackagesScreen(
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchPackages()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Packages") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
         Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
             if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(androidx.compose.ui.Alignment.Center))
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(uiState.packages) { pkg ->
                        ListItem(
                            headlineContent = { Text(pkg.name) },
                            supportingContent = { Text(pkg.description ?: "") },
                            trailingContent = { Text(pkg.price?.toString() ?: "") }
                        )
                        Divider()
                    }
                     if (uiState.packages.isEmpty()) {
                         item { Text("No packages found", modifier = Modifier.padding(16.dp)) }
                     }
                }
                 if (uiState.error != null) {
                     Text("Error: ${uiState.error}", color = MaterialTheme.colorScheme.error, modifier = Modifier.align(androidx.compose.ui.Alignment.BottomCenter).padding(16.dp))
                }
            }
        }
    }
}
