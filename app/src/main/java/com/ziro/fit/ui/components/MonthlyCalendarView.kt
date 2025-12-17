package com.ziro.fit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ziro.fit.model.BookedTimeSlot
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

/**
 * Monthly calendar view showing available and unavailable dates
 * for trainer booking
 */
@Composable
fun MonthlyCalendarView(
    selectedDate: String?,
    bookedSlots: List<BookedTimeSlot>,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    
    Column(modifier = modifier) {
        // Month navigation header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "Previous month")
            }
            
            Text(
                text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                Icon(Icons.Default.ChevronRight, contentDescription = "Next month")
            }
        }
        
        // Day of week headers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Calendar grid
        val daysInMonth = getDaysInMonth(currentMonth)
        val bookedDates = getBookedDates(bookedSlots)
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            contentPadding = PaddingValues(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 400.dp)
        ) {
            items(daysInMonth) { day ->
                CalendarDay(
                    day = day,
                    isSelected = day.date == selectedDate,
                    isBooked = bookedDates.contains(day.date),
                    isPast = day.localDate.isBefore(LocalDate.now()),
                    onDayClick = { if (!day.isEmpty) onDateSelected(day.date) }
                )
            }
        }
    }
}

@Composable
private fun CalendarDay(
    day: CalendarDayData,
    isSelected: Boolean,
    isBooked: Boolean,
    isPast: Boolean,
    onDayClick: () -> Unit
) {
    if (day.isEmpty) {
        // Empty cell for days before month starts
        Box(modifier = Modifier.size(48.dp))
    } else {
        val backgroundColor = when {
            isSelected -> MaterialTheme.colorScheme.primary
            isBooked -> MaterialTheme.colorScheme.errorContainer
            isPast -> MaterialTheme.colorScheme.surfaceVariant
            else -> MaterialTheme.colorScheme.surface
        }
        
        val textColor = when {
            isSelected -> MaterialTheme.colorScheme.onPrimary
            isBooked -> MaterialTheme.colorScheme.onErrorContainer
            isPast -> MaterialTheme.colorScheme.outline
            else -> MaterialTheme.colorScheme.onSurface
        }
        
        Box(
            modifier = Modifier
                .size(48.dp)
                .padding(2.dp)
                .background(
                    color = backgroundColor,
                    shape = MaterialTheme.shapes.small
                )
                .border(
                    width = if (isSelected) 2.dp else 0.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                    shape = MaterialTheme.shapes.small
                )
                .clickable(enabled = !isPast && !isBooked) { onDayClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = day.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

private data class CalendarDayData(
    val dayOfMonth: Int,
    val date: String,
    val localDate: LocalDate,
    val isEmpty: Boolean = false
)

private fun getDaysInMonth(yearMonth: YearMonth): List<CalendarDayData> {
    val firstDayOfMonth = yearMonth.atDay(1)
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7 // Sunday = 0
    
    val days = mutableListOf<CalendarDayData>()
    
    // Add empty cells for days before month starts
    repeat(firstDayOfWeek) {
        days.add(CalendarDayData(0, "", LocalDate.now(), isEmpty = true))
    }
    
    // Add actual days of the month
    for (day in 1..daysInMonth) {
        val date = yearMonth.atDay(day)
        days.add(
            CalendarDayData(
                dayOfMonth = day,
                date = date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                localDate = date
            )
        )
    }
    
    return days
}

private fun getBookedDates(bookedSlots: List<BookedTimeSlot>): Set<String> {
    return bookedSlots.map { slot ->
        // Extract date from ISO-8601 datetime string
        try {
            slot.startTime.substring(0, 10) // Gets YYYY-MM-DD
        } catch (e: Exception) {
            ""
        }
    }.filter { it.isNotEmpty() }.toSet()
}
