package com.ziro.fit.ui.onboarding

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsGymnastics
import com.ziro.fit.ui.components.LocationPickerDialog
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.ziro.fit.util.Logger
import com.ziro.fit.viewmodel.OnboardingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleSelectionScreen(
    onOnboardingComplete: (String) -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val avatarUri = viewModel.avatarUri
     // Pre-populate from UserSessionManager
    // Replace lines 38-40 with:
val name = uiState.name
val location = uiState.location ?: ""
val bio = uiState.bio ?: ""
var showLocationPicker by remember { mutableStateOf(false) }

    Logger.d("state", "location in initial state ${viewModel.initialLocation}" )
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> viewModel.setAvatar(uri) }
    )

    LaunchedEffect(uiState.isComplete) {
        if (uiState.isComplete) {
            onOnboardingComplete(uiState.role)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Setup Profile") })
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("I am a...", style = MaterialTheme.typography.titleMedium)
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        RoleCard(
                            title = "Personal",
                            icon = Icons.Default.Person,
                            selected = uiState.role == "client",
                            onClick = { viewModel.updateRole("client") },
                            modifier = Modifier.weight(1f)
                        )
                        RoleCard(
                            title = "Trainer",
                            icon = Icons.Default.SportsGymnastics,
                            selected = uiState.role == "trainer",
                            onClick = { viewModel.updateRole("trainer") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (avatarUri != null) {
                                AsyncImage(
                                    model = avatarUri,
                                    contentDescription = "Avatar",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Upload Photo",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { newName -> 
        
        viewModel.updateName(newName)  
    },
                        label = { Text("Display Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

if (uiState.role == "trainer") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            OutlinedTextField(
                                value = location,
                                onValueChange = { newLocation ->
                                    viewModel.updateLocation(newLocation)
                                },
                                label = { Text("Location") },
                                placeholder = { Text("City, Country") },
                                modifier = Modifier.weight(1f)
                            )
                            FilledTonalIconButton(
                                onClick = { showLocationPicker = true },
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Map,
                                    contentDescription = "Pick from map"
                                )
                            }
                        }
                        Text(
                            text = "Or tap the map icon to select on the map",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                        )
                    }

                    OutlinedTextField(
                        value = bio,
                        onValueChange = { newBio -> 
        
        viewModel.updateBio(newBio)  // ← Save immediately to local storage
    },
                        label = { Text("Short Bio") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                    
                    if (uiState.error != null) {
                        Text(
                            text = uiState.error!!,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Button(
                        onClick = { viewModel.completeOnboarding(name, location.takeIf { it.isNotBlank() }, bio.takeIf { it.isNotBlank() }) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = name.isNotBlank() && (uiState.role != "trainer" || location.isNotBlank())
                    ) {
                        Text("Complete Setup")
                    }
                }
            }
        }
    }

    if (showLocationPicker) {
        LocationPickerDialog(
            onLocationSelected = { latitude, longitude, address ->
                val locationString = "$latitude,$longitude"
                viewModel.updateLocation(locationString)
                showLocationPicker = false
            },
            onDismiss = { showLocationPicker = false }
        )
    }
}

@Composable
fun RoleCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        ),
        border = if (selected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, fontWeight = FontWeight.Bold)
        }
    }
}
