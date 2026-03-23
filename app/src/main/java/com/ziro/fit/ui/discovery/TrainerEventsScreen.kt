package com.ziro.fit.ui.discovery

import androidx.compose.foundation.clickable
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
import com.ziro.fit.model.ExploreEvent
import com.ziro.fit.ui.theme.*
import com.ziro.fit.viewmodel.TrainerEventsViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainerEventsScreen(
    onBack: () -> Unit,
    viewModel: TrainerEventsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            viewModel.resetSaveSuccess()
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Events", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = StrongBackground
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = viewModel::showCreateForm,
                containerColor = StrongBlue
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Event", tint = Color.White)
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = StrongBackground
    ) { padding ->
        if (uiState.isLoading && uiState.events.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = StrongBlue)
            }
        } else if (uiState.events.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Event,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = StrongTextSecondary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No events yet",
                        color = StrongTextSecondary,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tap + to create your first event",
                        color = StrongTextSecondary,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.events) { event ->
                    TrainerEventItem(
                        event = event,
                        onEdit = { viewModel.showEditForm(event) },
                        onDelete = { viewModel.showDeleteConfirmation(event.id) }
                    )
                }
            }
        }
    }

    if (uiState.showForm) {
        EventFormBottomSheet(
            uiState = uiState,
            onDismiss = viewModel::hideForm,
            onTitleChange = viewModel::setFormTitle,
            onDescriptionChange = viewModel::setFormDescription,
            onLocationChange = viewModel::setFormLocation,
            onAddressChange = viewModel::setFormAddress,
            onDateChange = viewModel::setFormDate,
            onStartTimeChange = viewModel::setFormStartTime,
            onEndTimeChange = viewModel::setFormEndTime,
            onPriceChange = viewModel::setFormPrice,
            onCapacityChange = viewModel::setFormCapacity,
            onSave = viewModel::saveEvent
        )
    }

    if (uiState.deleteConfirmEventId != null) {
        AlertDialog(
            onDismissRequest = viewModel::hideDeleteConfirmation,
            title = { Text("Delete Event", color = StrongTextPrimary) },
            text = { Text("Are you sure you want to delete this event? This action cannot be undone.", color = StrongTextSecondary) },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.deleteEvent(uiState.deleteConfirmEventId!!) }
                ) {
                    Text("Delete", color = StrongRed)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::hideDeleteConfirmation) {
                    Text("Cancel", color = StrongTextSecondary)
                }
            },
            containerColor = StrongSurface
        )
    }
}

@Composable
fun TrainerEventItem(
    event: ExploreEvent,
    onEdit: () -> Unit,
    onDelete: () -> Unit
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
            Box {
                AsyncImage(
                    model = event.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                    contentScale = ContentScale.Crop
                )

                if (event.status != null) {
                    Surface(
                        modifier = Modifier
                            .padding(12.dp)
                            .align(Alignment.TopStart),
                        color = when {
                            event.isPending -> Color(0xFFF59E0B)
                            event.isApproved -> Color(0xFF10B981)
                            event.isRejected -> Color(0xFFEF4444)
                            else -> Color.Gray
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = when {
                                event.isPending -> "Pending Review"
                                event.isApproved -> "Approved"
                                event.isRejected -> "Rejected"
                                else -> event.status ?: ""
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopEnd),
                    color = Color.Black.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = event.priceDisplay ?: "Free",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = StrongBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = event.title,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                if (event.isRejected && !event.rejectionReason.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = event.rejectionReason,
                        color = Color(0xFFEF4444),
                        fontSize = 12.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = event.locationName,
                    color = StrongTextSecondary,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                val dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
                val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")
                val date = try {
                    java.time.ZonedDateTime.parse(event.startTime).toLocalDate()
                } catch (e: Exception) {
                    LocalDate.now()
                }
                val startTime = try {
                    java.time.ZonedDateTime.parse(event.startTime).toLocalTime()
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
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "${event.enrolledCount ?: 0}/${event.capacity ?: "∞"} enrolled",
                        color = if (event.isNearCapacity == true) StrongRed else StrongTextSecondary,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onEdit) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            modifier = Modifier.size(18.dp),
                            tint = StrongBlue
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit", color = StrongBlue)
                    }
                    TextButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            modifier = Modifier.size(18.dp),
                            tint = StrongRed
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Delete", color = StrongRed)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventFormBottomSheet(
    uiState: com.ziro.fit.viewmodel.TrainerEventsUiState,
    onDismiss: () -> Unit,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onDateChange: (LocalDate) -> Unit,
    onStartTimeChange: (LocalTime) -> Unit,
    onEndTimeChange: (LocalTime) -> Unit,
    onPriceChange: (String) -> Unit,
    onCapacityChange: (String) -> Unit,
    onSave: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = StrongSurface
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (uiState.editingEvent != null) "Edit Event" else "Create Event",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            item {
                OutlinedTextField(
                    value = uiState.formTitle,
                    onValueChange = onTitleChange,
                    label = { Text("Title *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = StrongBlue,
                        unfocusedBorderColor = StrongTextSecondary,
                        focusedLabelColor = StrongBlue,
                        unfocusedLabelColor = StrongTextSecondary
                    )
                )
            }

            item {
                OutlinedTextField(
                    value = uiState.formDescription,
                    onValueChange = onDescriptionChange,
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = StrongBlue,
                        unfocusedBorderColor = StrongTextSecondary,
                        focusedLabelColor = StrongBlue,
                        unfocusedLabelColor = StrongTextSecondary
                    )
                )
            }

            item {
                OutlinedTextField(
                    value = uiState.formLocation,
                    onValueChange = onLocationChange,
                    label = { Text("Location Name *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = StrongBlue,
                        unfocusedBorderColor = StrongTextSecondary,
                        focusedLabelColor = StrongBlue,
                        unfocusedLabelColor = StrongTextSecondary
                    )
                )
            }

            item {
                OutlinedTextField(
                    value = uiState.formAddress,
                    onValueChange = onAddressChange,
                    label = { Text("Address") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = StrongBlue,
                        unfocusedBorderColor = StrongTextSecondary,
                        focusedLabelColor = StrongBlue,
                        unfocusedLabelColor = StrongTextSecondary
                    )
                )
            }

            item {
                DateSelector(
                    selectedDate = uiState.formDate,
                    onDateSelected = onDateChange
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TimeSelector(
                        label = "Start",
                        time = uiState.formStartTime,
                        onTimeSelected = onStartTimeChange,
                        modifier = Modifier.weight(1f)
                    )
                    TimeSelector(
                        label = "End",
                        time = uiState.formEndTime,
                        onTimeSelected = onEndTimeChange,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.formPrice,
                        onValueChange = onPriceChange,
                        label = { Text("Price") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        placeholder = { Text("0 = Free") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = StrongBlue,
                            unfocusedBorderColor = StrongTextSecondary,
                            focusedLabelColor = StrongBlue,
                            unfocusedLabelColor = StrongTextSecondary
                        )
                    )
                    OutlinedTextField(
                        value = uiState.formCapacity,
                        onValueChange = onCapacityChange,
                        label = { Text("Capacity") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        placeholder = { Text("Unlimited") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = StrongBlue,
                            unfocusedBorderColor = StrongTextSecondary,
                            focusedLabelColor = StrongBlue,
                            unfocusedLabelColor = StrongTextSecondary
                        )
                    )
                }
            }

            item {
                Button(
                    onClick = onSave,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    enabled = !uiState.isSaving,
                    colors = ButtonDefaults.buttonColors(containerColor = StrongBlue)
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    } else {
                        Text(
                            text = if (uiState.editingEvent != null) "Update Event" else "Create Event",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateSelector(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")

    OutlinedTextField(
        value = selectedDate.format(dateFormatter),
        onValueChange = {},
        readOnly = true,
        label = { Text("Date") },
        modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true },
        trailingIcon = {
            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.White)
        },
        enabled = false,
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = Color.White,
            disabledBorderColor = StrongTextSecondary,
            disabledTrailingIconColor = Color.White,
            disabledLabelColor = StrongTextSecondary
        )
    )

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val instant = java.time.Instant.ofEpochMilli(millis)
                        val date = instant.atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                        onDateSelected(date)
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeSelector(
    label: String,
    time: LocalTime,
    onTimeSelected: (LocalTime) -> Unit,
    modifier: Modifier = Modifier
) {
    var showTimePicker by remember { mutableStateOf(false) }
    val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")

    OutlinedTextField(
        value = time.format(timeFormatter),
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        modifier = modifier.clickable { showTimePicker = true },
        trailingIcon = {
            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.White)
        },
        enabled = false,
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = Color.White,
            disabledBorderColor = StrongTextSecondary,
            disabledTrailingIconColor = Color.White,
            disabledLabelColor = StrongTextSecondary
        )
    )

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = time.hour,
            initialMinute = time.minute,
            is24Hour = false
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    onTimeSelected(LocalTime.of(timePickerState.hour, timePickerState.minute))
                    showTimePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel")
                }
            },
            text = { TimePicker(state = timePickerState) }
        )
    }
}
