package com.ziro.fit.ui.workouts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ziro.fit.model.WorkoutTemplate
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz

@Composable
fun TemplateCard(
    template: WorkoutTemplate,
    onClick: () -> Unit,
    onMenuClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .padding(end = 8.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .height(120.dp), // Fixed height for uniformity
             verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(
                   modifier = Modifier.fillMaxWidth(),
                   horizontalArrangement = Arrangement.SpaceBetween,
                   verticalAlignment = Alignment.Top
                ) {
                   Text(
                       text = template.name.ifEmpty { "Untitled" },
                       style = MaterialTheme.typography.titleMedium,
                       maxLines = 2,
                       overflow = TextOverflow.Ellipsis,
                       modifier = Modifier.weight(1f)
                   )
                   IconButton(
                       onClick = onMenuClick,
                       modifier = Modifier.size(24.dp)
                   ) {
                        Icon(
                            imageVector = Icons.Default.MoreHoriz,
                            contentDescription = "More options",
                            tint = MaterialTheme.colorScheme.primary
                        )
                   }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                if (template.exercises.isNotEmpty()) {
                    Text(
                        text = template.exercises.joinToString(", "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                } else {
                    Text(
                        text = "${template.exerciseCount} exercises",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }

            if (template.lastPerformed != null) {
                 Row(verticalAlignment = Alignment.CenterVertically) {
                     Text(
                         text = template.lastPerformed ?: "",
                         style = MaterialTheme.typography.labelSmall,
                         color = MaterialTheme.colorScheme.onSurfaceVariant
                     )
                 }
            } else {
                 Text(
                     text = "Never performed",
                     style = MaterialTheme.typography.labelSmall,
                     color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                 )
            }
        }
    }
}

@Composable
fun ProgramCard(
    program: com.ziro.fit.model.ProgramDto,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(240.dp)
            .padding(end = 8.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .height(140.dp),
             verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = program.name.ifEmpty { "Untitled Program" },
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (!program.description.isNullOrBlank()) {
                    Text(
                        text = program.description ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            
            val weekCount = program.weeks?.size ?: 0
            val templateCount = program.templates?.size ?: 0
            val infoText = if (weekCount > 0) "$weekCount Weeks" else "$templateCount Workouts"
            
            Text(
                text = infoText,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}
