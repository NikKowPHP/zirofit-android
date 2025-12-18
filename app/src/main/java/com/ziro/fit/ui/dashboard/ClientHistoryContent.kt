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
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = formatSessionDate(session.startTime),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Text(
                    text = "Status: ${session.status}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (session.status.equals("COMPLETED", ignoreCase = true)) Color.Green else Color.Gray
                )
                if (session.workoutTemplate?.name != null) {
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Template: ${session.workoutTemplate.name}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (!session.exerciseLogs.isNullOrEmpty()) {
                Text(
                    text = "Exercises:",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                session.exerciseLogs.forEach { log ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = log.exercise.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${log.reps ?: "-"} x ${log.weight ?: "-"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                 Text(
                    text = "No exercises logged.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
