package com.ziro.fit.ui.client

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.ziro.fit.model.*
import com.ziro.fit.viewmodel.ClientDetailsViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientDetailsScreen(
    clientId: String,
    onNavigateBack: () -> Unit,
    onNavigateToMeasurements: (String) -> Unit,
    onNavigateToAssessments: (String) -> Unit,
    onNavigateToPhotos: (String) -> Unit,
    onNavigateToSessions: (String) -> Unit,
    viewModel: ClientDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(clientId) {
        viewModel.loadClientProfile(clientId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.client?.name ?: "Client Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.error != null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = uiState.error ?: "Unknown error",
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.refresh(clientId) }) {
                            Text("Retry")
                        }
                    }
                }
                uiState.client != null -> {
                    ClientDetailsContent(
                        client = uiState.client!!,
                        measurements = uiState.measurements,
                        assessments = uiState.assessments,
                        photos = uiState.photos,
                        sessions = uiState.sessions,
                        onSeeAllMeasurements = { onNavigateToMeasurements(clientId) },
                        onSeeAllAssessments = { onNavigateToAssessments(clientId) },
                        onSeeAllPhotos = { onNavigateToPhotos(clientId) },
                        onSeeAllSessions = { onNavigateToSessions(clientId) }
                    )
                }
            }
        }
    }
}

@Composable
fun ClientDetailsContent(
    client: Client,
    measurements: List<Measurement>,
    assessments: List<AssessmentResult>,
    photos: List<TransformationPhoto>,
    sessions: List<ClientSession>,
    onSeeAllMeasurements: () -> Unit,
    onSeeAllAssessments: () -> Unit,
    onSeeAllPhotos: () -> Unit,
    onSeeAllSessions: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Section
        item {
            ClientHeaderCard(client)
        }

        // Stats/Measurements Section
        if (measurements.isNotEmpty()) {
            item {
                SectionHeader(
                    title = "Measurements",
                    icon = Icons.Default.TrendingUp,
                    onSeeAll = onSeeAllMeasurements
                )
            }
            item {
                MeasurementsCard(measurements)
            }
        }

        // Assessments Section
        if (assessments.isNotEmpty()) {
            item {
                SectionHeader(
                    title = "Assessments",
                    icon = Icons.Default.Assessment,
                    onSeeAll = onSeeAllAssessments
                )
            }
            item {
                AssessmentsCard(assessments)
            }
        }

        // Photos Section
        if (photos.isNotEmpty()) {
            item {
                SectionHeader(
                    title = "Transformation Photos",
                    icon = Icons.Default.Photo,
                    onSeeAll = onSeeAllPhotos
                )
            }
            item {
                PhotosCarousel(photos)
            }
        }

        // Recent Activity Section
        if (sessions.isNotEmpty()) {
            item {
                SectionHeader(
                    title = "Recent Sessions",
                    icon = Icons.Default.FitnessCenter,
                    onSeeAll = onSeeAllSessions
                )
            }
            items(sessions.take(5)) { session ->
                SessionCard(session)
            }
        }
    }
}

@Composable
fun ClientHeaderCard(client: Client) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar placeholder
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = client.name.take(1).uppercase(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = client.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = client.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                if (client.phone != null) {
                    Text(
                        text = client.phone,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                StatusBadge(status = client.status)
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (color, text) = when (status.lowercase()) {
        "active" -> Color(0xFF10B981) to "Active"
        "inactive" -> Color.Gray to "Inactive"
        "pending" -> Color(0xFFF59E0B) to "Pending"
        else -> Color.Gray to status
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.1f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun SectionHeader(
    title: String,
    icon: ImageVector,
    onSeeAll: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        TextButton(onClick = onSeeAll) {
            Text("See All")
        }
    }
}

@Composable
fun MeasurementsCard(measurements: List<Measurement>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            measurements.take(3).forEach { measurement ->
                MeasurementItem(measurement)
                if (measurement != measurements.take(3).last()) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        }
    }
}

@Composable
fun MeasurementItem(measurement: Measurement) {
    val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    val date = try {
        Instant.parse(measurement.measurementDate)
            .atZone(ZoneId.systemDefault())
            .format(dateFormatter)
    } catch (e: Exception) {
        measurement.measurementDate
    }

    Column {
        Text(
            text = date,
            style = MaterialTheme.typography.labelMedium,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (measurement.weightKg != null) {
                MetricChip(label = "Weight", value = "${measurement.weightKg} kg")
            }
            if (measurement.bodyFatPercentage != null) {
                MetricChip(label = "Body Fat", value = "${measurement.bodyFatPercentage}%")
            }
        }
    }
}

@Composable
fun MetricChip(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun AssessmentsCard(assessments: List<AssessmentResult>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            assessments.take(3).forEach { assessment ->
                AssessmentItem(assessment)
                if (assessment != assessments.take(3).last()) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        }
    }
}

@Composable
fun AssessmentItem(assessment: AssessmentResult) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = assessment.assessmentName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = formatDate(assessment.date),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
        Text(
            text = "${assessment.value} ${assessment.unit ?: ""}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun PhotosCarousel(photos: List<TransformationPhoto>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(photos) { photo ->
            PhotoCard(photo)
        }
    }
}

@Composable
fun PhotoCard(photo: TransformationPhoto) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .height(250.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            AsyncImage(
                model = photo.photoUrl,
                contentDescription = photo.caption,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = formatDate(photo.photoDate),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
                if (photo.caption != null) {
                    Text(
                        text = photo.caption,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2
                    )
                }
            }
        }
    }
}

@Composable
fun SessionCard(session: ClientSession) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.FitnessCenter,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = session.templateName ?: "Freestyle Workout",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = formatDate(session.startTime),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            SessionStatusBadge(status = session.status)
        }
    }
}

@Composable
fun SessionStatusBadge(status: String) {
    val (color, text) = when (status.lowercase()) {
        "completed" -> Color(0xFF10B981) to "Completed"
        "in_progress" -> Color(0xFFF59E0B) to "In Progress"
        "planned" -> Color(0xFF6366F1) to "Planned"
        else -> Color.Gray to status
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.1f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}

fun formatDate(dateString: String): String {
    return try {
        val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
        Instant.parse(dateString)
            .atZone(ZoneId.systemDefault())
            .format(formatter)
    } catch (e: Exception) {
        dateString
    }
}
