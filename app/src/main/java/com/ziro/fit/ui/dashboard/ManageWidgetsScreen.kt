package com.ziro.fit.ui.dashboard

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.forEach
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.ziro.fit.model.AnalyticsWidget
import com.ziro.fit.model.AnalyticsWidgetType
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ManageWidgetsScreen(
    initialWidgets: List<AnalyticsWidget>,
    onWidgetsChanged: (List<AnalyticsWidget>) -> Unit,
    onNavigateBack: () -> Unit
) {
    var widgets by remember { mutableStateOf(initialWidgets) }
    val coroutineScope = rememberCoroutineScope()
    
    val activeWidgets = widgets.filter { it.isVisible }.sortedBy { it.order }
    val inactiveWidgets = widgets.filter { !it.isVisible }.sortedBy { it.type.title }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Widgets") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            onWidgetsChanged(widgets)
                            onNavigateBack()
                        }
                    ) {
                        Text("Done")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Active Widgets",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(
                    items = activeWidgets,
                    key = { _, widget -> widget.id }
                ) { index, widget ->
                    DraggableWidgetItem(
                        widget = widget,
                        index = index,
                        onToggle = { visible ->
                            widgets = widgets.map { 
                                if (it.id == widget.id) it.copy(isVisible = visible) else it 
                            }
                        },
                        onDelete = {
                            widgets = widgets.map { 
                                if (it.id == widget.id) it.copy(isVisible = false) else it 
                            }
                        },
                        modifier = Modifier.fillParentMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Available Widgets",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(inactiveWidgets) { widget ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = widget.type.icon,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = widget.type.title,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                            Button(
                                onClick = {
                                    widgets = widgets.map { 
                                        if (it.id == widget.id) it.copy(isVisible = true) else it 
                                    }
                                }
                            ) {
                                Text("Add")
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DraggableWidgetItem(
    widget: AnalyticsWidget,
    index: Int,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.DragHandle,
                contentDescription = "Drag to reorder",
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Icon(
                imageVector = widget.type.icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = widget.type.title,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Position: ${index + 1}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Switch(
                checked = widget.isVisible,
                onCheckedChange = onToggle
            )
            
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Remove widget",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
