package com.ziro.fit.ui.client

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
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
    onConfirm: (String, String, String?, String) -> Unit
) {
    var name by remember { mutableStateOf(client?.name ?: "") }
    var email by remember { mutableStateOf(client?.email ?: "") }
    var phone by remember { mutableStateOf(client?.phone ?: "") }
    var status by remember { mutableStateOf(client?.status ?: "active") }
    
    // Status Dropdown
    var expanded by remember { mutableStateOf(false) }
    val statuses = listOf("active", "inactive", "pending")

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
                    .fillMaxWidth(),
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
                             ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                     Surface(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable(onClick = { expanded = true }),
                        color = androidx.compose.ui.graphics.Color.Transparent
                    ) {}

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        statuses.forEach { selection ->
                            DropdownMenuItem(
                                text = { Text(selection.replaceFirstChar { it.uppercase() }) },
                                onClick = {
                                    status = selection
                                    expanded = false
                                }
                            )
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
                            onConfirm(name, email, phone.ifBlank { null }, status)
                        }
                    }) {
                        Text(if (client == null) "Create" else "Save")
                    }
                }
            }
        }
    }
}
