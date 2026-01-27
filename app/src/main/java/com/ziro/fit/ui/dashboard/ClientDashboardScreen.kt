package com.ziro.fit.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ziro.fit.model.ClientDashboardData
import com.ziro.fit.model.LinkedTrainer
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientDashboardScreen(
    onLogout: () -> Unit,
    onNavigateToDiscovery: () -> Unit,
    onNavigateToCheckIns: () -> Unit,
    onNavigateToLiveWorkout: () -> Unit,

    onNavigateToChat: (String, String) -> Unit,
    onNavigateToAICoach: () -> Unit,
    viewModel: com.ziro.fit.viewmodel.ClientDashboardViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val uiState = viewModel.uiState
    val pagerState = androidx.compose.foundation.pager.rememberPagerState(pageCount = { 3 })
    val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()
    val tabs = listOf("Overview", "History", "Stats")

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabRow(selectedTabIndex = pagerState.currentPage) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = { Text(title) }
                    )
                }
            }

            // Fetch data based on selected page if needed
            LaunchedEffect(pagerState.currentPage) {
                if (pagerState.currentPage == 1 && uiState is com.ziro.fit.viewmodel.ClientDashboardUiState.Success && uiState.history.isEmpty() && !uiState.isHistoryLoading) {
                    viewModel.fetchHistory()
                } else if (pagerState.currentPage == 2 && uiState is com.ziro.fit.viewmodel.ClientDashboardUiState.Success && uiState.progress == null && !uiState.isProgressLoading) {
                    viewModel.fetchProgress()
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                when (uiState) {
                    is com.ziro.fit.viewmodel.ClientDashboardUiState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    is com.ziro.fit.viewmodel.ClientDashboardUiState.Error -> {
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "Error: ${uiState.message}", color = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.fetchDashboard() }) {
                                Text("Retry")
                            }
                        }
                    }
                    is com.ziro.fit.viewmodel.ClientDashboardUiState.Success -> {
                        val data = uiState.data
                        
                        androidx.compose.foundation.pager.HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize()
                        ) { page ->
                            when (page) {
                                0 -> {
                                    /* PullToRefreshBox is M3 1.3+ way */
                                    val pullRefreshState = rememberPullToRefreshState()
                                    PullToRefreshBox(
                                        isRefreshing = uiState.isRefreshing,
                                        onRefresh = { viewModel.fetchDashboard(forceRefresh = true) },
                                        state = pullRefreshState,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        ClientDashboardHome(
                                            data, 
                                            uiState.linkedTrainer, 
                                            viewModel::unlinkTrainer, 
                                            onNavigateToDiscovery, 
                                            onNavigateToCheckIns,
                                            onNavigateToChat,
                                            onNavigateToAICoach,
                                            onStartSession = { session ->
                                                viewModel.startSession(session, onNavigateToLiveWorkout)
                                            }
                                        )
                                    }
                                }
                                1 -> ClientHistoryContent(
                                    sessions = uiState.history,
                                    isLoading = uiState.isHistoryLoading,
                                    canLoadMore = uiState.historyCursor != null,
                                    onLoadMore = { viewModel.fetchHistory(loadMore = true) }
                                )
                                2 -> ClientStatisticsContent(
                                    progress = uiState.progress,
                                    measurements = data.measurements ?: emptyList(),
                                    isLoading = uiState.isProgressLoading
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ClientDashboardHome(
    data: ClientDashboardData,
    linkedTrainer: LinkedTrainer?,
    onUnlinkTrainer: () -> Unit,
    onNavigateToDiscovery: () -> Unit,
    onNavigateToCheckIns: () -> Unit,
    onNavigateToChat: (String, String) -> Unit,
    onNavigateToAICoach: () -> Unit,
    onStartSession: (com.ziro.fit.model.ClientSession) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(androidx.compose.foundation.rememberScrollState())
            .padding(16.dp)
    ) {
        // Welcome message removed
        Spacer(modifier = Modifier.height(16.dp))

        // Upcoming Sessions Section
        val upcomingSessions = data.workoutSessions?.filter { it.status == "PLANNED" } ?: emptyList()
        Text(
            text = "Upcoming Sessions",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        if (upcomingSessions.isNotEmpty()) {
            upcomingSessions.forEach { session ->
                UpcomingSessionCard(session = session, onStartSession = onStartSession)
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Box(modifier = Modifier.padding(16.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("No upcoming workouts scheduled", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        // AI Coach Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "AI Coach",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Generate a personalized program based on your goals.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onNavigateToAICoach,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.onTertiary
                    )
                ) {
                    Text("Start AI Coach")
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        // Recent Activity Section (Last Completed Workout)
        val recentSessions = data.workoutSessions?.filter { it.status == "COMPLETED" }?.sortedByDescending { it.startTime } ?: emptyList()
        if (recentSessions.isNotEmpty()) {
            Text(
                text = "Recent Activity",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            val lastSession = recentSessions.first()
            com.ziro.fit.ui.client.SessionCard(session = lastSession)
            Spacer(modifier = Modifier.height(24.dp))
        }

        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Weekly Check-In",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                     text = "Keep your trainer updated with your progress.",
                     style = MaterialTheme.typography.bodyMedium,
                     color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                 Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onNavigateToCheckIns, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
                    Text("View Check-Ins")
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Your Trainer",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (linkedTrainer != null || data.trainer != null) {
                    val trainer = linkedTrainer
                    val dashboardTrainer = data.trainer
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (trainer?.profile?.profilePhotoPath != null) {
                            AsyncImage(
                                model = trainer.profile.profilePhotoPath,
                                contentDescription = "Trainer Photo",
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                        }
                        Column {
                            Text(
                                text = "Name: ${trainer?.name ?: dashboardTrainer?.name ?: "N/A"}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Email: ${trainer?.email ?: dashboardTrainer?.email ?: "N/A"}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = {
                             val trainerId = trainer?.id ?: dashboardTrainer?.id ?: ""
                             val clientId = data.id 
                             if (trainerId.isNotEmpty() && clientId.isNotEmpty()) {
                                 onNavigateToChat(clientId, trainerId)
                             }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Chat with Trainer")
                    }
                    
                    if (trainer?.profile?.aboutMe != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = trainer.profile.aboutMe,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = onUnlinkTrainer,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Unlink Trainer")
                    }
                } else {
                    Text(text = "No trainer linked.", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = onNavigateToDiscovery) {
                        Text("Find a Trainer")
                    }
                }
            }
        }
    }
}
