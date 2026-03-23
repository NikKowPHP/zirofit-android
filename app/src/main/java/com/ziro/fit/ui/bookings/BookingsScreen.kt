package com.ziro.fit.ui.bookings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ziro.fit.model.Booking
import com.ziro.fit.model.BookingStatus
import com.ziro.fit.viewmodel.BookingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingsListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    viewModel: BookingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bookings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreate) {
                Icon(Icons.Default.Add, contentDescription = "Add Booking")
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            if (uiState.isLoading && uiState.bookings.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.bookings.isEmpty()) {
                Text(
                    text = "No bookings found.",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (uiState.pendingBookings.isNotEmpty()) {
                        item {
                            Text(
                                text = "Pending Requests",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(uiState.pendingBookings) { booking ->
                            PendingBookingItem(
                                booking = booking,
                                onApprove = { dataSharingApproved ->
                                    viewModel.confirmBooking(
                                        bookingId = booking.id,
                                        dataSharingApproved = dataSharingApproved,
                                        onSuccess = {}
                                    )
                                },
                                onDecline = {
                                    viewModel.declineBooking(booking.id) {}
                                }
                            )
                        }
                    }

                    if (uiState.confirmedBookings.isNotEmpty()) {
                        item {
                            Text(
                                text = "Confirmed",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(uiState.confirmedBookings) { booking ->
                            BookingItem(
                                booking = booking,
                                onClick = { onNavigateToEdit(booking.id) },
                                onDelete = { viewModel.deleteBooking(booking.id) }
                            )
                        }
                    }

                    if (uiState.cancelledBookings.isNotEmpty()) {
                        item {
                            Text(
                                text = "Declined/Cancelled",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(uiState.cancelledBookings) { booking ->
                            BookingItem(
                                booking = booking,
                                onClick = { onNavigateToEdit(booking.id) },
                                onDelete = { viewModel.deleteBooking(booking.id) }
                            )
                        }
                    }
                }
            }
            
            if (uiState.error != null) {
                Snackbar(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
                    action = {
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text("Dismiss")
                        }
                    }
                ) {
                    Text(uiState.error ?: "Unknown error")
                }
            }
            
            uiState.successMessage?.let { message ->
                LaunchedEffect(message) {
                    viewModel.clearSuccessMessage()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PendingBookingItem(
    booking: Booking,
    onApprove: (Boolean) -> Unit,
    onDecline: () -> Unit
) {
    var showApprovalDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = booking.clientName ?: "Unknown Client",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${booking.startTime} - ${booking.endTime}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (booking.clientNotes != null) {
                        Text(
                            text = "Notes: ${booking.clientNotes}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
                Text(
                    text = "PENDING",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Yellow
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onDecline,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Decline")
                }

                Button(
                    onClick = { showApprovalDialog = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Approve")
                }
            }
        }
    }

    if (showApprovalDialog) {
        AlertDialog(
            onDismissRequest = { showApprovalDialog = false },
            title = { Text("Approve Booking") },
            text = {
                Column {
                    Text("Approve this booking request from ${booking.clientName ?: "client"}?")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Enable client data sharing?",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "This will allow you to view the client's workouts, measurements, photos, and check-ins.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    onApprove(true)
                    showApprovalDialog = false
                }) {
                    Text("Approve with Data Sharing")
                }
            },
            dismissButton = {
                Row {
                    TextButton(onClick = {
                        onApprove(false)
                        showApprovalDialog = false
                    }) {
                        Text("Approve Only")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = { showApprovalDialog = false }) {
                        Text("Cancel")
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingItem(
    booking: Booking,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = booking.clientName ?: "Unknown Client",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${booking.startTime} - ${booking.endTime}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = booking.status.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = when(booking.status) {
                        BookingStatus.CONFIRMED -> Color.Green
                        BookingStatus.PENDING -> Color.Yellow
                        BookingStatus.CANCELLED -> Color.Red
                    }
                )
                if (booking.dataSharingApproved == true) {
                    Text(
                        text = "Data sharing enabled",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF4CAF50)
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}
