package com.ziro.fit.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ziro.fit.viewmodel.TimeSlot
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Dialog for logged-in unlinked clients to book a session with a trainer
 */
@Composable
fun BookingDialog(
    timeSlot: TimeSlot,
    trainerName: String,
    isLoading: Boolean,
    error: String?,
    onConfirm: (notes: String?) -> Unit,
    onDismiss: () -> Unit
) {
    var notes by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Book Session with $trainerName",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Display selected time slot
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Selected Time",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formatTimeSlot(timeSlot),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                
                // Notes input
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (Optional)") },
                    placeholder = { Text("Any special requests or information...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )
                
                // Error message
                if (error != null) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(notes.ifBlank { null }) },
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Confirm Booking")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text("Cancel")
            }
        }
    )
}

private fun formatTimeSlot(timeSlot: TimeSlot): String {
    return try {
        // Parse ISO_INSTANT format (e.g., "2025-12-17T12:00:00Z")
        val start = java.time.Instant.parse(timeSlot.startTime)
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDateTime()
        val end = java.time.Instant.parse(timeSlot.endTime)
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDateTime()
        
        val dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")
        val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")
        
        val date = start.format(dateFormatter)
        val startTime = start.format(timeFormatter)
        val endTime = end.format(timeFormatter)
        
        "$date\n$startTime - $endTime"
    } catch (e: Exception) {
        "${timeSlot.startTime} - ${timeSlot.endTime}"
    }
}
