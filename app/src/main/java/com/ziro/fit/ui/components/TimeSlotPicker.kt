package com.ziro.fit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ziro.fit.model.BookedTimeSlot
import com.ziro.fit.viewmodel.TimeSlot
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


/**
 * Displays available time slots for a selected date
 */
@Composable
fun TimeSlotPicker(
    selectedDate: String,
    availability: Map<String, Any>,
    bookedSlots: List<BookedTimeSlot>,
    selectedTimeSlot: TimeSlot?,
    onTimeSlotSelected: (TimeSlot) -> Unit,
    modifier: Modifier = Modifier
) {
    val availableSlots = generateAvailableTimeSlots(selectedDate, availability, bookedSlots)
    
    Column(modifier = modifier) {
        Text(
            text = "Available Times",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        if (availableSlots.isEmpty()) {
            Text(
                text = "No available time slots for this date",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp)
            ) {
                items(availableSlots) { slot ->
                    TimeSlotCard(
                        timeSlot = slot,
                        isSelected = slot == selectedTimeSlot,
                        onClick = { onTimeSlotSelected(slot) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TimeSlotCard(
    timeSlot: TimeSlot,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = if (isSelected) {
            CardDefaults.outlinedCardBorder()
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = formatTimeSlotShort(timeSlot),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
            
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

private fun formatTimeSlotShort(timeSlot: TimeSlot): String {
    return try {
        // Parse ISO_INSTANT format (e.g., "2025-12-17T12:00:00Z")
        val start = java.time.Instant.parse(timeSlot.startTime)
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDateTime()
        val end = java.time.Instant.parse(timeSlot.endTime)
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDateTime()
        
        val formatter = DateTimeFormatter.ofPattern("h:mm a")
        "${start.format(formatter)} - ${end.format(formatter)}"
    } catch (e: Exception) {
        "${timeSlot.startTime} - ${timeSlot.endTime}"
    }
}

/**
 * Generate available time slots for a given date based on trainer's availability schedule
 */
private fun generateAvailableTimeSlots(
    date: String,
    availability: Map<String, Any>,
    bookedSlots: List<BookedTimeSlot>
): List<TimeSlot> {
    try {
        val selectedDate = LocalDate.parse(date)
        
        // Get day of week (mon, tue, wed, etc.)
        val dayOfWeek = selectedDate.dayOfWeek.toString().lowercase().substring(0, 3)
        
        // Get availability for this day
        val dayAvailability = availability[dayOfWeek] as? List<*> ?: return emptyList()
        
        val slots = mutableListOf<TimeSlot>()
        
        // Parse each time range for this day (e.g., "08:00-12:00", "14:00-18:00")
        dayAvailability.forEach { timeRangeAny ->
            val timeRange = timeRangeAny as? String ?: return@forEach
            val (startStr, endStr) = timeRange.split("-")
            
            // Parse start and end times
            val startHour = startStr.substring(0, 2).toInt()
            val startMinute = startStr.substring(3, 5).toInt()
            val endHour = endStr.substring(0, 2).toInt()
            val endMinute = endStr.substring(3, 5).toInt()
            
            // Generate hourly slots within this availability range
            var currentHour = startHour
            while (currentHour < endHour) {
                // Create datetime in local timezone, then convert to UTC
                val slotStartTime = LocalDateTime.of(selectedDate, LocalTime.of(currentHour, 0))
                    .atZone(java.time.ZoneId.systemDefault())
                    .withZoneSameInstant(ZoneOffset.UTC)
                val slotEndTime = slotStartTime.plusHours(1)
                
                val slot = TimeSlot(
                    startTime = DateTimeFormatter.ISO_INSTANT.format(slotStartTime),
                    endTime = DateTimeFormatter.ISO_INSTANT.format(slotEndTime)
                )
                
                // Check if this slot is not booked
                val isBooked = bookedSlots.any { bookedSlot ->
                    isTimeSlotOverlapping(slot, bookedSlot)
                }
                
                if (!isBooked) {
                    slots.add(slot)
                }
                
                currentHour++
            }
        }
        
        return slots
    } catch (e: Exception) {
        return emptyList()
    }
}

private fun isTimeSlotOverlapping(timeSlot: TimeSlot, bookedSlot: BookedTimeSlot): Boolean {
    return try {
        // Parse ISO_INSTANT format for time slots ("2025-12-17T12:00:00Z")
        val slotStart = java.time.Instant.parse(timeSlot.startTime)
        val slotEnd = java.time.Instant.parse(timeSlot.endTime)
        
        // Booked slots might come from API in different formats, try both
        val bookedStart = try {
            java.time.Instant.parse(bookedSlot.startTime)
        } catch (e: Exception) {
            LocalDateTime.parse(bookedSlot.startTime, DateTimeFormatter.ISO_DATE_TIME)
                .atZone(java.time.ZoneId.systemDefault())
                .toInstant()
        }
        val bookedEnd = try {
            java.time.Instant.parse(bookedSlot.endTime)
        } catch (e: Exception) {
            LocalDateTime.parse(bookedSlot.endTime, DateTimeFormatter.ISO_DATE_TIME)
                .atZone(java.time.ZoneId.systemDefault())
                .toInstant()
        }
        
        // Check if time ranges overlap
        slotStart.isBefore(bookedEnd) && slotEnd.isAfter(bookedStart)
    } catch (e: Exception) {
        false
    }
}
