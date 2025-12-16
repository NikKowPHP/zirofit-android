package com.ziro.fit.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class ProfileMenuItem(
    val title: String,
    val description: String,
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onNavigateToSubScreen: (String) -> Unit
) {
    val menuItems = listOf(
        ProfileMenuItem("Core Info", "Manage your personal details", "profile/core_info"),
        ProfileMenuItem("Branding", "Customize your app appearance", "profile/branding"),
        ProfileMenuItem("Services", "Manage your offered services", "profile/services"),
        ProfileMenuItem("Packages", "Manage your packages", "profile/packages"),
        ProfileMenuItem("Availability", "Set your working hours", "profile/availability"),
        ProfileMenuItem("Transformation Photos", "Showcase client results", "profile/transformation_photos"),
        ProfileMenuItem("Testimonials", "Manage client reviews", "profile/testimonials"),
        ProfileMenuItem("Social Links", "Connect your social media", "profile/social_links"),
        ProfileMenuItem("External Links", "Add external resources", "profile/external_links"),
        ProfileMenuItem("Billing", "Manage subscription and payments", "profile/billing"),
        ProfileMenuItem("Benefits", "List your service benefits", "profile/benefits"),
        ProfileMenuItem("Notifications", "View your notifications", "profile/notifications")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile & Settings") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(menuItems) { item ->
                ListItem(
                    headlineContent = { Text(item.title) },
                    supportingContent = { Text(item.description) },
                    trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                    modifier = Modifier.clickable { onNavigateToSubScreen(item.route) }
                )
                Divider()
            }
        }
    }
}
