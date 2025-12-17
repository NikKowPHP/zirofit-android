package com.ziro.fit.ui.discovery

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import androidx.compose.runtime.remember
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapType
import com.ziro.fit.model.TrainerSummary

@Composable
fun TrainerMapScreen(
    trainers: List<TrainerSummary>,
    onTrainerClick: (String) -> Unit
) {
    val london = LatLng(51.5074, -0.1278) // Default to London for now
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(london, 10f)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        val mapProperties = remember {
            MapProperties(
                mapType = MapType.NORMAL,
                isMyLocationEnabled = false
            )
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
            uiSettings = mapUiSettings
        ) {
            trainers.forEach { trainer ->
                key(trainer.id) {
                    val location = trainer.profile?.locations?.firstOrNull()
                    if (location?.latitude != null && location.longitude != null) {
                        val markerState = rememberMarkerState(position = LatLng(location.latitude, location.longitude))
                        
                        // Build snippet with price and rating
                        val snippetParts = mutableListOf<String>()
                        
                        // Add minimum price if available
                        trainer.profile?.services?.mapNotNull { service ->
                            service.price?.toDoubleOrNull()
                        }?.minOrNull()?.let { minPrice ->
                            val currency = trainer.profile.services.firstOrNull()?.currency ?: "£"
                            snippetParts.add("From $currency$minPrice")
                        }
                        
                        // Add rating if available
                        trainer.profile?.averageRating?.let { rating ->
                            snippetParts.add("⭐ ${String.format("%.1f", rating)}")
                        }
                        
                        Marker(
                            state = markerState,
                            title = trainer.name,
                            snippet = if (snippetParts.isNotEmpty()) {
                                snippetParts.joinToString(" • ")
                            } else {
                                location.address
                            },
                            onClick = {
                                onTrainerClick(trainer.id)
                                false // Allow default info window
                            },
                            onInfoWindowClick = {
                                onTrainerClick(trainer.id)
                            }
                        )
                    }
                }
            }
        }
        
        if (trainers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                 // Optional: Show empty state or simplified view
            }
        }
    }
}
