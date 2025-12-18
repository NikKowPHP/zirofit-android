package com.ziro.fit.ui.checkins

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ziro.fit.data.model.CheckInSubmissionRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckInSubmissionScreen(
    viewModel: ClientCheckInViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    // Form States
    var weight by remember { mutableStateOf("") }
    var waist by remember { mutableStateOf("") }
    var sleep by remember { mutableStateOf("") }
    var energy by remember { mutableStateOf(5f) }
    var stress by remember { mutableStateOf(5f) }
    var hunger by remember { mutableStateOf(5f) }
    var digestion by remember { mutableStateOf(5f) }
    var nutritionCompliance by remember { mutableStateOf("On Track") }
    var notes by remember { mutableStateOf("") }

    LaunchedEffect(uiState.submissionSuccess) {
        if (uiState.submissionSuccess) {
            onNavigateBack()
            viewModel.resetSubmissionState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Check-In") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (uiState.isSubmitting) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                if (uiState.error != null) {
                    Text(
                        text = "Error: ${uiState.error}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                Text("Physical Metrics", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Weight (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = waist,
                    onValueChange = { waist = it },
                    label = { Text("Waist (cm)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = sleep,
                    onValueChange = { sleep = it },
                    label = { Text("Sleep (hours)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))
                Text("Wellness Indicators (1-10)", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                
                SliderInput("Energy", energy) { energy = it }
                SliderInput("Stress", stress) { stress = it }
                SliderInput("Hunger", hunger) { hunger = it }
                SliderInput("Digestion", digestion) { digestion = it }

                Spacer(modifier = Modifier.height(24.dp))
                Text("Compliance & Notes", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                
                // Nutrition Compliance Selection (Simple Dropdown or Radio? Using Text Input for flexibility right now or predefined chips)
                // Let's stick with a text field or simple options.
                OutlinedTextField(
                    value = nutritionCompliance,
                    onValueChange = { nutritionCompliance = it },
                    label = { Text("Nutrition Compliance") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes / Questions") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = {
                        val request = CheckInSubmissionRequest(
                            weight = weight.toDoubleOrNull(),
                            waistCm = waist.toDoubleOrNull(),
                            sleepHours = sleep.toDoubleOrNull(),
                            energyLevel = energy.toDouble(),
                            stressLevel = stress.toDouble(),
                            hungerLevel = hunger.toDouble(),
                            digestionLevel = digestion.toDouble(),
                            nutritionCompliance = nutritionCompliance,
                            clientNotes = notes,
                            photos = null // Photo upload flow to be added separately or assumed handled
                        )
                        viewModel.submitCheckIn(request)
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Submit Check-In")
                }
            }
        }
    }
}

@Composable
fun SliderInput(label: String, value: Float, onValueChange: (Float) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text("${value.toInt()}/10", style = MaterialTheme.typography.bodyMedium)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 1f..10f,
            steps = 8
        )
    }
}
