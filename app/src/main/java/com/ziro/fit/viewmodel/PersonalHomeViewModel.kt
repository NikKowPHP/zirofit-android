package com.ziro.fit.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziro.fit.data.repository.ClientDashboardRepository
import com.ziro.fit.data.repository.TrainerRepository
import com.ziro.fit.model.ClientDashboardData
import com.ziro.fit.model.ClientDashboardSession
import com.ziro.fit.model.ClientSession
import com.ziro.fit.model.DailyTarget
import com.ziro.fit.model.HistorySession
import com.ziro.fit.model.LinkedTrainer
import com.ziro.fit.model.Notification
import com.ziro.fit.service.DailyTargetManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class PersonalHomeUiState(
    val dashboard: ClientDashboardData? = null,
    val linkedTrainer: LinkedTrainer? = null,
    val activeProgram: com.ziro.fit.model.ActiveProgramProgress? = null,
    val upcomingSessions: List<ClientDashboardSession> = emptyList(),
    val recentHistory: List<HistorySession> = emptyList(),
    val notifications: List<Notification> = emptyList(),
    val unreadCount: Int = 0,
    val dailyTargets: List<DailyTarget> = emptyList(),
    val isCheckInComplete: Boolean = false,
    val lastCheckInDate: String? = null,
    val streakCount: Int = 0,
    val remainingCredits: Int? = null,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class PersonalHomeViewModel @Inject constructor(
    private val dashboardRepository: ClientDashboardRepository,
    private val trainerRepository: TrainerRepository,
    private val profileRepository: com.ziro.fit.data.repository.ProfileRepository,
    private val dailyTargetManager: DailyTargetManager
) : ViewModel() {

    var uiState by mutableStateOf(PersonalHomeUiState())
        private set

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = uiState.dashboard == null, error = null)

            // Launch all data fetches concurrently
            val dashboardJob = launch { fetchDashboard() }
            val notificationsJob = launch { fetchNotifications() }
            val targetsJob = launch { fetchDailyTargets() }

            dashboardJob.join()
            notificationsJob.join()
            targetsJob.join()

            uiState = uiState.copy(isLoading = false)
        }
    }

    fun refresh() {
        viewModelScope.launch {
            uiState = uiState.copy(isRefreshing = true)
            loadData()
            uiState = uiState.copy(isRefreshing = false)
        }
    }

    private suspend fun fetchDashboard() {
        dashboardRepository.getClientDashboard()
            .onSuccess { data ->
                // Fetch linked trainer and active program in parallel
                val trainerResult = trainerRepository.getLinkedTrainer()
                val programResult = dashboardRepository.getActiveProgramProgress()

                // Get analytics for streak count
                val analyticsResult = dashboardRepository.getClientAnalytics()

                uiState = uiState.copy(
                    dashboard = data,
                    linkedTrainer = trainerResult.getOrNull(),
                    activeProgram = programResult.getOrNull(),
                    streakCount = analyticsResult.getOrNull()?.streak ?: 0
                )
            }
            .onFailure { e ->
                if (e.message != "ProfileNotFound") {
                    uiState = uiState.copy(error = e.message)
                }
            }
    }

    private suspend fun fetchNotifications() {
        profileRepository.getNotifications()
            .onSuccess { response ->
                val notifications = response.notifications
                uiState = uiState.copy(
                    notifications = notifications,
                    unreadCount = notifications.count { !it.readStatus }
                )
            }
    }

    private suspend fun fetchDailyTargets() {
        dailyTargetManager.refresh()
        uiState = uiState.copy(
            dailyTargets = dailyTargetManager.targets
        )
    }

    fun unlinkTrainer() {
        viewModelScope.launch {
            trainerRepository.unlinkTrainer()
                .onSuccess {
                    uiState = uiState.copy(linkedTrainer = null)
                    // Also optimistically update dashboard
                    val current = uiState.dashboard
                    if (current != null) {
                        uiState = uiState.copy(
                            dashboard = current.copy(trainer = null, remainingCredits = null)
                        )
                    }
                }
        }
    }

    fun acceptCoachInvite(notificationId: String) {
        viewModelScope.launch {
            trainerRepository.acceptTrainerLinkRequest(notificationId)
                .onSuccess { message ->
                    // Refresh data to reflect the new linked trainer
                    loadData()
                }
                .onFailure { e ->
                    uiState = uiState.copy(error = e.message)
                }
        }
    }

    fun declineCoachInvite(notificationId: String) {
        viewModelScope.launch {
            trainerRepository.declineTrainerLinkRequest(notificationId)
                .onSuccess { message ->
                    // Refresh to remove the notification
                    loadData()
                }
                .onFailure { e ->
                    uiState = uiState.copy(error = e.message)
                }
        }
    }

    fun addTarget(type: String, goal: Int, exerciseId: String?) {
        viewModelScope.launch {
            dailyTargetManager.createDailyTarget(type, goal, exerciseId)
                .onSuccess { newTarget ->
                    uiState = uiState.copy(
                        dailyTargets = dailyTargetManager.targets
                    )
                }
                .onFailure { e ->
                    uiState = uiState.copy(error = e.message)
                }
        }
    }

    fun deleteTarget(target: com.ziro.fit.model.DailyTarget) {
        viewModelScope.launch {
            // Optimistically remove from UI
            uiState = uiState.copy(
                dailyTargets = uiState.dailyTargets - target
            )
            // API call would go here when the server supports it
            // For now, the local cache handles deletion on next refresh
        }
    }

    fun addTargetProgress(target: com.ziro.fit.model.DailyTarget, progress: Int) {
        viewModelScope.launch {
            dailyTargetManager.updateTargetProgress(target.id, progress)
                .onSuccess { updated ->
                    // Update the target in the list
                    uiState = uiState.copy(
                        dailyTargets = uiState.dailyTargets.map {
                            if (it.id == target.id) updated else it
                        }
                    )
                }
                .onFailure { e ->
                    uiState = uiState.copy(error = e.message)
                }
        }
    }

    fun dismissCoachBanner() {
        // Persisted in DataStore - will be handled at screen level
    }

    fun dismissCheckInBanner() {
        // Same as above
    }
}
