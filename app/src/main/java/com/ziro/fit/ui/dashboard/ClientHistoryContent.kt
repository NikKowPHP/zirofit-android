package com.ziro.fit.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ziro.fit.model.HistorySession
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ClientHistoryContent(
    sessions: List<HistorySession>,
    isLoading: Boolean,
    canLoadMore: Boolean,
    onLoadMore: () -> Unit
) {
    val listState = rememberLazyListState()

    // Infinite scroll detection
    val buffer = 2
    val isScrolledToEnd by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            if (totalItems == 0) return@derivedStateOf false
            val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleItemIndex >= (totalItems - buffer)
        }
    }

    LaunchedEffect(isScrolledToEnd) {
        if (isScrolledToEnd && !isLoading && canLoadMore) {
            onLoadMore()
        }
    }

    if (sessions.isEmpty() && !isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            Text(text = "No training history available.")
        }
    } else {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(sessions) { session ->
                SessionHistoryItem(session)
            }
            if (isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = androidx.compose.ui.Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
fun SessionHistoryItem(session: HistorySession) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Text(
                    text = formatSessionDate(session.startTime),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                val isCompleted = session.status.equals("COMPLETED", ignoreCase = true)
                Surface(
                    color = if (isCompleted) Color(0xFFE8F5E9) else Color(0xFFF5F5F5),
                    contentColor = if (isCompleted) Color(0xFF2E7D32) else Color(0xFF616161),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = session.status.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            
            if (session.workoutTemplate?.name != null) {
                Text(
                    text = session.workoutTemplate.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            
            Divider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)
            
            if (!session.exerciseLogs.isNullOrEmpty()) {
                // Group by exercise name
                val groupedLogs = session.exerciseLogs.groupBy { it.exercise.name }
                
                groupedLogs.forEach { (exerciseName, logs) ->
                    Column(modifier = Modifier.padding(bottom = 12.dp)) {
                        Text(
                            text = exerciseName,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        // Display sets for this exercise
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            logs.forEachIndexed { index, log ->
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "${log.weight ?: "-"} x ${log.reps ?: "-"}",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                 Text(
                    text = "No exercises logged",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }
    }
}

private fun formatSessionDate(timestamp: String): String {
    return try {
        // Tries to parse ISO 8601 format
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val date = inputFormat.parse(timestamp)
        val outputFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        date?.let { outputFormat.format(it) } ?: timestamp
    } catch (e: Exception) {
         try {
             // Fallback for different format if needed, or just return timestamp
             val inputFormat2 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
             val date2 = inputFormat2.parse(timestamp)
             val outputFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
             date2?.let { outputFormat.format(it) } ?: timestamp
         } catch (e2: Exception) {
             timestamp
         }
    }
}
