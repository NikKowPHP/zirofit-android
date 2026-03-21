package com.ziro.fit.ui.profile.subscreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ziro.fit.viewmodel.ProfileViewModel
import com.ziro.fit.model.DaySchedule
import com.ziro.fit.model.WorkingHours
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.TimePicker
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvailabilityScreen(
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var workingHours by remember { mutableStateOf<WorkingHours?>(null) }

    LaunchedEffect(uiState.availability) {
        // Load initial data into the editor from backend availability data
        if (uiState.availability != null && workingHours == null) {
            val scheduleMap = uiState.availability?.schedule ?: emptyMap()
            val names = listOf("Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday")
            val days = names.map { name ->
                val slots = scheduleMap[name]
                val isOpen = !slots.isNullOrEmpty()
                var start = "09:00"
                var end = "17:00"
                if (!slots.isNullOrEmpty()) {
                    val first = slots[0]
                    val parts = first.split("-")
                    if (parts.size >= 2) {
                        val s = parts[0]
                        val e = parts[1]
                        if (s.matches(Regex("\\d{2}:\\d{2}"))) start = s
                        if (e.matches(Regex("\\d{2}:\\d{2}"))) end = e
                    }
                }
                DaySchedule(day = name, isOpen = isOpen, startTime = start, endTime = end)
            }
            workingHours = WorkingHours(days = days)
        }
        if (uiState.availability == null && workingHours == null) {
            viewModel.fetchAvailability()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Availability") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        if (workingHours != null) {
                            viewModel.saveWorkingHours(workingHours!!)
                        }
                    }) {
                        Icon(Icons.Default.Save, contentDescription = "Save")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(androidx.compose.ui.Alignment.Center))
            } else {
                if (workingHours != null) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(workingHours!!.days) { day ->
                            DayRow(day = day, onDayChanged = { updated ->
                                val cur = workingHours
                                if (cur != null) {
                                    val newDays = cur.days.map { if (it.day == updated.day) updated else it }
                                    workingHours = WorkingHours(days = newDays)
                                }
                            })
                        }
                    }
                } else {
                    Column {
                        uiState.availability?.let { data ->
                            Text("TimeZone: ${data.timeZone ?: "N/A"}", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Schedule:", style = MaterialTheme.typography.titleMedium)
                            data.schedule?.forEach { (day, slots) ->
                                Text("$day: ${slots.joinToString(", ")}")
                            }
                        } ?: run {
                            Text("No data available")
                        }
                    }
                }
                if (uiState.error != null) {
                    Text("Error: ${uiState.error}", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DayRow(day: DaySchedule, onDayChanged: (DaySchedule) -> Unit) {
    var startPickerOpen by remember { mutableStateOf(false) }
    var endPickerOpen by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = day.day, modifier = Modifier.weight(1f))
        Text(text = if (day.isOpen) "Open" else "Closed", modifier = Modifier.padding(end = 8.dp))
        Switch(checked = day.isOpen, onCheckedChange = { v ->
            onDayChanged(day.copy(isOpen = v))
        })
        if (day.isOpen) {
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedButton(onClick = { startPickerOpen = true }) {
                Text(day.startTime ?: "")
            }
            if (startPickerOpen) {
                val stHour = day.startTime?.substring(0, 2)?.toIntOrNull() ?: 9
                val stMin = day.startTime?.substring(3, 5)?.toIntOrNull() ?: 0
                val timePickerState = rememberTimePickerState(initialHour = stHour, initialMinute = stMin, is24Hour = true)
                AlertDialog(
                    onDismissRequest = { startPickerOpen = false },
                    confirmButton = {
                        TextButton(onClick = {
                            val newTime = LocalTime.of(timePickerState.hour, timePickerState.minute).format(DateTimeFormatter.ofPattern("HH:mm"))
                            onDayChanged(day.copy(startTime = newTime))
                            startPickerOpen = false
                        }) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { startPickerOpen = false }) {
                            Text("Cancel")
                        }
                    },
                    text = {
                        TimePicker(state = timePickerState)
                    }
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedButton(onClick = { endPickerOpen = true }) {
                Text(day.endTime ?: "")
            }
            if (endPickerOpen) {
                val edHour = day.endTime?.substring(0, 2)?.toIntOrNull() ?: 17
                val edMin = day.endTime?.substring(3, 5)?.toIntOrNull() ?: 0
                val timePickerState2 = rememberTimePickerState(initialHour = edHour, initialMinute = edMin, is24Hour = true)
                AlertDialog(
                    onDismissRequest = { endPickerOpen = false },
                    confirmButton = {
                        TextButton(onClick = {
                            val newTime = LocalTime.of(timePickerState2.hour, timePickerState2.minute).format(DateTimeFormatter.ofPattern("HH:mm"))
                            onDayChanged(day.copy(endTime = newTime))
                            endPickerOpen = false
                        }) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { endPickerOpen = false }) {
                            Text("Cancel")
                        }
                    },
                    text = {
                        TimePicker(state = timePickerState2)
                    }
                )
            }
        }
    }
}
