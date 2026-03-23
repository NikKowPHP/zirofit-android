package com.ziro.fit.ui.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.ziro.fit.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToBlogManagement: () -> Unit,
    onNavigateToEventModeration: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Settings", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = StrongBackground
                ),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }
            )
        },
        containerColor = StrongBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            item {
                Text(
                    text = "Management",
                    style = MaterialTheme.typography.titleSmall,
                    color = StrongTextSecondary,
                    modifier = Modifier.padding(16.dp)
                )
            }

            item {
                AdminSettingsItem(
                    icon = Icons.Default.Article,
                    title = "Blog Management",
                    subtitle = "Create, edit, and manage blog posts",
                    onClick = onNavigateToBlogManagement
                )
                HorizontalDivider(color = StrongSecondaryBackground)
            }

            item {
                AdminSettingsItem(
                    icon = Icons.Default.Event,
                    title = "Event Moderation",
                    subtitle = "Review and approve pending events",
                    onClick = onNavigateToEventModeration
                )
                HorizontalDivider(color = StrongSecondaryBackground)
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Coming Soon",
                    style = MaterialTheme.typography.titleSmall,
                    color = StrongTextSecondary,
                    modifier = Modifier.padding(16.dp)
                )
            }

            item {
                AdminSettingsItem(
                    icon = Icons.Default.People,
                    title = "User Management",
                    subtitle = "Manage users and roles",
                    onClick = { },
                    enabled = false
                )
                HorizontalDivider(color = StrongSecondaryBackground)
            }

            item {
                AdminSettingsItem(
                    icon = Icons.Default.Settings,
                    title = "App Settings",
                    subtitle = "Configure application settings",
                    onClick = { },
                    enabled = false
                )
                HorizontalDivider(color = StrongSecondaryBackground)
            }

            item {
                AdminSettingsItem(
                    icon = Icons.Default.Info,
                    title = "About",
                    subtitle = "App version and information",
                    onClick = { },
                    enabled = false
                )
                HorizontalDivider(color = StrongSecondaryBackground)
            }
        }
    }
}

@Composable
fun AdminSettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (enabled) StrongBlue else StrongTextSecondary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (enabled) Color.White else StrongTextSecondary
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = StrongTextSecondary
                )
            }
        }
        if (enabled) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = StrongTextSecondary
            )
        } else {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = StrongTextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
