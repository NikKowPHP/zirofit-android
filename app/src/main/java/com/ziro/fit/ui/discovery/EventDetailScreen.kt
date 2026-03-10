package com.ziro.fit.ui.discovery

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.ziro.fit.model.ExploreEvent
import com.ziro.fit.ui.theme.BackgroundDark
import com.ziro.fit.ui.theme.PrimaryGold
import com.ziro.fit.ui.theme.SurfaceDark
import com.ziro.fit.viewmodel.EventDetailViewModel

@Composable
fun EventDetailScreen(
    eventId: String,
    onBack: () -> Unit,
    viewModel: EventDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(eventId) {
        viewModel.loadEventDetails(eventId)
    }

    // Handle checkout redirect
    LaunchedEffect(uiState.checkoutUrl) {
        uiState.checkoutUrl?.let { url ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
            viewModel.clearCheckoutUrl()
        }
    }

    Scaffold(
        backgroundColor = BackgroundDark,
        topBar = {
            TopAppBar(
                title = { Text("Event Details") },
                backgroundColor = Color.Transparent,
                elevation = 0.dp,
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading && uiState.event == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryGold)
            }
        } else {
            uiState.event?.let { event ->
                EventDetailContent(
                    event = event,
                    onEnroll = { viewModel.enroll(event) },
                    isLoading = uiState.isLoading,
                    joinSuccess = uiState.joinSuccess
                )
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
fun EventDetailContent(
    event: ExploreEvent,
    onEnroll: () -> Unit,
    isLoading: Boolean,
    joinSuccess: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        AsyncImage(
            model = event.imageUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = event.title,
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = PrimaryGold, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = event.startTime, color = Color.LightGray, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = PrimaryGold, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = event.locationName, color = Color.LightGray, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Capacity Progress
            val progress = (event.enrolledCount?.toFloat() ?: 0f) / (event.capacity?.toFloat() ?: 1f)
            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Capacity", color = Color.White, fontSize = 14.sp)
                    Text("${event.enrolledCount ?: 0}/${event.capacity ?: "∞"}", color = PrimaryGold, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = progress.coerceIn(0f, 1f),
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = if (progress >= 0.9f) Color.Red else PrimaryGold,
                    backgroundColor = SurfaceDark
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("About this event", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = event.description ?: "No description provided.",
                color = Color.Gray,
                lineHeight = 22.sp,
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Host Info
            if (event.resolvedHostName != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    backgroundColor = SurfaceDark
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = event.trainer?.profile?.profilePhotoPath,
                            contentDescription = null,
                            modifier = Modifier.size(50.dp).clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Hosted by", color = Color.Gray, fontSize = 12.sp)
                            Text(event.resolvedHostName!!, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            if (joinSuccess) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.Green.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Green)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("You are enrolled!", color = Color.White)
                    }
                }
            } else {
                Button(
                    onClick = onEnroll,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = PrimaryGold),
                    enabled = !isLoading && (event.isBooked != true) && !event.isFull
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
                    } else {
                        val text = when {
                            event.isBooked == true -> "Already Enrolled"
                            event.isFull -> "Event Full"
                            else -> if (event.price != null && event.price > 0) "Buy Ticket (${event.priceDisplay})" else "Join for Free"
                        }
                        Text(text, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
