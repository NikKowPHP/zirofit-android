package com.ziro.fit.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziro.fit.data.repository.ClientDashboardRepository
import com.ziro.fit.model.ClientDashboardData
import com.ziro.fit.model.AnalyticsWidget
import com.ziro.fit.model.AnalyticsWidgetType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ClientDashboardUiState {
    object Loading : ClientDashboardUiState()
    data class Success(
        val data: ClientDashboardData,
        val linkedTrainer: com.ziro.fit.model.LinkedTrainer? = null,
        val history: List<com.ziro.fit.model.HistorySession> = emptyList(),
        val historyCursor: String? = null,
        val isHistoryLoading: Boolean = false,
        val progress: com.ziro.fit.model.ClientProgressResponse? = null,
        val isProgressLoading: Boolean = false,
        val isRefreshing: Boolean = false,
        val activeProgram: com.ziro.fit.model.ActiveProgramProgress? = null,
        val volumeTrend: Double? = null,
        val consistencyTrend: Double? = null,
        val frequencyTrend: Double? = null,
        val widgets: List<com.ziro.fit.model.AnalyticsWidget> = listOf(
            com.ziro.fit.model.AnalyticsWidget("1", com.ziro.fit.model.AnalyticsWidgetType.WORKOUTS_PER_WEEK, true, 0),
            com.ziro.fit.model.AnalyticsWidget("2", com.ziro.fit.model.AnalyticsWidgetType.CONSISTENCY, true, 1),
            com.ziro.fit.model.AnalyticsWidget("3", com.ziro.fit.model.AnalyticsWidgetType.VOLUME_PROGRESSION, true, 2),
            com.ziro.fit.model.AnalyticsWidget("4", com.ziro.fit.model.AnalyticsWidgetType.MUSCLE_FOCUS, true, 3),
            com.ziro.fit.model.AnalyticsWidget("5", com.ziro.fit.model.AnalyticsWidgetType.PRS, true, 4),
            com.ziro.fit.model.AnalyticsWidget("6", com.ziro.fit.model.AnalyticsWidgetType.HEAT_MAP, true, 5),
            com.ziro.fit.model.AnalyticsWidget("7", com.ziro.fit.model.AnalyticsWidgetType.INSIGHTS, true, 6)
        )
    ) : ClientDashboardUiState()
    data class Error(val message: String) : ClientDashboardUiState()
}

@HiltViewModel
class ClientDashboardViewModel @Inject constructor(
    private val repository: ClientDashboardRepository,
    private val trainerRepository: com.ziro.fit.data.repository.TrainerRepository,
    private val api: com.ziro.fit.data.remote.ZiroApi
) : ViewModel() {

    var uiState by mutableStateOf<ClientDashboardUiState>(ClientDashboardUiState.Loading)
        private set

    init {
        fetchDashboard()
        viewModelScope.launch {
            trainerRepository.linkEvents.collect {
                fetchDashboard()
            }
        }
    }

    fun fetchDashboard(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            if (forceRefresh && uiState is ClientDashboardUiState.Success) {
                uiState = (uiState as ClientDashboardUiState.Success).copy(isRefreshing = true)
            } else {
                uiState = ClientDashboardUiState.Loading
            }

            repository.getClientDashboard()
                .onSuccess { data ->
                    // Fetch dedicated trainer info and active program in parallel
                    viewModelScope.launch {
                        val trainerResult = trainerRepository.getLinkedTrainer()
                        val linkedTrainer = trainerResult.getOrNull()
                        
                        val activeProgramResult = repository.getActiveProgramProgress()
                        val activeProgram = activeProgramResult.getOrNull()
                        
                        uiState = ClientDashboardUiState.Success(
                            data, 
                            linkedTrainer, 
                            isRefreshing = false,
                            activeProgram = activeProgram
                        )
                    }
                }
                .onFailure { e ->
                    if (e.message == "ProfileNotFound") {
                        viewModelScope.launch {
                            val trainerResult = trainerRepository.getLinkedTrainer()
                            val linkedTrainer = trainerResult.getOrNull()
                            
                            val fallbackData = ClientDashboardData(
                                id = "",
                                name = "User",
                                email = null,
                                trainer = linkedTrainer,
                                workoutSessions = emptyList(),
                                measurements = emptyList()
                            )
                            
                            uiState = ClientDashboardUiState.Success(
                                fallbackData, 
                                linkedTrainer, 
                                isRefreshing = false,
                                activeProgram = null
                            )
                        }
                    } else {
                         uiState = ClientDashboardUiState.Error(e.message ?: "Unknown error")
                    }
                }
        }
    }

    fun unlinkTrainer() {
        viewModelScope.launch {
            trainerRepository.unlinkTrainer()
                .onSuccess {
                    fetchDashboard()
                }
                .onFailure { e ->
                    // Optionally handle error in UI
                    uiState = ClientDashboardUiState.Error(e.message ?: "Failed to unlink trainer")
                }
        }
    }

    fun fetchHistory(loadMore: Boolean = false) {
        val currentState = uiState as? ClientDashboardUiState.Success ?: return
        if (currentState.isHistoryLoading) return

        viewModelScope.launch {
            uiState = currentState.copy(isHistoryLoading = true)
            
            val cursor = if (loadMore) currentState.historyCursor else null
            
            repository.getWorkoutHistory(cursor = cursor)
                .onSuccess { response ->
                    val newState = uiState as? ClientDashboardUiState.Success ?: return@onSuccess
                    val newSessions = if (loadMore) newState.history + response.sessions.sessions else response.sessions.sessions
                    uiState = newState.copy(
                        history = newSessions,
                        historyCursor = response.sessions.nextCursor,
                        isHistoryLoading = false
                    )
                }
                .onFailure {
                     val newState = uiState as? ClientDashboardUiState.Success ?: return@onFailure
                     uiState = newState.copy(isHistoryLoading = false)
                }
        }
    }

    fun fetchProgress() {
        val currentState = uiState as? ClientDashboardUiState.Success ?: return
        viewModelScope.launch {
            uiState = currentState.copy(isProgressLoading = true)
            val progressResult = repository.getClientProgress()
            val analyticsResult = repository.getClientAnalytics()
            
            val progress = progressResult.getOrNull()
            val analytics = analyticsResult.getOrNull()
            
            val combinedProgress = if (progress != null && analytics != null) {
                progress.copy(heatmap = analytics.heatmap)
            } else {
                progress
            }
            
            val trends = calculateTrends(combinedProgress)
            
            val newState = uiState as? ClientDashboardUiState.Success ?: return@launch
            uiState = newState.copy(
                progress = combinedProgress,
                isProgressLoading = false,
                volumeTrend = trends.first,
                consistencyTrend = trends.second,
                frequencyTrend = trends.third
            )
        }
    }

    private fun calculateTrends(progress: com.ziro.fit.model.ClientProgressResponse?): Triple<Double?, Double?, Double?> {
        if (progress == null) return Triple(null, null, null)
        
        val volumeHistory = progress.volumeHistory
        if (volumeHistory.isEmpty()) return Triple(null, null, null)
        
        val sortedHistory = volumeHistory.sortedBy { it.date }
        val totalEntries = sortedHistory.size
        
        if (totalEntries < 60) {
            if (totalEntries >= 30) {
                return Triple(0.0, null, null)
            }
            return Triple(null, null, null)
        }
        
        val currentPeriod = sortedHistory.takeLast(30)
        val previousPeriod = sortedHistory.dropLast(30).takeLast(30)
        
        val currentVolumeSum = currentPeriod.sumOf { it.totalVolume }
        val previousVolumeSum = previousPeriod.sumOf { it.totalVolume }
        val volumeTrend = if (previousVolumeSum > 0) {
            ((currentVolumeSum - previousVolumeSum) / previousVolumeSum) * 100
        } else {
            null
        }
        
        val currentConsistency = currentPeriod.size.toDouble()
        val previousConsistency = previousPeriod.size.toDouble()
        val consistencyTrend = if (previousConsistency > 0) {
            ((currentConsistency - previousConsistency) / previousConsistency) * 100
        } else {
            null
        }
        
        val currentFrequency = currentPeriod.size / 7.0
        val previousFrequency = previousPeriod.size / 7.0
        val frequencyTrend = if (previousFrequency > 0) {
            ((currentFrequency - previousFrequency) / previousFrequency) * 100
        } else {
            null
        }
        
        return Triple(volumeTrend, consistencyTrend, frequencyTrend)
    }
}
