package com.ziro.fit.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ziro.fit.model.ClientSession
import com.ziro.fit.util.DateTimeUtils
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun UpcomingSessionCard(
    session: ClientSession,
    onStartSession: (ClientSession) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "UPCOMING WORKOUT",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = session.name ?: "Planned Workout",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    val dateText = try {
                        val parsed = java.time.LocalDate.parse(session.plannedDate ?: "")
                        parsed.format(DateTimeFormatter.ofPattern("EEEE, MMM d", Locale.getDefault()))
                    } catch (e: Exception) {
                        session.plannedDate ?: "Scheduled"
                    }
                    
                    val timeText = try {
                        val parsedTime = java.time.LocalDateTime.parse(session.startTime) // Usually ISO
                        parsedTime.format(DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault()))
                    } catch (e: Exception) {
                        try {
                             // Fallback in case startTime is just Date or something else, though typically ISO in this app
                             val parsedTime = java.time.ZonedDateTime.parse(session.startTime)
                             parsedTime.format(DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault()))
                        } catch (e2: Exception) {
                            "Time TBD"
                        }
                    }

                    Text(
                        text = "$dateText • $timeText",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { onStartSession(session) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Start Workout")
            }
        }
    }
}
