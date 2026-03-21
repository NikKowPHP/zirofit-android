package com.ziro.fit.ui.discovery

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.ziro.fit.model.TrainerSummary
import com.ziro.fit.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class TrainerCluster(
    val latitude: Double,
    val longitude: Double,
    val trainers: List<TrainerSummary>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainerMapScreen(
    trainers: List<TrainerSummary>,
    onTrainerClick: (String) -> Unit
) {
    val clusters = remember(trainers) {
        trainers
            .mapNotNull { trainer ->
                trainer.profile?.locations?.firstOrNull()?.let { location ->
                    if (location.latitude != null && location.longitude != null) {
                        Triple(trainer, location.latitude, location.longitude)
                    } else null
                }
            }
            .groupBy { (_, lat, lng) ->
                "${lat.toBigDecimal().setScale(4, java.math.RoundingMode.HALF_UP)}_${lng.toBigDecimal().setScale(4, java.math.RoundingMode.HALF_UP)}"
            }
            .map { (_, items) ->
                val first = items.first()
                TrainerCluster(
                    latitude = first.second,
                    longitude = first.third,
                    trainers = items.map { it.first }
                )
            }
    }

    val defaultLocation = LatLng(51.5074, -0.1278)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 12f)
    }

    var selectedCluster by remember { mutableStateOf<TrainerCluster?>(null) }
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Cache for marker bitmaps
    val markerBitmapCache = remember { mutableStateMapOf<String, BitmapDescriptor>() }

    LaunchedEffect(selectedCluster) {
        showBottomSheet = selectedCluster != null
    }

    // Load marker bitmaps
    LaunchedEffect(clusters) {
        clusters.forEach { cluster ->
            val trainer = cluster.trainers.first()
            if (!markerBitmapCache.containsKey(trainer.id)) {
                try {
                    val bitmap = withContext(Dispatchers.IO) {
                        createMarkerBitmap(context, trainer, cluster.trainers.size)
                    }
                    bitmap?.let {
                        markerBitmapCache[trainer.id] = BitmapDescriptorFactory.fromBitmap(it)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        val mapProperties = remember {
            MapProperties(mapType = MapType.NORMAL, isMyLocationEnabled = false)
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
            clusters.forEach { cluster ->
                val firstTrainer = cluster.trainers.first()
                val markerBitmap = markerBitmapCache[firstTrainer.id]

                Marker(
                    state = MarkerState(position = LatLng(cluster.latitude, cluster.longitude)),
                    title = if (cluster.trainers.size > 1) {
                        "${cluster.trainers.size} Trainers"
                    } else {
                        firstTrainer.name
                    },
                    snippet = firstTrainer.profile?.locations?.firstOrNull()?.address,
                    icon = markerBitmap ?: BitmapDescriptorFactory.defaultMarker(
                        if (cluster.trainers.size > 1) BitmapDescriptorFactory.HUE_VIOLET else BitmapDescriptorFactory.HUE_AZURE
                    ),
                    onClick = {
                        selectedCluster = cluster
                        false
                    }
                )
            }
        }

        if (showBottomSheet && selectedCluster != null) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                    selectedCluster = null
                },
                sheetState = sheetState,
                containerColor = StrongSecondaryBackground,
                dragHandle = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(vertical = 12.dp)
                                .width(40.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color.Gray.copy(alpha = 0.4f))
                        )
                    }
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (selectedCluster!!.trainers.size > 1) {
                                "${selectedCluster!!.trainers.size} Specialists"
                            } else {
                                "Specialist"
                            },
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(
                            onClick = {
                                showBottomSheet = false
                                selectedCluster = null
                            }
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    selectedCluster!!.trainers.forEach { trainer ->
                        TrainerListItem(
                            trainer = trainer,
                            onClick = {
                                onTrainerClick(trainer.id)
                                showBottomSheet = false
                                selectedCluster = null
                            }
                        )
                    }
                }
            }
        }
    }
}

private suspend fun createMarkerBitmap(
    context: android.content.Context,
    trainer: TrainerSummary,
    count: Int?
): Bitmap? {
    return try {
        if (count != null && count > 1) {
            val combined = Bitmap.createBitmap(140, 150, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(combined)

            val bgPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#9C27B0")
                isAntiAlias = true
            }
            val rect = android.graphics.RectF(0f, 0f, 140f, 150f)
            val radius = 20f
            canvas.drawRoundRect(rect, radius, radius, bgPaint)

            val circlePaint = android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                isAntiAlias = true
            }
            canvas.drawCircle(70f, 75f, 30f, circlePaint)
            canvas.drawCircle(70f, 55f, 30f, circlePaint)
            canvas.drawCircle(70f, 35f, 30f, circlePaint)

            val badgePaint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#3B82F6")
                isAntiAlias = true
            }
            canvas.drawCircle(120f, 15f, 15f, badgePaint)

            val badgeTextPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 20f
                isFakeBoldText = true
                textAlign = android.graphics.Paint.Align.CENTER
                isAntiAlias = true
            }
            canvas.drawText("$count", 120f, 20f, badgeTextPaint)

            combined
        } else {
            val imageLoader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(trainer.profile?.profilePhotoPath)
                .size(120, 120)
                .allowHardware(false)
                .build()

            val result = imageLoader.execute(request)
            if (result is SuccessResult) {
                val drawable = result.drawable
                val baseBitmap = drawable.toBitmap(120, 120)

                val combined = Bitmap.createBitmap(140, 150, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(combined)

                canvas.drawBitmap(baseBitmap, 10f, 0f, null)

                val ratingBg = android.graphics.Paint().apply {
                    color = android.graphics.Color.parseColor("#1B2228")
                }
                canvas.drawRect(10f, 95f, 130f, 120f, ratingBg)

                val starPaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.parseColor("#FFD700")
                    textSize = 20f
                }
                canvas.drawText("★", 15f, 112f, starPaint)

                val textPaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.WHITE
                    textSize = 22f
                    isFakeBoldText = true
                }
                canvas.drawText(String.format("%.1f", trainer.profile?.averageRating ?: 5.0), 32f, 112f, textPaint)

                combined
            } else {
                null
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@Composable
private fun TrainerListItem(
    trainer: TrainerSummary,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = StrongBackground),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = trainer.profile?.profilePhotoPath,
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(StrongSecondaryBackground),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = trainer.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                trainer.profile?.certifications?.split(",")?.firstOrNull()?.let {
                    Text(
                        text = it.trim(),
                        color = StrongTextSecondary,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = String.format("%.1f", trainer.profile?.averageRating ?: 5.0),
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = StrongTextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = trainer.profile?.locations?.firstOrNull()?.address ?: "Online",
                        color = StrongTextSecondary,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
