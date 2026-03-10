package com.ziro.fit.ui.discovery

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.ziro.fit.model.ExploreEvent
import com.ziro.fit.ui.theme.BackgroundDark
import com.ziro.fit.ui.theme.PrimaryGold
import com.ziro.fit.ui.theme.SurfaceDark
import com.ziro.fit.viewmodel.EventsViewModel

@Composable
fun EventsListScreen(
    onBack: () -> Unit,
    onNavigateToEvent: (String) -> Unit,
    viewModel: EventsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Explore Events", color = Color.White) },
                backgroundColor = BackgroundDark,
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Open filters */ }) {
                        Icon(Icons.Default.FilterAlt, contentDescription = "Filter", tint = PrimaryGold)
                    }
                }
            )
        },
        backgroundColor = BackgroundDark
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search Bar
            TextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchQueryChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search events...", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = SurfaceDark,
                    textColor = Color.White,
                    focusedIndicatorColor = PrimaryGold,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp)
            )

            // Category Chips (Horizontal Scroll would be better, but keeping it simple)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CategoryChip("All", uiState.selectedCategory == null) { viewModel.onCategorySelected(null) }
                CategoryChip("Workouts", uiState.selectedCategory == "workouts") { viewModel.onCategorySelected("workouts") }
                CategoryChip("Seminars", uiState.selectedCategory == "seminars") { viewModel.onCategorySelected("seminars") }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isLoading && uiState.events.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryGold)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.events) { event ->
                        EventItem(event = event, onClick = { onNavigateToEvent(event.id) })
                    }
                    
                    if (uiState.hasMore) {
                        item {
                            LaunchedEffect(Unit) {
                                viewModel.loadEvents()
                            }
                            Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = PrimaryGold, strokeWidth = 2.dp)
                            }
                        }
                    }
                }
            }
        }
    }

    if (uiState.error != null) {
        AlertDialog(
            onDismissRequest = viewModel::clearError,
            title = { Text("Error") },
            text = { Text(uiState.error ?: "Unknown error") },
            confirmButton = {
                TextButton(onClick = viewModel::clearError) { Text("OK") }
            }
        )
    }
}

@Composable
fun CategoryChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) PrimaryGold else SurfaceDark,
        elevation = 2.dp
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = if (isSelected) Color.Black else Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun EventItem(event: ExploreEvent, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        backgroundColor = SurfaceDark,
        elevation = 4.dp
    ) {
        Column {
            Box {
                AsyncImage(
                    model = event.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop
                )
                
                // Price Badge
                Surface(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopEnd),
                    color = Color.Black.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = event.priceDisplay ?: "Free",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = PrimaryGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
            
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = event.title,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = event.locationName,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = event.startTime.take(10), // Simple date extraction
                        color = PrimaryGold,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "${event.enrolledCount ?: 0}/${event.capacity ?: "∞"}",
                        color = if (event.isNearCapacity == true) Color.Red else Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
