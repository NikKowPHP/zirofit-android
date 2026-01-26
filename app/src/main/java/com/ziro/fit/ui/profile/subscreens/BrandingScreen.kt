package com.ziro.fit.ui.profile.subscreens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ziro.fit.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrandingScreen(
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var selectedBannerUri by remember { mutableStateOf<Uri?>(null) }
    var selectedProfileUri by remember { mutableStateOf<Uri?>(null) }

    val bannerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedBannerUri = uri
        }
    }

    val profileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedProfileUri = uri
        }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchBranding()
    }

    // Reset local selection when loading finishes successfully
    LaunchedEffect(uiState.branding, uiState.isLoading) {
       if (!uiState.isLoading && uiState.error == null) {
           // Only clear if we actually have data, to avoid clearing on initial empty state if that happens?
           // Actually, standard behavior: if success, data is current, so local edits are "saved".
           // But if we just loaded the screen, we don't want to clear if we haven't selected anything yet (it's already null).
           // If we selected something and then saved, isLoading goes true -> false.
           if (selectedBannerUri != null || selectedProfileUri != null) {
               selectedBannerUri = null
               selectedProfileUri = null
           }
       }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Branding") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            viewModel.updateBranding(selectedBannerUri, selectedProfileUri, context)
                        },
                        enabled = !uiState.isLoading && (selectedBannerUri != null || selectedProfileUri != null)
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Save")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Banner Section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { bannerLauncher.launch("image/*") }
                ) {
                    val bannerModel = selectedBannerUri ?: uiState.branding?.bannerImageUrl
                    if (bannerModel != null) {
                        AsyncImage(
                            model = bannerModel,
                            contentDescription = "Banner Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        // Overlay to indicate edit
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.2f))
                        )
                    } else {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Image, 
                                contentDescription = null, 
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text("Tap to add banner", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit Banner",
                        tint = Color.White,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Profile Image Section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { profileLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        val profileModel = selectedProfileUri ?: uiState.branding?.profileImageUrl
                        if (profileModel != null) {
                            AsyncImage(
                                model = profileModel,
                                contentDescription = "Profile Image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                Icons.Default.Image,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Edit icon badge
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp)
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit Profile Image",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))

                // Other Branding Info
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Branding Details",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Primary Color",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                uiState.branding?.primaryColor ?: "Not set",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
                
                if (uiState.error != null) {
                    Text(
                        text = "Error: ${uiState.error}", 
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}
