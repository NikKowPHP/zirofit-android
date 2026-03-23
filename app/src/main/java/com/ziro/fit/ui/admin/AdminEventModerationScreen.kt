package com.ziro.fit.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.ziro.fit.model.PendingEvent
import com.ziro.fit.ui.theme.*
import com.ziro.fit.viewmodel.AdminEventModerationViewModel
import com.ziro.fit.viewmodel.error
import com.ziro.fit.viewmodel.isLoading
import com.ziro.fit.viewmodel.isSuccess
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminEventModerationScreen(
    onNavigateBack: () -> Unit,
    onEventReviewed: () -> Unit,
    viewModel: AdminEventModerationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val events by viewModel.events.collectAsState()
    val selectedEvent by viewModel.selectedEvent.collectAsState()
    val isActionLoading by viewModel.isActionLoading.collectAsState()
    val actionError by viewModel.actionError.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.loadPendingEvents()
    }

    LaunchedEffect(uiState) {
        if (uiState.isSuccess) {
            snackbarHostState.showSnackbar("Event reviewed successfully")
            viewModel.clearSuccess()
            onEventReviewed()
        }
    }

    LaunchedEffect(actionError) {
        actionError?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearActionError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Event Moderation", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = StrongBackground
                ),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = StrongBackground
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                uiState.isLoading && events.isEmpty() -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = StrongBlue
                    )
                }
                uiState.error != null && events.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.ErrorOutline,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = StrongTextSecondary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = uiState.error ?: "An error occurred",
                            color = StrongTextSecondary,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.loadPendingEvents() },
                            colors = ButtonDefaults.buttonColors(containerColor = StrongBlue)
                        ) {
                            Text("Retry")
                        }
                    }
                }
                events.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = StrongGreen
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "All caught up!",
                            color = StrongTextSecondary,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No pending events to review",
                            color = StrongTextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Text(
                                text = "${events.size} pending event${if (events.size != 1) "s" else ""}",
                                color = StrongTextSecondary,
                                fontSize = 14.sp
                            )
                        }
                        items(events) { event ->
                            PendingEventCard(
                                event = event,
                                onReview = { viewModel.selectEvent(event) }
                            )
                        }
                    }
                }
            }

            if (isActionLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = StrongBlue)
                }
            }
        }
    }

    selectedEvent?.let { event ->
        ReviewBottomSheet(
            event = event,
            isLoading = isActionLoading,
            onDismiss = viewModel::clearSelection,
            onApprove = { viewModel.approveEvent(event.id) },
            onReject = { reason -> viewModel.rejectEvent(event.id, reason) }
        )
    }
}

@Composable
fun PendingEventCard(
    event: PendingEvent,
    onReview: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = StrongSecondaryBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            if (event.trainer?.profilePhotoPath != null) {
                AsyncImage(
                    model = event.trainer.profilePhotoPath,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = event.title,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                event.trainer?.let { trainer ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = StrongBlue
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = trainer.name ?: "Unknown Trainer",
                            color = StrongTextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                val dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
                val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")
                val date = try {
                    ZonedDateTime.parse(event.startTime).toLocalDate()
                } catch (e: Exception) {
                    LocalDate.now()
                }
                val startTime = try {
                    ZonedDateTime.parse(event.startTime).toLocalTime()
                } catch (e: Exception) {
                    LocalTime.of(9, 0)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = StrongBlue
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = date.format(dateFormatter),
                        color = StrongTextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        Icons.Default.AccessTime,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = StrongBlue
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = startTime.format(timeFormatter),
                        color = StrongTextSecondary,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = event.price?.let { "$${"%.2f".format(it)}" } ?: "Free",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = StrongBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Button(
                        onClick = onReview,
                        colors = ButtonDefaults.buttonColors(containerColor = StrongBlue)
                    ) {
                        Text("Review")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewBottomSheet(
    event: PendingEvent,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onApprove: () -> Unit,
    onReject: (String) -> Unit
) {
    var showRejectForm by remember { mutableStateOf(false) }
    var rejectionReason by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = StrongSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Review Event",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = StrongSecondaryBackground
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = event.title,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    event.trainer?.let { trainer ->
                        Text(
                            text = "By: ${trainer.name ?: "Unknown"}",
                            color = StrongTextSecondary,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")
                    val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")
                    val date = try {
                        ZonedDateTime.parse(event.startTime).toLocalDate()
                    } catch (e: Exception) {
                        LocalDate.now()
                    }
                    val startTime = try {
                        ZonedDateTime.parse(event.startTime).toLocalTime()
                    } catch (e: Exception) {
                        LocalTime.of(9, 0)
                    }

                    Text(
                        text = "Date: ${date.format(dateFormatter)}",
                        color = StrongTextSecondary,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Time: ${startTime.format(timeFormatter)}",
                        color = StrongTextSecondary,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Price: ${event.price?.let { "$${"%.2f".format(it)}" } ?: "Free"}",
                        color = StrongTextSecondary,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (showRejectForm) {
                OutlinedTextField(
                    value = rejectionReason,
                    onValueChange = { rejectionReason = it },
                    label = { Text("Rejection Reason") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = StrongRed,
                        unfocusedBorderColor = StrongTextSecondary,
                        focusedLabelColor = StrongRed,
                        unfocusedLabelColor = StrongTextSecondary
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { showRejectForm = false },
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = { onReject(rejectionReason) },
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading && rejectionReason.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = StrongRed)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White
                            )
                        } else {
                            Text("Confirm Reject")
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onApprove,
                        modifier = Modifier.weight(1f).height(56.dp),
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = StrongGreen)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White
                            )
                        } else {
                            Icon(Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Approve")
                        }
                    }

                    Button(
                        onClick = { showRejectForm = true },
                        modifier = Modifier.weight(1f).height(56.dp),
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = StrongRed)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Reject")
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
