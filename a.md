<response>
  <reasoning>
    I will implement the event detail bottom sheet by updating `CalendarViewModel.kt` to manage the selected event state and modifying `CalendarScreen.kt` to display the `ModalBottomSheet` with event details. I will also make the event list items clickable. Finally, I will create a `TASKS.md` file to track this completed task.
  </reasoning>
  <modifications>
    <file path="app/src/main/java/com/ziro/fit/ui/calendar/CalendarViewModel.kt">
      <![CDATA[
package com.ziro.fit.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziro.fit.data.repository.CalendarRepository
import com.ziro.fit.model.CalendarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class CalendarUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val currentWeekOffset: Int = 0, // 0 = current week, -1 = previous week, +1 = next week
    val events: List<CalendarEvent> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val selectedEvent: CalendarEvent? = null
) {
    // Derived property: Filter events for the selected date on the UI side
    // This makes the UI snappy as switching days doesn't always need a network call
    val selectedDateEvents: List<CalendarEvent>
        get() = events.filter { 
            it.startTime.toLocalDate().isEqual(selectedDate) 
        }.sortedBy { it.startTime }
    
    // Get the start of the current week being displayed
    val currentWeekStart: LocalDate
        get() {
            val today = LocalDate.now()
            val startOfThisWeek = today.minusDays(today.dayOfWeek.value.toLong() - 1)
            return startOfThisWeek.plusWeeks(currentWeekOffset.toLong())
        }
}

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val repository: CalendarRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init {
        fetchEvents()
    }

    fun onDateSelected(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date) }
        // Optional: If date is outside current cache range, fetch more
        // For now, we fetch every time date changes or we could optimize
        // to only fetch if close to the edge of the buffer
        fetchEvents() 
    }
    
    fun onWeekChanged(weekOffset: Int) {
        _uiState.update { it.copy(currentWeekOffset = weekOffset) }
        // Optionally fetch events for the new week range
        fetchEvents()
    }
    
    fun navigateToNextWeek() {
        _uiState.update { it.copy(currentWeekOffset = it.currentWeekOffset + 1) }
        fetchEvents()
    }
    
    fun navigateToPreviousWeek() {
        _uiState.update { it.copy(currentWeekOffset = it.currentWeekOffset - 1) }
        fetchEvents()
    }

    fun onEventSelected(event: CalendarEvent) {
        _uiState.update { it.copy(selectedEvent = event) }
    }

    fun onEventDismissed() {
        _uiState.update { it.copy(selectedEvent = null) }
    }
    
    fun onStartSession(event: CalendarEvent) {
        // TODO: Navigate to live workout screen with event.id
        println("Starting session for ${event.title}")
    }

    fun onUpdateSession(event: CalendarEvent) {
        // TODO: Open edit dialog
        println("Updating session ${event.id}")
    }

    fun retry() {
        refresh()
    }

    fun refresh(isPullToRefresh: Boolean = false) {
        viewModelScope.launch {
            if (isPullToRefresh) {
                _uiState.update { it.copy(isRefreshing = true, error = null) }
            } else {
                _uiState.update { it.copy(isLoading = true, error = null) }
            }
            
            repository.getEvents(_uiState.value.selectedDate)
                .onSuccess { fetchedEvents ->
                    _uiState.update { it.copy(events = fetchedEvents, isLoading = false, isRefreshing = false) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(error = error.message, isLoading = false, isRefreshing = false) }
                }
        }
    }

    private fun fetchEvents() {
        refresh()
    }
}
      ]]>
    </file>
    <file path="app/src/main/java/com/ziro/fit/ui/calendar/CalendarScreen.kt">
      <![CDATA[
package com.ziro.fit.ui.calendar

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ziro.fit.model.CalendarEvent
import com.ziro.fit.model.EventType
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    
    // Sheet state
    val sheetState = rememberModalBottomSheetState()
    
    // Create a pager state with a very large page count to simulate infinite scrolling
    // Start at the middle to allow backward and forward navigation
    val initialPage = Int.MAX_VALUE / 2
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { Int.MAX_VALUE }
    )
    
    // Sync pager changes with view model
    LaunchedEffect(pagerState.currentPage) {
        val offset = pagerState.currentPage - initialPage
        viewModel.onWeekChanged(offset)
    }

    PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh = { viewModel.refresh(isPullToRefresh = true) },
        modifier = Modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            // 1. Header (Month Name)
            Text(
                text = state.currentWeekStart.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.onBackground
            )

            // 2. Horizontal Pager for Week View
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
                    selectedDate = state.selectedDate,
                    onDateSelected = viewModel::onDateSelected
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Events List
            if (state.isLoading && state.events.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (state.error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Error loading events", color = MaterialTheme.colorScheme.error)
                        Button(onClick = { viewModel.retry() }) {
                            Text("Retry")
                        }
                    }
                }
            } else {
                EventsList(
                    events = state.selectedDateEvents,
                    onEventClick = viewModel::onEventSelected
                )
            }
        }
    }

    // Bottom Sheet Implementation
    if (state.selectedEvent != null) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.onEventDismissed() },
            sheetState = sheetState
        ) {
            EventDetailsSheetContent(
                event = state.selectedEvent!!,
                onStartSession = { 
                    viewModel.onStartSession(it)
                    viewModel.onEventDismissed() 
                },
                onUpdateSession = { 
                    viewModel.onUpdateSession(it)
                    // Optional: keep sheet open or close
                }
            )
        }
    }
}

@Composable
fun WeekCalendarView(
    weekStartDate: LocalDate,
    selectedDate: LocalDate,
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
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onDateSelected(date) }
                    .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .padding(vertical = 8.dp, horizontal = 12.dp)
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
            }
        }
    }
}

@Composable
fun EventsList(
    events: List<CalendarEvent>,
    onEventClick: (CalendarEvent) -> Unit
) {
    if (events.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            Text(
                text = "No sessions today",
                color = Color.Gray,
                modifier = Modifier.padding(top = 32.dp)
            )
        }
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(events) { event ->
            EventItem(event, onClick = { onEventClick(event) })
        }
    }
}

@Composable
fun EventItem(
    event: CalendarEvent,
    onClick: () -> Unit
) {
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    
    // Status color mapping
    val statusColor = when(event.type) {
        EventType.session_in_progress -> Color(0xFFF59E0B) // Amber
        EventType.session_completed -> Color(0xFF10B981) // Green
        EventType.session_planned -> Color(0xFF6366F1) // Indigo
        EventType.booking -> Color(0xFFEC4899) // Pink
    }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            // Time Column
            Column(horizontalAlignment = Alignment.End, modifier = Modifier.width(50.dp)) {
                Text(
                    text = event.startTime.format(timeFormatter),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = event.endTime.format(timeFormatter),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.width(12.dp))
            
            // Vertical Divider
            Box(modifier = Modifier
                .width(4.dp)
                .height(40.dp)
                .clip(CircleShape)
                .background(statusColor)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                if (event.clientName != null) {
                    Text(
                        text = event.clientName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun EventDetailsSheetContent(
    event: CalendarEvent,
    onStartSession: (CalendarEvent) -> Unit,
    onUpdateSession: (CalendarEvent) -> Unit
) {
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d")

    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .padding(bottom = 48.dp) // Extra padding for navigation bar
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = event.startTime.format(dateFormatter),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Time Info
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.DateRange, 
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "${event.startTime.format(timeFormatter)} - ${event.endTime.format(timeFormatter)}",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Client Info
        if (event.clientName != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = event.clientName,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Notes
        if (!event.notes.isNullOrEmpty()) {
            Text(
                text = "Notes",
                style = MaterialTheme.typography.labelLarge,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = event.notes,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(24.dp))
        } else {
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Actions
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = { onUpdateSession(event) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Edit")
            }
            
            Button(
                onClick = { onStartSession(event) },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Start")
            }
        }
    }
}
      ]]>
    </file>
    <file path="TASKS.md">
      <![CDATA[
- [x] Implement event detail bottom sheet
      ]]>
    </file>
  </modifications>
</response>