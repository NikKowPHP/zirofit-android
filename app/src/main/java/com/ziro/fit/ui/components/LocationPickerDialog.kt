package com.ziro.fit.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun LocationPickerDialog(
    initialLatitude: Double? = null,
    initialLongitude: Double? = null,
    onLocationSelected: (latitude: Double, longitude: Double, address: String) -> Unit,
    onDismiss: () -> Unit
) {
    val defaultLocation = LatLng(
        initialLatitude ?: 51.5074,
        initialLongitude ?: -0.1278
    )

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 12f)
    }

    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    var selectedAddress by remember { mutableStateOf("") }

    fun reverseGeocode(latLng: LatLng) {
        selectedAddress = "${latLng.latitude}, ${latLng.longitude}"
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Select Location",
                        style = MaterialTheme.typography.titleLarge
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Box(modifier = Modifier.weight(1f)) {
                    val mapProperties = remember {
                        MapProperties(mapType = MapType.NORMAL)
                    }
                    val mapUiSettings = remember {
                        MapUiSettings(
                            zoomControlsEnabled = true,
                            myLocationButtonEnabled = false
                        )
                    }

                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        properties = mapProperties,
                        uiSettings = mapUiSettings,
                        onMapClick = { latLng ->
                            selectedLocation = latLng
                            reverseGeocode(latLng)
                        }
                    ) {
                        selectedLocation?.let { location ->
                            Marker(
                                state = MarkerState(position = location),
                                title = "Selected Location"
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    if (selectedLocation != null) {
                        Text(
                            text = "Selected: $selectedAddress",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    } else {
                        Text(
                            text = "Tap on the map to select your location",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = {
                                selectedLocation?.let { location ->
                                    onLocationSelected(
                                        location.latitude,
                                        location.longitude,
                                        selectedAddress
                                    )
                                }
                            },
                            modifier = Modifier.weight(1f),
                            enabled = selectedLocation != null
                        ) {
                            Text("Confirm")
                        }
                    }
                }
            }
        }
    }
}
