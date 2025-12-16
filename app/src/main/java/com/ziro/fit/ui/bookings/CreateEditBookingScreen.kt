package com.ziro.fit.ui.bookings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ziro.fit.model.Booking
import com.ziro.fit.model.BookingStatus
import com.ziro.fit.model.UpdateBookingRequest
import com.ziro.fit.viewmodel.BookingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditBookingScreen(
    onNavigateBack: () -> Unit,
    bookingId: String? = null,
    viewModel: BookingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Form State
    var clientName by remember { mutableStateOf("") }
    var clientEmail by remember { mutableStateOf("") }
    var clientNotes by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    var status by remember { mutableStateOf(BookingStatus.PENDING) }
    
    // Initialize state if editing
    LaunchedEffect(bookingId, uiState.bookings) {
        if (bookingId != null) {
            val booking = uiState.bookings.find { it.id == bookingId }
            if (booking != null) {
                clientName = booking.clientName ?: ""
                clientEmail = booking.clientEmail ?: ""
                clientNotes = booking.clientNotes ?: ""
                startTime = booking.startTime
                endTime = booking.endTime
                status = booking.status
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (bookingId == null) "New Booking" else "Edit Booking") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (uiState.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            OutlinedTextField(
                value = clientName,
                onValueChange = { clientName = it },
                label = { Text("Client Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = clientEmail,
                onValueChange = { clientEmail = it },
                label = { Text("Client Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = startTime,
                onValueChange = { startTime = it },
                label = { Text("Start Time (ISO 8601)") },
                placeholder = { Text("2023-10-27T10:00:00Z") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = endTime,
                onValueChange = { endTime = it },
                label = { Text("End Time (ISO 8601)") },
                placeholder = { Text("2023-10-27T11:00:00Z") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = clientNotes,
                onValueChange = { clientNotes = it },
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            
            // Status Dropdown (Simplified as a text list or simple selector for now)
            // Ideally use an ExposedDropdownMenuBox, but for speed keeping simple if needed.
            // Let's implement a simple row of chips or radio buttons for status if editing
            if (bookingId != null) {
                Text("Status", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    BookingStatus.values().forEach { s ->
                        FilterChip(
                            selected = status == s,
                            onClick = { status = s },
                            label = { Text(s.name) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (bookingId == null) {
                        viewModel.createBooking(
                            trainerId = "current_trainer", // Ideally fetched from auth/profile
                            startTime = startTime,
                            endTime = endTime,
                            clientName = clientName,
                            clientEmail = clientEmail,
                            clientNotes = clientNotes,
                            onSuccess = onNavigateBack
                        )
                    } else {
                        viewModel.updateBooking(
                            bookingId = bookingId,
                            request = UpdateBookingRequest(
                                startTime = startTime,
                                endTime = endTime,
                                status = status,
                                clientName = clientName,
                                clientEmail = clientEmail,
                                clientNotes = clientNotes
                            ),
                            onSuccess = onNavigateBack
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            ) {
                Text(if (bookingId == null) "Create Booking" else "Save Changes")
            }
        }
    }
}
