package com.ziro.fit.ui.discovery

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.ziro.fit.model.ExploreEvent
import com.ziro.fit.model.TrainerSummary
import com.ziro.fit.ui.theme.*
import com.ziro.fit.viewmodel.TrainerDiscoveryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainerDiscoveryScreen(
    onNavigateBack: () -> Unit,
    onTrainerClick: (String) -> Unit,
    viewModel: TrainerDiscoveryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    val searchQuery by viewModel.searchQuery.collectAsState()
    val discoveryType by viewModel.discoveryType.collectAsState()
    val selectedSpecialty by viewModel.selectedSpecialty.collectAsState()
    val selectedLocation by viewModel.selectedLocation.collectAsState()
    
    var isMapView by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = StrongBackground,
        topBar = {
            TopAppBar(
                title = { Text("Discover", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = StrongSecondaryBackground),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { isMapView = !isMapView },
                containerColor = StrongBlue
            ) {
                Icon(
                    imageVector = if (isMapView) Icons.Default.List else Icons.Default.Map,
                    contentDescription = if (isMapView) "Show List" else "Show Map",
                    tint = Color.White
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Surface(
                color = StrongSecondaryBackground,
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                shadowElevation = 8.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        TrainerDiscoveryViewModel.DiscoveryType.values().forEachIndexed { index, type ->
                            SegmentedButton(
                                selected = discoveryType == type,
                                onClick = { viewModel.discoveryType.value = type },
                                shape = SegmentedButtonDefaults.itemShape(index = index, count = 3),
                                colors = SegmentedButtonDefaults.colors(
                                    activeContainerColor = StrongBlue,
                                    activeContentColor = Color.White,
                                    inactiveContainerColor = Color.Transparent,
                                    inactiveContentColor = StrongTextSecondary
                                )
                            ) {
                                Text(type.label)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.searchQuery.value = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Search...", color = StrongTextSecondary) },
                            leadingIcon = { Icon(Icons.Default.Search, null, tint = StrongTextSecondary) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = StrongInputBackground,
                                unfocusedContainerColor = StrongInputBackground,
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        IconButton(
                            onClick = { showFilterSheet = true },
                            modifier = Modifier
                                .background(if (hasActiveFilters(viewModel)) StrongBlue else StrongInputBackground, CircleShape)
                        ) {
                            Icon(Icons.Default.Tune, contentDescription = "Filter", tint = Color.White)
                        }
                    }
                    
                    if (hasActiveFilters(viewModel)) {
                        Spacer(modifier = Modifier.height(12.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            selectedSpecialty?.let { spec ->
                                item { FilterChipItem(spec) { viewModel.selectedSpecialty.value = null } }
                            }
                            if (selectedLocation.isNotBlank()) {
                                item { FilterChipItem(selectedLocation) { viewModel.selectedLocation.value = "" } }
                            }
                        }
                    }
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                if (uiState.isLoading && uiState.trainers.isEmpty() && uiState.events.isEmpty()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = StrongBlue)
                } else if (isMapView) {
                    TrainerMapScreen(trainers = uiState.trainers, onTrainerClick = onTrainerClick)
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (discoveryType == TrainerDiscoveryViewModel.DiscoveryType.SPECIALISTS || discoveryType == TrainerDiscoveryViewModel.DiscoveryType.ALL) {
                            if (uiState.trainers.isEmpty() && !uiState.isLoading) {
                                item { EmptyState("No trainers found") }
                            } else {
                                if (discoveryType == TrainerDiscoveryViewModel.DiscoveryType.ALL) {
                                    item { Text("Specialists", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold) }
                                }
                                items(uiState.trainers) { trainer ->
                                    TrainerCardItem(trainer, onTrainerClick)
                                }
                            }
                        }
                        
                        if (discoveryType == TrainerDiscoveryViewModel.DiscoveryType.EVENTS || discoveryType == TrainerDiscoveryViewModel.DiscoveryType.ALL) {
                            if (uiState.events.isEmpty() && !uiState.isLoading) {
                                item { EmptyState("No events found") }
                            } else {
                                if (discoveryType == TrainerDiscoveryViewModel.DiscoveryType.ALL) {
                                    item { Text("Events", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp)) }
                                }
                                items(uiState.events) { event ->
                                    EventCardItem(event) { }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        if (showFilterSheet) {
            FilterBottomSheet(
                viewModel = viewModel,
                onDismiss = { showFilterSheet = false }
            )
        }
    }
}

private fun hasActiveFilters(viewModel: TrainerDiscoveryViewModel): Boolean {
    return viewModel.selectedSpecialty.value != null || viewModel.selectedLocation.value.isNotBlank() || viewModel.minRating.value > 0.0
}

@Composable
fun FilterChipItem(text: String, onRemove: () -> Unit) {
    Surface(
        color = StrongBlue.copy(alpha = 0.2f),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, StrongBlue)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text, color = Color.White, fontSize = 12.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Icon(Icons.Default.Close, null, modifier = Modifier.size(14.dp).clickable { onRemove() }, tint = Color.White)
        }
    }
}

@Composable
fun EmptyState(message: String) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.SearchOff, null, modifier = Modifier.size(64.dp), tint = StrongTextSecondary)
        Spacer(modifier = Modifier.height(16.dp))
        Text(message, color = StrongTextSecondary, fontSize = 16.sp)
    }
}

@Composable
fun TrainerCardItem(trainer: TrainerSummary, onClick: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick(trainer.id) },
        colors = CardDefaults.cardColors(containerColor = StrongSecondaryBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = trainer.profile?.profilePhotoPath,
                contentDescription = null,
                modifier = Modifier.size(70.dp).clip(RoundedCornerShape(12.dp)).background(StrongBackground),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(trainer.name, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                trainer.profile?.certifications?.split(",")?.firstOrNull()?.let {
                    Text(it.trim(), color = StrongTextSecondary, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, null, tint = Color(0xFFFFD700), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(String.format("%.1f", trainer.profile?.averageRating ?: 5.0), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(Icons.Default.LocationOn, null, tint = StrongTextSecondary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(trainer.profile?.locations?.firstOrNull()?.address ?: "Online", color = StrongTextSecondary, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}

@Composable
fun EventCardItem(event: ExploreEvent, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = StrongSecondaryBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(70.dp).clip(RoundedCornerShape(12.dp)).background(StrongBlue.copy(alpha=0.2f)),
                contentAlignment = Alignment.Center
            ) {
                if (event.imageUrl != null) {
                    AsyncImage(model = event.imageUrl, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                } else {
                    Icon(Icons.Default.Event, null, tint = StrongBlue)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(event.title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(event.locationName, color = StrongTextSecondary, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(event.startTime.take(16).replace("T", " "), color = StrongBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(viewModel: TrainerDiscoveryViewModel, onDismiss: () -> Unit) {
    val specialties = listOf("Calisthenics", "Strength", "Yoga", "HIIT", "CrossFit", "Mobility", "Pilates", "Boxing")
    
    val selectedSpecialty by viewModel.selectedSpecialty.collectAsState()
    val minRating by viewModel.minRating.collectAsState()
    val selectedSort by viewModel.selectedSortOption.collectAsState()
    val location by viewModel.selectedLocation.collectAsState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = StrongSecondaryBackground
    ) {
        Column(modifier = Modifier.padding(24.dp).padding(bottom = 24.dp)) {
            Text("Filters", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))
            
            OutlinedTextField(
                value = location,
                onValueChange = { viewModel.selectedLocation.value = it },
                label = { Text("Location", color = StrongTextSecondary) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                    focusedBorderColor = StrongBlue, unfocusedBorderColor = StrongDivider
                ),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("Specialty", color = Color.White, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(specialties) { spec ->
                    FilterChip(
                        selected = selectedSpecialty == spec,
                        onClick = { viewModel.selectedSpecialty.value = if (selectedSpecialty == spec) null else spec },
                        label = { Text(spec) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = StrongBlue,
                            selectedLabelColor = Color.White,
                            labelColor = StrongTextSecondary
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("Sort By", color = Color.White, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(TrainerDiscoveryViewModel.SortOption.values().toList()) { opt ->
                    FilterChip(
                        selected = selectedSort == opt,
                        onClick = { viewModel.selectedSortOption.value = opt },
                        label = { Text(opt.label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = StrongBlue,
                            selectedLabelColor = Color.White,
                            labelColor = StrongTextSecondary
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                TextButton(onClick = { viewModel.resetFilters() }) {
                    Text("Reset", color = StrongRed)
                }
                Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = StrongBlue)) {
                    Text("Apply Filters")
                }
            }
        }
    }
}
