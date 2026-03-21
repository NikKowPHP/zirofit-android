package com.ziro.fit.ui.components

import android.content.Context
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ziro.fit.model.AppMode
import com.ziro.fit.util.HapticNotification
import com.ziro.fit.util.HapticStyle
import kotlin.math.roundToInt

enum class TabItem(
    val label: String,
    val icon: ImageVector,
    val trainerIcon: ImageVector = icon
) {
    CALENDAR("Calendar", Icons.Default.DateRange),
    PROGRAMS("Programs", Icons.Default.List, Icons.Default.Search),
    HOME("Home", Icons.Default.Home),
    CLIENTS("Clients", Icons.Default.People, Icons.Default.DateRange),
    MORE("More", Icons.Default.Menu);

    companion object {
        fun trainerTabs() = listOf(CALENDAR, PROGRAMS, HOME, CLIENTS, MORE)
        fun personalTabs() = listOf(CALENDAR, PROGRAMS, HOME, CLIENTS, MORE)
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
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp, bottomStart = 28.dp, bottomEnd = 28.dp),
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                tabs.forEach { tab ->
                    val icon = if (currentMode == AppMode.TRAINER) tab.trainerIcon else tab.icon
                    val isSelected = tab == selectedTab

                    TabButton(
                        icon = icon,
                        label = tab.label,
                        isSelected = isSelected,
                        onClick = {
                            HapticManagerCompat.impact(HapticStyle.LIGHT)
                            onTabSelected(tab)
                        },
                        modifier = Modifier.weight(1f)
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

@Composable
private fun TabButton(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "iconColor"
    )
    val iconScale by animateFloatAsState(
        targetValue = if (isSelected) 1.15f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "iconScale"
    )

    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .then(
                    if (isSelected) {
                        Modifier
                            .shadow(4.dp, CircleShape)
                            .background(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                                CircleShape
                            )
                            .padding(4.dp)
                    } else {
                        Modifier
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconColor,
                modifier = Modifier
                    .size(if (isSelected) 22.dp else 20.dp)
                    .offset {
                        IntOffset(
                            x = ((iconScale - 1f) * 20 * 0.5f).roundToInt(),
                            y = ((iconScale - 1f) * 20 * 0.5f).roundToInt()
                        )
                    }
            )
        }
        Text(
            text = label,
            color = iconColor,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.padding(top = 2.dp)
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
