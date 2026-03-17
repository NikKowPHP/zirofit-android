package com.ziro.fit.ui.discovery

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.ziro.fit.model.*
import com.ziro.fit.ui.theme.*
import com.ziro.fit.viewmodel.ExploreViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ExploreScreen(
    onNavigateToEvent: (String) -> Unit,
    onNavigateToTrainer: (String) -> Unit,
    onNavigateToMap: () -> Unit,
    viewModel: ExploreViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            ExploreHeader(
                selectedCity = uiState.selectedCity,
                onCityClick = { /* Show City Picker Dialog */ },
                onMapClick = onNavigateToMap
            )
        },
        containerColor = StrongBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // 1. Featured Trainers Carousel
            if (uiState.featuredTrainers.isNotEmpty()) {
                item {
                    SectionHeader("Featured Trainers")
                    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(uiState.featuredTrainers) { trainer ->
                            FeaturedTrainerCard(trainer, onClick = { onNavigateToTrainer(trainer.id) })
                        }
                    }
                }
            }

            // 2. Featured Events Carousel
            if (uiState.featuredEvents.isNotEmpty()) {
                item {
                    SectionHeader("Featured Events")
                    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(uiState.featuredEvents) { event ->
                            FeaturedEventCard(event, onClick = { onNavigateToEvent(event.id) })
                        }
                    }
                }
            }

            // 3. Category Filter
            item {
                Spacer(Modifier.height(24.dp))
                LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(uiState.categories) { category ->
                        FilterChip(
                            selected = uiState.selectedCategory?.id == category.id,
                            onClick = { viewModel.selectCategory(category) },
                            label = { Text(category.name) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = StrongBlue,
                                labelColor = Color.White,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // 4. Grouped Events List
            uiState.upcomingEvents.forEach { (date, events) ->
                stickyHeader {
                    Box(Modifier.fillMaxWidth().background(StrongBackground).padding(16.dp, 8.dp)) {
                        Text(date, color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
                items(events) { event ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .clickable { onNavigateToEvent(event.id) },
                        colors = CardDefaults.cardColors(containerColor = StrongSecondaryBackground),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Event Image
                            AsyncImage(
                                model = event.imageUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            
                            Spacer(Modifier.width(12.dp))
                            
                            // Event Details
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = event.title,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 2
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = event.locationName,
                                    color = Color.Gray,
                                    fontSize = 12.sp,
                                    maxLines = 1
                                )
                                Spacer(Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Schedule,
                                        contentDescription = null,
                                        tint = StrongBlue,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        text = event.startTime.take(16).replace("T", " "),
                                        color = Color.Gray,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                            
                            // Price or spots
                            Column(horizontalAlignment = Alignment.End) {
                                event.priceDisplay?.let { price ->
                                    Text(
                                        text = price,
                                        color = StrongGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                if (event.spotsLeft > 0) {
                                    Text(
                                        text = "${event.spotsLeft} spots left",
                                        color = if (event.isNearCapacity == true) StrongRed else Color.Gray,
                                        fontSize = 10.sp
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

@Composable
fun ExploreHeader(selectedCity: ExploreCity?, onCityClick: () -> Unit, onMapClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onCityClick() }) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = StrongBlue)
            Spacer(Modifier.width(8.dp))
            Column {
                Text(selectedCity?.name ?: "Select City", color = Color.White, fontWeight = FontWeight.Bold)
                if (selectedCity?.isCurrentLocation == true) Text("Current Location", color = Color.Gray, fontSize = 10.sp)
            }
            Icon(Icons.Default.KeyboardArrowDown, null, tint = Color.Gray)
        }
        IconButton(onClick = onMapClick) {
            Icon(Icons.Default.Map, null, tint = Color.White)
        }
    }
}

@Composable
fun FeaturedTrainerCard(trainer: TrainerSummary, onClick: () -> Unit) {
    Column(Modifier.width(140.dp).clickable { onClick() }) {
        AsyncImage(
            model = trainer.profile?.profilePhotoPath,
            contentDescription = null,
            modifier = Modifier.size(140.dp).clip(RoundedCornerShape(12.dp)).background(StrongSecondaryBackground),
            contentScale = ContentScale.Crop
        )
        Text(trainer.name, color = Color.White, fontWeight = FontWeight.Bold, maxLines = 1, modifier = Modifier.padding(top = 8.dp))
        Text(trainer.profile?.certifications?.split(",")?.firstOrNull() ?: "Pro Trainer", color = Color.Gray, fontSize = 12.sp)
    }
}

@Composable
fun FeaturedEventCard(event: ExploreEvent, onClick: () -> Unit) {
    Card(
        Modifier.width(280.dp).height(160.dp).clickable { onClick() },
        shape = RoundedCornerShape(16.dp)
    ) {
        Box {
            AsyncImage(event.imageUrl, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)))
            Column(Modifier.padding(16.dp).align(Alignment.BottomStart)) {
                Text(event.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(event.locationName, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
}
