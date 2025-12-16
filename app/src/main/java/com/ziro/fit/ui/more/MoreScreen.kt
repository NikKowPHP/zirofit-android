package com.ziro.fit.ui.more

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(
    onNavigateToAssessments: () -> Unit,
    onNavigateToBookings: () -> Unit,
    onNavigateToCheckIns: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("More") }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            item {
                MoreMenuItem(
                    icon = Icons.Default.Build,
                    title = "Assessments Library",
                    subtitle = "Manage custom assessment types",
                    onClick = onNavigateToAssessments
                )
                HorizontalDivider()
            }
            item {
                MoreMenuItem(
                    icon = Icons.Default.DateRange, // Reusing DateRange as it fits Bookings
                    title = "Bookings",
                    subtitle = "Manage your bookings",
                    onClick = onNavigateToBookings
                )
                HorizontalDivider()
            }
            // Add more items here in the future
        }
    }
}

@Composable
fun MoreMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
