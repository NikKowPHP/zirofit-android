package com.ziro.fit.ui.program

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GenerateProgramDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit,
    isLoading: Boolean
) {
    var isMonth by remember { mutableStateOf(true) }
    var focus by remember { mutableStateOf("") }
    
    if (isLoading) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Generating Program") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Analyzing client history...")
                }
            },
            confirmButton = {}
        )
    } else {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Generate AI Program") },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Duration", style = MaterialTheme.typography.labelLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = !isMonth,
                            onClick = { isMonth = false },
                            label = { Text("1 Week") },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = isMonth,
                            onClick = { isMonth = true },
                            label = { Text("1 Month") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = focus,
                        onValueChange = { focus = it },
                        label = { Text("Focus / Goal") },
                        placeholder = { Text("e.g. Leg hypertrophy, Improve squat") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val duration = if (isMonth) "month" else "week"
                        onConfirm(duration, focus)
                    },
                    enabled = focus.isNotBlank()
                ) {
                    Text("Generate")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }
}
