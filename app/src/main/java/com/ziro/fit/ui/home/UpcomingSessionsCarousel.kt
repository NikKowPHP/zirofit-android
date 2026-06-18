package com.ziro.fit.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ziro.fit.model.ClientDashboardSession
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun UpcomingSessionsCarousel(
    sessions: List<ClientDashboardSession>,
    onSessionClick: (ClientDashboardSession) -> Unit,
    modifier: Modifier = Modifier
) {
    if (sessions.isEmpty()) return

    Column(modifier = modifier) {
        Text(
            text = "Upcoming",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sessions) { session ->
                UpcomingSessionCard(
                    session = session,
                    onClick = { onSessionClick(session) }
                )
            }
        }
    }
}

@Composable
private fun UpcomingSessionCard(
    session: ClientDashboardSession,
    onClick: () -> Unit
) {
    val cardColor = if (session.isTrainerAssigned == true) Color(0xFF9C27B0) else Color(0xFF2196F3)

    Surface(
        modifier = Modifier
            .width(160.dp)
            .height(140.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = cardColor
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Date badge
                val dateText = try {
                    val parsed = LocalDate.parse(session.date.take(10))
                    val month = parsed.format(DateTimeFormatter.ofPattern("MMM", Locale.getDefault()))
                    val day = parsed.format(DateTimeFormatter.ofPattern("dd", Locale.getDefault()))
                    "$month $day"
                } catch (e: Exception) {
                    session.date.take(5)
                }

                Text(
                    text = dateText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                )

                if (session.isTrainerAssigned == true) {
                    Text(
                        text = "\u2B50", // star
                        fontSize = 14.sp
                    )
                } else {
                    Text(
                        text = "\uD83D\uDCC5", // calendar
                        fontSize = 14.sp
                    )
                }
            }

            if (session.isTrainerAssigned == true) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Coach Assigned",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Yellow
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = session.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${session.duration} min",
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}
