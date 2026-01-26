package com.ziro.fit.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ziro.fit.model.Message
import com.ziro.fit.viewmodel.ChatViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLiveWorkout: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    
    // Navigation Event
    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            if (event == "live_workout") {
                onNavigateToLiveWorkout()
            }
        }
    }

    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chat") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
        ) {
            if (uiState.error != null) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = uiState.error ?: "Unknown Error",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(8.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.messages) { message ->
                    MessageBubble(
                        message = message,
                        isMine = message.senderId == uiState.currentUserId,
                        onStartWorkout = { plan -> viewModel.startAiWorkout(plan) }
                    )
                }
                
                if (uiState.isLoading) {
                    item {
                         LoadingBubble()
                    }
                }
            }

            QuickActionsRow(
                onActionClick = { intent -> viewModel.generateAiWorkout(intent) },
                enabled = !uiState.isLoading
            )

            ChatInput(
                onSend = { content -> viewModel.sendMessage(content) },
                enabled = !uiState.isLoading && uiState.currentUserId.isNotEmpty()
            )
        }
    }
}

@Composable
fun LoadingBubble() {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
        Surface(
             color = MaterialTheme.colorScheme.secondaryContainer,
             shape = RoundedCornerShape(16.dp),
        ) {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Coach is thinking...", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun QuickActionsRow(onActionClick: (String) -> Unit, enabled: Boolean) {
    val actions = listOf(
        "⚡ Generate Workout" to "Generate a workout for me based on my history",
        "🤕 Strength Focus" to "I want a strength focused workout",
        "🏃 Conditioning" to "I need a conditioning session"
    )
    
    androidx.compose.foundation.lazy.LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
         items(actions) { (label, intent) ->
             SuggestionChip(
                 onClick = { onActionClick(intent) },
                 label = { Text(label) },
                 enabled = enabled
             )
         }
    }
}

@Composable
fun MessageBubble(
    message: Message,
    isMine: Boolean,
    onStartWorkout: (com.ziro.fit.model.WorkoutPlanContent) -> Unit
) {
    if (message.isSystemMessage) {
        SystemMessage(message)
    } else if (message.mediaType == Message.TYPE_WORKOUT_PLAN) {
        WorkoutPlanCard(message, onStartWorkout)
    } else {
        UserMessage(message, isMine)
    }
}

@Composable
fun WorkoutPlanCard(message: Message, onStartWorkout: (com.ziro.fit.model.WorkoutPlanContent) -> Unit) {
     val plan = remember(message.content) {
         try {
             com.google.gson.Gson().fromJson(message.content, com.ziro.fit.model.WorkoutPlanContent::class.java)
         } catch(e: Exception) {
             null
         }
     }

     if (plan == null) {
         // Fallback if parsing fails
         UserMessage(message.copy(content = "Error loading plan: ${message.content}"), false)
         return
     }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 32.dp), // Intentional margin to show it's from "AI" (left side)
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "AI Suggested: ${plan.name}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = plan.focus,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = plan.reasoning,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            HorizontalDivider()
            
            plan.exercises.take(4).forEach { exercise ->
                 Row(
                     modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                     horizontalArrangement = Arrangement.SpaceBetween
                 ) {
                     Text(text = exercise.name, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                     Text(text = "${exercise.sets} x ${exercise.reps}", style = MaterialTheme.typography.bodySmall, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                 }
            }
            if (plan.exercises.size > 4) {
                Text(text = "+ ${plan.exercises.size - 4} more...", style = MaterialTheme.typography.labelSmall)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onStartWorkout(plan) },
                modifier = Modifier.fillMaxWidth(),
                enabled = true // Always enabled now
            ) {
                Text(if (plan.templateId != null) "Start Workout" else "Save & Start Workout")
            }
        }
    }
}

@Composable
fun SystemMessage(message: Message) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = Color(0xFFFFF9C4), // Yellow-ish
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFBC02D))
        ) {
            Text(
                text = message.content,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelMedium,
                color = Color.Black
            )
        }
    }
}

@Composable
fun UserMessage(message: Message, isMine: Boolean) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isMine) Alignment.End else Alignment.Start
    ) {
        Surface(
            color = if (isMine) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isMine) 16.dp else 4.dp,
                bottomEnd = if (isMine) 4.dp else 16.dp
            )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                if (!message.mediaUrl.isNullOrEmpty()) {
                    coil.compose.AsyncImage(
                        model = message.mediaUrl,
                        contentDescription = "Shared Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                if (message.content.isNotEmpty()) {
                    Text(
                        text = message.content,
                        color = if (isMine) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
        Text(
            text = formatTime(message.createdAt),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp),
            color = Color.Gray
        )
    }
}

@Composable
fun ChatInput(
    onSend: (String) -> Unit,
    enabled: Boolean
) {
    var text by remember { mutableStateOf("") }

    Surface(
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type message...") }, // Fixed typo "Type position message..."
                maxLines = 3,
                enabled = enabled
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (text.isNotBlank()) {
                        onSend(text)
                        text = ""
                    }
                },
                enabled = enabled && text.isNotBlank()
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send")
            }
        }
    }
}

private fun formatTime(timestamp: String): String {
    return try {
        // Handle various timestamp formats if needed, standardizing on ISO 8601
        // Assuming java.time.Instant.toString() or similar "2023-10-05T10:00:00Z"
        if (timestamp.contains("T")) {
             val timePart = timestamp.split("T")[1]
             timePart.substring(0, 5) // HH:mm
        } else {
             timestamp
        }
    } catch (e: Exception) {
        ""
    }
}
