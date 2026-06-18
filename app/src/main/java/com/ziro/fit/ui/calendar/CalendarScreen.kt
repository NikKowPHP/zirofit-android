package com.ziro.fit.ui.calendar

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.RadioButtonUnchecked
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.ziro.fit.model.CalendarEvent
import com.ziro.fit.model.EventType
import com.ziro.fit.ui.theme.ZiroAccent
import com.ziro.fit.ui.theme.StrongTextSecondary
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import com.ziro.fit.viewmodel.WorkoutViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel = hiltViewModel(),
    workoutViewModel: WorkoutViewModel = hiltViewModel(),
    onNavigateToLiveWorkout: () -> Unit = {},
    onNavigateToCreateSession: (String) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val workoutState by workoutViewModel.uiState.collectAsState()
    
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

    // Solve FAB and mini player overlap: move FAB up if workout mini player is active
    val isMiniPlayerActive = workoutState.activeSession != null
    val fabBottomPadding = if (isMiniPlayerActive) 100.dp else 16.dp

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    onNavigateToCreateSession(state.selectedDate.toString())
                },
                shape = RoundedCornerShape(16.dp), // Premium rounded square
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = ZiroAccent,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 6.dp,
                    pressedElevation = 2.dp
                ),
                modifier = Modifier
                    .padding(bottom = fabBottomPadding, end = 8.dp) // Dynamic bottom padding
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f), RoundedCornerShape(16.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create Session",
                    modifier = Modifier.size(24.dp),
                    tint = ZiroAccent
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { viewModel.refresh(isPullToRefresh = true) },
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        ) {
            Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
                // Header & View Selector
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(), 
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (state.viewMode == CalendarViewMode.MONTH) 
                                state.currentMonthStart.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
                            else 
                                state.selectedDate.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        
                        // View Selection using Dropdown
                        Box {
                            var expanded by remember { mutableStateOf(false) }
                            
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(100.dp))
                                    .clickable { expanded = true }
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = state.viewMode.name.first() + state.viewMode.name.substring(1).lowercase(),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = ZiroAccent
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Select View",
                                    tint = ZiroAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                            ) {
                                CalendarViewMode.values().forEach { mode ->
                                    DropdownMenuItem(
                                        text = { 
                                            Text(
                                                text = mode.name.first() + mode.name.substring(1).lowercase(),
                                                fontWeight = if (state.viewMode == mode) FontWeight.Bold else FontWeight.Normal,
                                                color = if (state.viewMode == mode) ZiroAccent else MaterialTheme.colorScheme.onSurface
                                            ) 
                                        },
                                        onClick = {
                                            viewModel.onViewModeChanged(mode)
                                            expanded = false
                                        },
                                        leadingIcon = {
                                            if (state.viewMode == mode) {
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = "Selected",
                                                    tint = ZiroAccent
                                                )
                                            } else {
                                                Icon(
                                                    imageVector = Icons.Default.RadioButtonUnchecked,
                                                    contentDescription = null,
                                                    tint = Color.Transparent
                                                )
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Main Content
                when (state.viewMode) {
                    CalendarViewMode.WEEK -> {
                        Column(modifier = Modifier.fillMaxSize()) {
                            WeekViewPager(
                                pagerState = pagerState,
                                selectedDate = state.selectedDate,
                                clientSummaries = state.clientSummaries,
                                onDateSelected = viewModel::onDateSelected
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f),
                                thickness = 1.dp
                            )
                            Box(modifier = Modifier.weight(1f)) {
                                EventsListWithLoading(state, viewModel)
                            }
                        }
                    }
                    CalendarViewMode.MONTH -> {
                        val monthPagerState = rememberPagerState(
                             initialPage = Int.MAX_VALUE / 2,
                             pageCount = { Int.MAX_VALUE }
                        )
                        LaunchedEffect(monthPagerState.currentPage) {
                             val offset = monthPagerState.currentPage - (Int.MAX_VALUE / 2)
                             viewModel.onMonthChanged(offset)
                        }

                        Column(modifier = Modifier.fillMaxSize()) {
                            MonthViewPager(
                                pagerState = monthPagerState,
                                selectedDate = state.selectedDate,
                                clientSummaries = state.clientSummaries,
                                onDateSelected = viewModel::onDateSelected
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f),
                                thickness = 1.dp
                            )
                            Box(modifier = Modifier.weight(1f)) {
                                EventsListWithLoading(state, viewModel)
                            }
                        }
                    }
                    CalendarViewMode.DAY -> {
                        DayCalendarView(
                            date = state.selectedDate,
                            events = state.selectedDateEvents,
                            onEventClick = viewModel::onEventSelected
                        )
                    }
                    CalendarViewMode.AGENDA -> {
                         AgendaCalendarView(
                            selectedDate = state.selectedDate,
                            events = state.selectedDateEvents,
                            onEventClick = viewModel::onEventSelected
                        )
                    }
                }
            }
        }

        // Bottom Sheet Implementation
        if (state.selectedEvent != null) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.onEventDismissed() },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                EventDetailsSheetContent(
                    event = state.selectedEvent!!,
                    onStartSession = { event -> 
                        workoutViewModel.startWorkout(
                            clientId = null,
                            templateId = null,
                            plannedSessionId = event.id,
                            onSuccess = { onNavigateToLiveWorkout() }
                        )
                        viewModel.onEventDismissed() 
                    },
                    onUpdateSession = { 
                        viewModel.onUpdateSession(it)
                    }
                )
            }
        }
    }
}

@Composable
fun EventsListWithLoading(state: CalendarUiState, viewModel: CalendarViewModel) {
    if (state.isLoading && state.events.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = ZiroAccent)
        }
    } else if (state.error != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Error loading events", color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
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

@Composable
fun EventsList(
    events: List<CalendarEvent>,
    onEventClick: (CalendarEvent) -> Unit
) {
    if (events.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "No sessions today",
                color = StrongTextSecondary,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
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
      