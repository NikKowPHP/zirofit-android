package com.ziro.fit.ui.discovery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.ui.platform.LocalContext
import com.ziro.fit.model.ProfilePackage
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.ziro.fit.model.PublicTrainerProfileResponse
import com.ziro.fit.viewmodel.TrainerPublicProfileViewModel
import com.ziro.fit.viewmodel.TrainerPublicProfileUiState
import com.ziro.fit.ui.components.BookingDialog
import com.ziro.fit.ui.components.MonthlyCalendarView
import com.ziro.fit.ui.components.TimeSlotPicker



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainerPublicProfileScreen(
    trainerId: String,
    onNavigateBack: () -> Unit,
    viewModel: TrainerPublicProfileViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(trainerId) {
        viewModel.loadTrainerProfile(trainerId)
    }

    val uiState by viewModel.uiState.collectAsState()
    
    // Show booking success snackbar
    LaunchedEffect(uiState.bookingSuccess) {
        if (uiState.bookingSuccess) {
            // Reset after showing
            kotlinx.coroutines.delay(2000)
            viewModel.resetBookingState()
        }
    }

    // Show link success/error snackbar
    LaunchedEffect(uiState.linkSuccess, uiState.linkError, uiState.unlinkSuccess, uiState.unlinkError) {
        if (uiState.linkSuccess || uiState.linkError != null || uiState.unlinkSuccess || uiState.unlinkError != null) {
            kotlinx.coroutines.delay(3000)
            viewModel.resetLinkState()
        }
    }

    // Handle Checkout Navigation
    LaunchedEffect(uiState.checkoutUrl) {
        uiState.checkoutUrl?.let { url ->
            val intent = CustomTabsIntent.Builder()
                .setShowTitle(true)
                .build()
            try {
                intent.launchUrl(context, Uri.parse(url))
                viewModel.onCheckoutLaunched()
            } catch (e: Exception) {
                // Fallback to standard browser if Chrome not available
                val browserIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(browserIntent)
                viewModel.onCheckoutLaunched()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trainer Profile") },
                actions = {
                    val profile = uiState.profile
                    if (profile != null) {
                        val isCurrentlyLinkedToThis = uiState.linkedTrainerId == profile.id
                        val isNotLinkedToAnyone = uiState.linkedTrainerId == null

                        if (uiState.isLinking || uiState.isUnlinking) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        } else if (isCurrentlyLinkedToThis) {
                            OutlinedButton(
                                onClick = { viewModel.unlinkFromTrainer() },
                                modifier = Modifier.padding(end = 8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text("Unlink")
                            }
                        } else if (isNotLinkedToAnyone) {
                            Button(
                                onClick = { 
                                    profile.username?.let { viewModel.linkWithTrainer(it) }
                                },
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text("Link")
                            }
                        } else {
                            // Linked to someone else
                            AssistChip(
                                onClick = { /* Could allow switching here */ },
                                label = { Text("Already Linked") },
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = remember { SnackbarHostState() }.apply {
                LaunchedEffect(uiState.bookingSuccess, uiState.linkSuccess, uiState.unlinkSuccess, uiState.linkError, uiState.unlinkError) {
                    if (uiState.bookingSuccess) showSnackbar("Booking request sent successfully!")
                    if (uiState.linkSuccess) showSnackbar("You have shared your training data with ${uiState.profile?.name ?: "your trainer"}.")
                    if (uiState.unlinkSuccess) showSnackbar("Successfully unlinked from trainer!")
                    uiState.linkError?.let { showSnackbar("Error: $it") }
                    uiState.unlinkError?.let { showSnackbar("Error: $it") }
                    uiState.checkoutError?.let { showSnackbar("Checkout Error: $it") }
                }
            })
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.error != null) {
                Text(
                    text = uiState.error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (uiState.profile != null) {
                TrainerProfileContent(
                    profile = uiState.profile!!,
                    uiState = uiState,
                    viewModel = viewModel
                )
            }
        }
    }
    
    // Booking dialog
    uiState.selectedTimeSlot?.let { timeSlot ->
        BookingDialog(
            timeSlot = timeSlot,
            trainerName = uiState.profile?.name ?: "Trainer",
            isLoading = uiState.isCreatingBooking,
            error = uiState.bookingError,
            onConfirm = { notes ->
                uiState.profile?.id?.let { trainerId ->
                    viewModel.createBooking(trainerId, notes)
                }
            },
            onDismiss = { viewModel.resetBookingState() }
        )
    }
}

@Composable
fun TrainerProfileContent(
    profile: PublicTrainerProfileResponse,
    uiState: TrainerPublicProfileUiState,
    viewModel: TrainerPublicProfileViewModel
) {
    val details = profile.profile
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = details.images.profilePhoto,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = profile.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                if (profile.username != null) {
                    Text(
                        text = "@${profile.username}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = profile.role,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                
                if (!details.bio.aboutMe.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = details.bio.aboutMe,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Link/Unlink Action Button (In Body)
                val isCurrentlyLinkedToThis = uiState.linkedTrainerId == profile.id
                val isNotLinkedToAnyone = uiState.linkedTrainerId == null
                
                if (isCurrentlyLinkedToThis) {
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = { viewModel.unlinkFromTrainer() },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Unlink from Trainer")
                    }
                } else if (isNotLinkedToAnyone) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { 
                            profile.username?.let { viewModel.linkWithTrainer(it) }
                        }
                    ) {
                        Text("Link with Trainer")
                    }
                }

                // Professional Stats
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(), 
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    if (details.professional.averageRating != null) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Text(text = "${details.professional.averageRating}", style = MaterialTheme.typography.titleMedium)
                            Text(text = "Rating", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    if (details.professional.minServicePrice != null) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "$${details.professional.minServicePrice}+", style = MaterialTheme.typography.titleMedium)
                            Text(text = "Starting Price", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
        
        // Philosophy & Methodology
        if (!details.bio.philosophy.isNullOrBlank() || !details.bio.methodology.isNullOrBlank()) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (!details.bio.philosophy.isNullOrBlank()) {
                            Text(text = "Philosophy", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(text = details.bio.philosophy, style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        if (!details.bio.methodology.isNullOrBlank()) {
                            Text(text = "Methodology", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(text = details.bio.methodology, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }

        // Specialties & Certifications
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (details.professional.specialties?.isNotEmpty() == true) {
                    Text(text = "Specialties", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        items(details.professional.specialties) { specialty ->
                            SuggestionChip(onClick = {}, label = { Text(specialty) })
                        }
                    }
                }
                
                if (!details.professional.certifications.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Certifications", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(text = details.professional.certifications, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        
        // Locations
        if (details.locations?.isNotEmpty() == true) {
            item {
                Text(text = "Locations", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    details.locations.forEach { location ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Place, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = location.address, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }

        // Services
        if (details.services?.isNotEmpty() == true) {
            item {
                Text(text = "Services", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    details.services.forEach { service ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                    Text(text = service.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    if (service.price != null) {
                                        Text(
                                            text = "${service.currency ?: "$"} ${service.price}",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                                if (!service.description.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = service.description, style = MaterialTheme.typography.bodySmall)
                                }
                                if (service.duration != null && service.duration > 0) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "${service.duration.toInt()} mins", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Packages Section
        if (profile.profile.packages?.isNotEmpty() == true) {
            item {
                Text(text = "Training Packages", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    profile.profile.packages.forEach { pkg ->
                        PackageCard(
                            pkg = pkg,
                            isCheckingOut = uiState.isCheckingOut,
                            onBuy = { viewModel.purchasePackage(pkg.id) }
                        )
                    }
                }
            }
        }

        // Book a Session
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Book a Session",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Check if trainer has availability configured
                    val hasAvailability = uiState.schedule?.availability?.isNotEmpty() == true
                    
                    if (!hasAvailability && uiState.schedule != null) {
                        // Show message when no availability is set
                        Text(
                            text = "This trainer hasn't set their availability yet. Please check back later or contact them directly.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    } else {
                        Text(
                            text = "Select a date and time to book a session",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }

        // Monthly Calendar (only show if trainer has availability configured)
        if (uiState.schedule != null && uiState.schedule.availability.isNotEmpty()) {
            item {
                com.ziro.fit.ui.components.MonthlyCalendarView(
                    selectedDate = uiState.selectedDate,
                    bookedSlots = uiState.schedule.bookings,
                    onDateSelected = { date ->
                        viewModel.selectDate(date)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else if (uiState.isLoadingSchedule) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        } else if (uiState.scheduleError != null) {
            item {
                Text(
                    text = uiState.scheduleError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        // Time Slot Picker (shown when date is selected and availability exists)
        if (uiState.selectedDate != null && uiState.schedule != null && uiState.schedule.availability.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                com.ziro.fit.ui.components.TimeSlotPicker(
                    selectedDate = uiState.selectedDate,
                    availability = uiState.schedule.availability,
                    bookedSlots = uiState.schedule.bookings,
                    selectedTimeSlot = uiState.selectedTimeSlot,
                    onTimeSlotSelected = { timeSlot ->
                        viewModel.selectTimeSlot(timeSlot)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Book Now Button
        if (uiState.selectedTimeSlot != null) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { /* Dialog will open automatically via selectedTimeSlot */ },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isCreatingBooking
                ) {
                    Text("Book This Time Slot")
                }
            }
        }

        // Transformations

        if (details.transformations?.isNotEmpty() == true) {
            item {
                Text(text = "Transformations", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(230.dp)
                ) {
                    items(details.transformations) { trans ->
                        Card(modifier = Modifier.width(200.dp)) {
                            Column {
                                AsyncImage(
                                    model = trans.imagePath,
                                    contentDescription = "Transformation",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(150.dp),
                                    contentScale = ContentScale.Crop
                                )
                                if (!trans.caption.isNullOrBlank()) {
                                    Text(
                                        text = trans.caption, 
                                        modifier = Modifier.padding(8.dp),
                                        style = MaterialTheme.typography.bodySmall,
                                        maxLines = 2
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Testimonials
        if (details.testimonials?.isNotEmpty() == true) {
            item {
                Text(text = "Testimonials", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    details.testimonials.forEach { testimonial ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = "\"${testimonial.text}\"", style = MaterialTheme.typography.bodyLarge, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "- ${testimonial.clientName}", style = MaterialTheme.typography.titleSmall)
                                    Spacer(modifier = Modifier.weight(1f))
                                    if (testimonial.rating != null) {
                                        Text(text = "★ ${testimonial.rating}", color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // Benefits
        if (details.benefits?.isNotEmpty() == true) {
            item {
                Text(text = "Benefits", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    details.benefits.forEach { benefit ->
                        Row(verticalAlignment = Alignment.Top) {
                            Text(text = "• ", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                            Column {
                                Text(text = benefit.title, style = MaterialTheme.typography.titleMedium)
                                if (benefit.description != null) {
                                    Text(text = benefit.description, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // Socials
        if (details.socials?.isNotEmpty() == true) {
            item {
                Text(text = "Connect", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    details.socials.forEach { social ->
                        // Just displaying text for now as we don't have icons for all platforms
                        AssistChip(
                            onClick = { /* Open URL */ },
                            label = { Text(social.platform) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PackageCard(
    pkg: com.ziro.fit.model.ProfilePackage,
    isCheckingOut: Boolean,
    onBuy: () -> Unit
) {
    Card(
        modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = androidx.compose.ui.Modifier.padding(16.dp)) {
            Text(
                text = pkg.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            if (!pkg.description.isNullOrBlank()) {
                Spacer(modifier = androidx.compose.ui.Modifier.height(4.dp))
                Text(
                    text = pkg.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (pkg.sessionCount != null) {
                Spacer(modifier = androidx.compose.ui.Modifier.height(4.dp))
                Text(
                    text = "${pkg.sessionCount} Sessions",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))

            Row(
                modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Text(
                    text = "$${pkg.price}",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = onBuy,
                    enabled = !isCheckingOut
                ) {
                    if (isCheckingOut) {
                        CircularProgressIndicator(
                            modifier = androidx.compose.ui.Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Purchase")
                    }
                }
            }
        }
    }
}
