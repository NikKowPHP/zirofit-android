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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
        if (uiState.isLoading && uiState.featuredTrainers.isEmpty() && uiState.featuredEvents.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = StrongBlue)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                // 1. Featured Trainers Carousel - FIRST
                if (uiState.featuredTrainers.isNotEmpty()) {
                    item {
                        SectionHeaderWithAction(
                            title = "Featured Trainers",
                            onSeeAllClick = { /* Navigate to all trainers */ }
                        )
                    }
                    item {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(uiState.featuredTrainers) { trainer ->
                                InteractiveTrainerCard(
                                    trainer = trainer,
                                    onClick = { onNavigateToTrainer(trainer.id) }
                                )
                            }
                        }
                    }
                }

                // 2. Featured Events Carousel - SECOND
                if (uiState.featuredEvents.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        SectionHeaderWithAction(
                            title = "Featured Events",
                            onSeeAllClick = { /* Navigate to all events */ }
                        )
                    }
                    item {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(uiState.featuredEvents) { event ->
                                InteractiveEventCard(
                                    event = event,
                                    onClick = { onNavigateToEvent(event.id) }
                                )
                            }
                        }
                    }
                }

                // 3. Category Filter
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
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

                // 4. Upcoming Events - Grouped by Date
                if (uiState.upcomingEvents.isEmpty() && !uiState.isLoading) {
                    item {
                        Spacer(modifier = Modifier.height(32.dp))
                        EmptyStateView(
                            icon = Icons.Default.EventBusy,
                            title = "No Upcoming Events",
                            subtitle = "Check back later for new workshops and meetups"
                        )
                    }
                } else {
                    uiState.upcomingEvents.forEach { (date, events) ->
                        stickyHeader {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(StrongBackground)
                                    .padding(16.dp, 8.dp)
                            ) {
                                Text(
                                    formatDateHeader(date),
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                        items(events) { event ->
                            CompactEventCard(
                                event = event,
                                onClick = { onNavigateToEvent(event.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeaderWithAction(title: String, onSeeAllClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onSeeAllClick() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            title,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "See All",
                color = StrongBlue,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = StrongBlue,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun InteractiveTrainerCard(trainer: TrainerSummary, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = StrongSecondaryBackground),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // Profile Image with gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                AsyncImage(
                    model = trainer.profile?.profilePhotoPath,
                    contentDescription = trainer.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentScale = ContentScale.Crop
                )
                
                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    StrongSecondaryBackground.copy(alpha = 0.8f)
                                ),
                                startY = 100f
                            )
                        )
                )

                // Rating badge
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        String.format("%.1f", trainer.profile?.averageRating ?: 5.0),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Verified badge
                if (trainer.username != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .background(StrongBlue, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            Icons.Default.Verified,
                            contentDescription = "Verified",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            // Trainer Info
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    trainer.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    trainer.profile?.certifications?.split(",")?.firstOrNull()?.trim() ?: "Pro Trainer",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                // Show first certification if available
                trainer.profile?.certifications?.let { certs ->
                    if (certs.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            certs.split(",").firstOrNull()?.trim() ?: "Certified Trainer",
                            color = StrongBlue,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InteractiveEventCard(event: ExploreEvent, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(300.dp)
            .height(180.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box {
            AsyncImage(
                model = event.imageUrl,
                contentDescription = event.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            )
                        )
                    )
            )

            // Price badge
            event.priceDisplay?.let { price ->
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .background(StrongGreen, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        price,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Event info
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    event.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        event.locationName,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        tint = StrongBlue,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        formatEventDateTime(event.startTime),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            //spots left
            if (event.spotsLeft > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                        .background(
                            if (event.isNearCapacity == true) StrongRed else Color.Black.copy(alpha = 0.6f),
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        "${event.spotsLeft} spots left",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun CompactEventCard(event: ExploreEvent, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = StrongSecondaryBackground),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = event.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = event.locationName,
                    color = Color.Gray,
                    fontSize = 12.sp,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        tint = StrongBlue,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formatEventDateTime(event.startTime),
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }
            
            Column(horizontalAlignment = Alignment.End) {
                event.priceDisplay?.let { price ->
                    Text(
                        text = price,
                        color = StrongGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                if (event.spotsLeft > 0) {
                    Text(
                        text = "${event.spotsLeft} left",
                        color = if (event.isNearCapacity == true) StrongRed else Color.Gray,
                        fontSize = 10.sp
                    )
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
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onCityClick() }
        ) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = StrongBlue)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    selectedCity?.name ?: "Select City",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                if (selectedCity?.isCurrentLocation == true) {
                    Text("Current Location", color = Color.Gray, fontSize = 10.sp)
                }
            }
            Icon(Icons.Default.KeyboardArrowDown, null, tint = Color.Gray)
        }
        IconButton(onClick = onMapClick) {
            Icon(Icons.Default.Map, null, tint = Color.White)
        }
    }
}

@Composable
fun EmptyStateView(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            icon,
            null,
            modifier = Modifier.size(64.dp),
            tint = StrongTextSecondary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        Text(
            subtitle,
            color = StrongTextSecondary,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        title,
        color = Color.White,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(16.dp)
    )
}

private fun formatDateHeader(dateStr: String): String {
    return try {
        val parts = dateStr.split("-")
        if (parts.size == 3) {
            val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
            val month = monthNames[parts[1].toInt() - 1]
            "$month ${parts[2]}, ${parts[0]}"
        } else dateStr
    } catch (e: Exception) {
        dateStr
    }
}

private fun formatEventDateTime(dateTime: String): String {
    return try {
        val formatted = dateTime.take(16).replace("T", " ")
        formatted.substringAfter(" ")
    } catch (e: Exception) {
        dateTime
    }
}
