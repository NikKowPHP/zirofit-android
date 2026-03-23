package com.ziro.fit.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ziro.fit.model.AppMode
import com.ziro.fit.ui.theme.ZiroAccent
import com.ziro.fit.util.HapticNotification
import com.ziro.fit.util.HapticStyle
import kotlin.math.roundToInt

enum class TabItem(
    val trainerLabel: String,
    val personalLabel: String,
    val trainerIcon: ImageVector,
    val personalIcon: ImageVector
) {
    CALENDAR("Calendar", "Calendar", Icons.Default.CalendarMonth, Icons.Default.CalendarMonth),
    PROGRAMS("Programs", "Explore", Icons.Default.Search, Icons.Default.Search),
    HOME("Home", "Home", Icons.Default.Home, Icons.Default.Home),
    CLIENTS("Clients", "Workouts", Icons.Default.People, Icons.AutoMirrored.Filled.List),
    MORE("More", "More", Icons.Default.Menu, Icons.Default.Menu),
    ANALYTICS("Analytics", "Analytics", Icons.Default.BarChart, Icons.Default.BarChart);
    
    fun label(mode: AppMode) = if (mode == AppMode.TRAINER) trainerLabel else personalLabel
    fun icon(mode: AppMode) = if (mode == AppMode.TRAINER) trainerIcon else personalIcon

    companion object {
        fun trainerTabs() = listOf(CALENDAR, PROGRAMS, HOME, CLIENTS, MORE)
        fun personalTabs() = listOf(PROGRAMS, CLIENTS, HOME, ANALYTICS, MORE)
        fun tabsFor(mode: AppMode) = if (mode == AppMode.TRAINER) trainerTabs() else personalTabs()
    }
}

@Composable
fun ModeTabBar(
    currentMode: AppMode,
    selectedTab: TabItem,
    onTabSelected: (TabItem) -> Unit,
    onModeSwitch: (AppMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val threshold = screenWidth * 0.35f

    var dragOffset by remember { mutableFloatStateOf(0f) }
    val animatedOffset by animateFloatAsState(
        targetValue = dragOffset,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "drag"
    )

    val tabs = TabItem.tabsFor(currentMode)
    val selectedIndex = tabs.indexOf(selectedTab).coerceAtLeast(0)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        val trigger = threshold.value
                        if (kotlin.math.abs(dragOffset) > trigger) {
                            val newMode = if (currentMode == AppMode.TRAINER) AppMode.PERSONAL else AppMode.TRAINER
                            HapticManagerCompat.notification(HapticNotification.SUCCESS)
                            onModeSwitch(newMode)
                        }
                        dragOffset = 0f
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        dragOffset += dragAmount
                    }
                )
            }
            .clickable(
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                indication = null
            ) {
            },
        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        shadowElevation = 12.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TabPillButton(
                    text = AppMode.TRAINER.displayName,
                    isSelected = currentMode == AppMode.TRAINER,
                    onClick = {
                        HapticManagerCompat.notification(HapticNotification.SUCCESS)
                        onModeSwitch(AppMode.TRAINER)
                    }
                )
                Box(modifier = Modifier.width(8.dp))
                TabPillButton(
                    text = AppMode.PERSONAL.displayName,
                    isSelected = currentMode == AppMode.PERSONAL,
                    onClick = {
                        HapticManagerCompat.notification(HapticNotification.SUCCESS)
                        onModeSwitch(AppMode.PERSONAL)
                    }
                )
            }

            Box(modifier = Modifier.height(4.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .padding(horizontal = 16.dp)
                    .alpha(0.1f)
                    .background(MaterialTheme.colorScheme.onSurface)
            )

            Box(modifier = Modifier.height(4.dp))

            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                containerColor = Color.Transparent,
                tonalElevation = 0.dp
            ) {
                tabs.forEach { tab ->
                    val icon = tab.icon(currentMode)
                    val label = tab.label(currentMode)
                    val isSelected = tab == selectedTab

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            HapticManagerCompat.impact(HapticStyle.LIGHT)
                            onTabSelected(tab)
                        },
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ZiroAccent,
                            selectedTextColor = ZiroAccent,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                        alwaysShowLabel = true
                    )
                }
            }
        }
    }
}

@Composable
private fun TabPillButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "pillBg"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "pillText"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

object HapticManagerCompat {
    fun impact(style: HapticStyle) {
        try {
            com.ziro.fit.ZiroFitApp.globalHapticManager?.impact(style)
        } catch (_: Exception) { }
    }
    fun notification(type: HapticNotification) {
        try {
            com.ziro.fit.ZiroFitApp.globalHapticManager?.notification(type)
        } catch (_: Exception) { }
    }
}
