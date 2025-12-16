package com.ziro.fit.ui.client

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.ziro.fit.model.Client


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientFormDialog(
    client: Client? = null,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String?, String, Int?, Int?) -> Unit
) {
    var name by remember { mutableStateOf(client?.name ?: "") }
    var email by remember { mutableStateOf(client?.email ?: "") }
    var phone by remember { mutableStateOf(client?.phone ?: "") }
    var status by remember { mutableStateOf(client?.status ?: "active") }
    var checkInDay by remember { mutableStateOf(client?.checkInDay) }
    var checkInHour by remember { mutableStateOf(client?.checkInHour) }
    
    // Status Dropdown
    var statusExpanded by remember { mutableStateOf(false) }
    val statuses = listOf("active", "inactive", "pending")
    
    // Day Dropdown
    var dayExpanded by remember { mutableStateOf(false) }
    val days = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
    
    // Hour Dropdown
    var hourExpanded by remember { mutableStateOf(false) }
    val hours = (0..23).map { "$it:00" }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if (client == null) "Add Client" else "Edit Client",
                    style = MaterialTheme.typography.titleLarge
                )
                
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone (Optional)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )

                // Status Dropdown
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = status.replaceFirstChar { it.uppercase() },
                        onValueChange = {},
                        label = { Text("Status") },
                        readOnly = true,
                        trailingIcon = {
                             ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded)
                        },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                     Surface(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable(onClick = { statusExpanded = true }),
                        color = androidx.compose.ui.graphics.Color.Transparent
                    ) {}

                    DropdownMenu(
                        expanded = statusExpanded,
                        onDismissRequest = { statusExpanded = false }
                    ) {
                        statuses.forEach { selection ->
                            DropdownMenuItem(
                                text = { Text(selection.replaceFirstChar { it.uppercase() }) },
                                onClick = {
                                    status = selection
                                    statusExpanded = false
                                }
                            )
                        }
                    }
                }
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text("Check-in Schedule", style = MaterialTheme.typography.titleMedium)
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Day Dropdown
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = checkInDay?.let { days.getOrNull(it) } ?: "None",
                            onValueChange = {},
                            label = { Text("Day") },
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = dayExpanded)
                            },
                             colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Surface(
                            modifier = Modifier.matchParentSize().clickable { dayExpanded = true },
                            color = androidx.compose.ui.graphics.Color.Transparent
                        ) {}
                        DropdownMenu(expanded = dayExpanded, onDismissRequest = { dayExpanded = false }) {
                            DropdownMenuItem(
                                text = { Text("None") },
                                onClick = { checkInDay = null; dayExpanded = false }
                            )
                            days.forEachIndexed { index, day ->
                                DropdownMenuItem(
                                    text = { Text(day) },
                                    onClick = { checkInDay = index; dayExpanded = false }
                                )
                            }
                        }
                    }
                    
                    // Hour Dropdown
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = checkInHour?.let { "$it:00" } ?: "None",
                            onValueChange = {},
                            label = { Text("Hour") },
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = hourExpanded)
                            },
                             colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Surface(
                            modifier = Modifier.matchParentSize().clickable { hourExpanded = true },
                            color = androidx.compose.ui.graphics.Color.Transparent
                        ) {}
                        DropdownMenu(expanded = hourExpanded, onDismissRequest = { hourExpanded = false }) {
                            DropdownMenuItem(
                                text = { Text("None") },
                                onClick = { checkInHour = null; hourExpanded = false }
                            )
                            hours.forEachIndexed { index, hour ->
                                DropdownMenuItem(
                                    text = { Text(hour) },
                                    onClick = { checkInHour = index; hourExpanded = false }
                                )
                            }
                        }
                    }
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Button(onClick = { 
                        if (name.isNotBlank() && email.isNotBlank()) {
                            onConfirm(name, email, phone.ifBlank { null }, status, checkInDay, checkInHour)
                        }
                    }) {
                        Text(if (client == null) "Create" else "Save")
                    }
                }
            }
        }
    }
}
