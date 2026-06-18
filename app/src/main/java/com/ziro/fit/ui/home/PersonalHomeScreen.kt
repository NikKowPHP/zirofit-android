package com.ziro.fit.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ziro.fit.model.ClientDashboardSession
import com.ziro.fit.model.HistorySession
import com.ziro.fit.model.Notification
import com.ziro.fit.viewmodel.PersonalHomeUiState
import com.ziro.fit.viewmodel.PersonalHomeViewModel
import com.ziro.fit.ui.theme.StrongBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalHomeScreen(
    onNavigateToDiscovery: () -> Unit,
    onNavigateToCheckIns: () -> Unit,
    onNavigateToLiveWorkout: () -> Unit,
    onNavigateToTemplates: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToSessionDetail: (String) -> Unit,
    onNavigateToCoachProfile: (String) -> Unit,
    onChatWithTrainer: (String, String) -> Unit,
    onQuickStart: () -> Unit,
    viewModel: PersonalHomeViewModel = hiltViewModel()
) {
    val state = viewModel.uiState
    val scrollState = rememberScrollState()

    // Dismissal states (equivalent to @AppStorage in iOS)
    var coachBannerDismissed by remember { mutableStateOf(false) }
    var checkInBannerDismissed by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box {
            // Main scrollable content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                // Top spacer for floating header
                Spacer(modifier = Modifier.height(48.dp))

                if (state.isLoading && state.dashboard == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    HomeContent(
                        state = state,
                        coachBannerDismissed = coachBannerDismissed,
                        checkInBannerDismissed = checkInBannerDismissed,
                        onDismissCoachBanner = { coachBannerDismissed = true },
                        onDismissCheckInBanner = { checkInBannerDismissed = true },
                        onAcceptCoachInvite = { /* TODO */ },
                        onDeclineCoachInvite = { /* TODO */ },
                        onNavigateToDiscovery = onNavigateToDiscovery,
                        onNavigateToCheckIns = onNavigateToCheckIns,
                        onQuickStart = onQuickStart,
                        onNavigateToTemplates = onNavigateToTemplates,
                        onSessionClick = { session ->
                            onNavigateToSessionDetail(session.id)
                        },
                        onHistoryClick = { session ->
                            onNavigateToSessionDetail(session.id)
                        },
                        onCoachClick = { },
                        onDisconnectTrainer = { viewModel.unlinkTrainer() },
                        onAddTarget = { /* TODO: navigate to add daily target */ },
                        onDeleteTarget = { /* TODO */ },
                        onAddProgress = { _, _ -> /* TODO */ }
                    )
                }

                // Bottom spacing
                Spacer(modifier = Modifier.height(100.dp))
            }

            // Floating header
            ZiroHeader(
                title = state.dashboard?.name ?: "Home",
                showAvatar = true,
                avatarUrl = null, // TODO: get from user profile
                unreadBadgeCount = state.unreadCount,
                onAvatarTap = onNavigateToProfile,
                onNotificationsTap = onNavigateToNotifications
            )
        }
    }
}

@Composable
private fun HomeContent(
    state: PersonalHomeUiState,
    coachBannerDismissed: Boolean,
    checkInBannerDismissed: Boolean,
    onDismissCoachBanner: () -> Unit,
    onDismissCheckInBanner: () -> Unit,
    onAcceptCoachInvite: () -> Unit,
    onDeclineCoachInvite: () -> Unit,
    onNavigateToDiscovery: () -> Unit,
    onNavigateToCheckIns: () -> Unit,
    onQuickStart: () -> Unit,
    onNavigateToTemplates: () -> Unit,
    onSessionClick: (ClientDashboardSession) -> Unit,
    onHistoryClick: (HistorySession) -> Unit,
    onCoachClick: () -> Unit,
    onDisconnectTrainer: () -> Unit,
    onAddTarget: () -> Unit,
    onDeleteTarget: (com.ziro.fit.model.DailyTarget) -> Unit,
    onAddProgress: (com.ziro.fit.model.DailyTarget, Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 0. Trainer Invitation Hero Card
        // Look for pending coach invite notifications
        val pendingInvite = state.notifications.find {
            it.type == "trainer_link_request" || it.message.contains("invited", ignoreCase = true)
        }
        if (pendingInvite != null) {
            CoachInvitationCard(
                notification = pendingInvite,
                onAccept = onAcceptCoachInvite,
                onDecline = onDeclineCoachInvite
            )
        }

        // 1. Streak Motivation Hook
        StreakWidget(streakCount = state.streakCount)

        // 2. Coach Card / AI Coach Banner
        if (state.linkedTrainer != null) {
            // Coach Profile Card
            CoachProfileCard(
                trainerName = state.linkedTrainer.name,
                trainerUsername = state.linkedTrainer.email,
                onClick = onCoachClick,
                onDisconnect = onDisconnectTrainer
            )

            // Credit Status
            state.remainingCredits?.let { credits ->
                if (credits > 0) {
                    CreditStatusWidget(remainingCredits = credits)
                }
            }
        } else if (!coachBannerDismissed) {
            CoachBanner(
                onTap = onNavigateToDiscovery,
                onDismiss = onDismissCoachBanner
            )
        }

        // 3. Active Routine / Program
        ActiveProgramSection(
            activeProgram = state.activeProgram,
            onStartWorkout = { templateId ->
                // Navigate to live workout with template
                onQuickStart()
            },
            onQuickStart = onQuickStart,
            onTemplates = onNavigateToTemplates,
            hasTrainer = state.linkedTrainer != null
        )

        // 4. Check-In Banner
        if (!checkInBannerDismissed) {
            CheckInBanner(
                isComplete = state.isCheckInComplete,
                onTap = onNavigateToCheckIns,
                onDismiss = onDismissCheckInBanner,
                hasTrainer = state.linkedTrainer != null
            )
        }

        // 5. Upcoming Sessions
        UpcomingSessionsCarousel(
            sessions = state.upcomingSessions,
            onSessionClick = onSessionClick
        )

        // 6. Daily Targets
        DailyTargetsSection(
            targets = state.dailyTargets,
            onAddTarget = onAddTarget,
            onDeleteTarget = onDeleteTarget,
            onAddProgress = onAddProgress
        )

        // 7. Quick Actions
        QuickActionsSection(
            onQuickStart = onQuickStart,
            onTemplates = onNavigateToTemplates
        )

        // 8. Recent History
        RecentHistorySection(
            sessions = state.recentHistory,
            onSessionClick = onHistoryClick
        )
    }
}
