package com.ziro.fit.ui.calendar

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import com.ziro.fit.model.CalendarEvent
import com.ziro.fit.model.ClientSummaryItem
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

// --- Week View ---

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WeekViewPager(
    pagerState: PagerState,
    selectedDate: LocalDate,
    clientSummaries: List<ClientSummaryItem>,
    onDateSelected: (LocalDate) -> Unit
) {
    val initialPage = Int.MAX_VALUE / 2
    
     HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxWidth()
    ) { page ->
        val weekOffset = page - initialPage
        val today = LocalDate.now()
        val startOfThisWeek = today.minusDays(today.dayOfWeek.value.toLong() - 1)
        val weekStartDate = startOfThisWeek.plusWeeks(weekOffset.toLong())
        
        WeekCalendarView(
            weekStartDate = weekStartDate,
            selectedDate = selectedDate,
            clientSummaries = clientSummaries,
            onDateSelected = onDateSelected
        )
    }
}

@Composable
fun WeekCalendarView(
    weekStartDate: LocalDate,
    selectedDate: LocalDate,
    clientSummaries: List<ClientSummaryItem>,
    onDateSelected: (LocalDate) -> Unit
) {
    val days = (0..6).map { weekStartDate.plusDays(it.toLong()) }

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        days.forEach { date ->
            val isSelected = date.isEqual(selectedDate)
            val isToday = date.isEqual(LocalDate.now())
            
            // Filter distinct clients for this day using the summary list
            val dayClients = clientSummaries
                .filter { 
                    try {
                        java.time.Instant.parse(it.date)
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate()
                            .isEqual(date)
                    } catch (e: Exception) {
                        false
                    }
                }
                .distinctBy { it.clientId }


            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onDateSelected(date) }
                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .padding(vertical = 8.dp, horizontal = 4.dp)
                    .width(42.dp)
            ) {
                Text(
                    text = date.format(DateTimeFormatter.ofPattern("EEE")),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) Color.White else Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = date.dayOfMonth.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                )
                
                if (dayClients.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    ClientCircles(clients = dayClients)
                } else {
                    Spacer(modifier = Modifier.height(16.dp)) 
                }
            }
        }
    }
}

@Composable
fun ClientCircles(clients: List<ClientSummaryItem>) {
    val maxCircles = 3
    val displayCount = if (clients.size > maxCircles) 2 else clients.size
    val showEllipsis = clients.size > maxCircles
    val circleSize = 16.dp
    val overlap = 6.dp

    Box(contentAlignment = Alignment.Center) {
        val totalWidth = (circleSize * (displayCount + if (showEllipsis) 1 else 0)) - (overlap * (displayCount + (if (showEllipsis) 1 else 0) - 1))
        
        Box(modifier = Modifier.width(totalWidth.coerceAtLeast(circleSize)).height(circleSize)) {
            for (i in 0 until displayCount) {
                val client = clients[i]
                val offset = (circleSize - overlap) * i
                
                Box(
                    modifier = Modifier
                        .padding(start = offset)
                        .size(circleSize)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.tertiaryContainer)
                        .border(1.dp, MaterialTheme.colorScheme.background, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (client.clientAvatarUrl != null) {
                        AsyncImage(
                            model = client.clientAvatarUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        val firstChar = client.clientFirstName.firstOrNull()?.uppercase() ?: "?"
                        Text(
                            text = firstChar,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }
            
            if (showEllipsis) {
                val offset = (circleSize - overlap) * displayCount
                Box(
                    modifier = Modifier
                        .padding(start = offset)
                        .size(circleSize)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondary)
                        .border(1.dp, MaterialTheme.colorScheme.background, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+${clients.size - displayCount}",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 7.sp,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }
            }
        }
    }
}

// --- Month View ---

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MonthViewPager(
    pagerState: PagerState,
    selectedDate: LocalDate,
    clientSummaries: List<ClientSummaryItem>,
    onDateSelected: (LocalDate) -> Unit
) {
    val initialPage = Int.MAX_VALUE / 2
    
     HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxWidth()
    ) { page ->
        val monthOffset = page - initialPage
        val today = LocalDate.now()
        val currentMonth = today.plusMonths(monthOffset.toLong())
        
        MonthCalendarView(
            currentMonth = currentMonth,
            selectedDate = selectedDate,
            clientSummaries = clientSummaries,
            onDateSelected = onDateSelected
        )
    }
}

@Composable
fun MonthCalendarView(
    currentMonth: LocalDate,
    selectedDate: LocalDate,
    clientSummaries: List<ClientSummaryItem>,
    onDateSelected: (LocalDate) -> Unit
) {
    val yearMonth = YearMonth.from(currentMonth)
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDayOfMonth = yearMonth.atDay(1)
    val startDayOfWeek = firstDayOfMonth.dayOfWeek.value // 1=Mon, 7=Sun
    val paddingDays = startDayOfWeek - 1 // Assuming Mon start

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        // Weekday headers
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            val weekDays = listOf("M", "T", "W", "T", "F", "S", "S")
            weekDays.forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))

        // Days Grid
        val totalSlots = daysInMonth + paddingDays
        val rows = (totalSlots / 7) + if (totalSlots % 7 != 0) 1 else 0
        
        for (row in 0 until rows) {
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                for (col in 0 until 7) {
                    val dayIndex = (row * 7) + col
                    val dayOfMonth = dayIndex - paddingDays + 1
                    
                    if (dayOfMonth in 1..daysInMonth) {
                        val date = yearMonth.atDay(dayOfMonth)
                        val isSelected = date.isEqual(selectedDate)
                        val isToday = date.isEqual(LocalDate.now())
                        
                        // Check for events/clients
                        val dayClients = clientSummaries.filter { 
                             try {
                                java.time.Instant.parse(it.date)
                                    .atZone(java.time.ZoneId.systemDefault())
                                    .toLocalDate()
                                    .isEqual(date)
                            } catch (e: Exception) {
                                false
                            }
                        }

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(CircleShape)
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                                .clickable { onDateSelected(date) }
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = dayOfMonth.toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isSelected) Color.White else if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                                )
                                if (dayClients.isNotEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .size(4.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) Color.White else MaterialTheme.colorScheme.tertiary)
                                    )
                                }
                            }
                        }
                    } else {
                         Spacer(modifier = Modifier.weight(1f).aspectRatio(1f))
                    }
                }
            }
        }
    }
}

// --- Day View ---
@Composable
fun DayCalendarView(
    date: LocalDate,
    events: List<CalendarEvent>,
    onEventClick: (CalendarEvent) -> Unit
) {
     if (events.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "No sessions for ${date.format(DateTimeFormatter.ofPattern("MMM d"))}",
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(events) { event ->
                EventItem(event, onClick = { onEventClick(event) })
            }
        }
    }
}


// --- Agenda View ---
@Composable
fun AgendaCalendarView(
    selectedDate: LocalDate,
    events: List<CalendarEvent>,
    onEventClick: (CalendarEvent) -> Unit
) {
    // For now, Agenda looks similar to DayView/EventsList but conceptually 
    // it could show a list of upcoming days. Currently constrained to selected date events by ViewModel structure.
    // Ideally Agenda would show a flat list of ALL upcoming events from today onwards.
    // Given the ViewModel mainly fetches by selectedDate or range, we might need adjustments to show multiple days.
    // For MVP reuse Day/Event List style but maybe with headers if we had multi-day data.
    
    // Just reusing DayCalendarView layout for simplicity as per requirements for 'view switching' 
    // on the 'calendar page'.
    DayCalendarView(
        date = selectedDate,
        events = events,
        onEventClick = onEventClick
    )
}
