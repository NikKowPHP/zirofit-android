package com.ziro.fit.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziro.fit.data.repository.ClientDashboardRepository
import com.ziro.fit.model.ClientDashboardData
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
        val activeProgram: com.ziro.fit.model.ActiveProgramProgress? = null
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
                        // Fallback to basic user info
                        try {
                            val userResponse = api.getMe()
                            val user = userResponse.data
                            if (user != null) {
                                val fallbackData = ClientDashboardData(
                                    id = user.id,
                                    name = user.name ?: "User",
                                    email = user.email,
                                    trainer = null,
                                    workoutSessions = emptyList(),
                                    measurements = emptyList()
                                )
                                // Also try to fetch trainer info and active program for fallback
                                viewModelScope.launch {
                                    val trainerResult = trainerRepository.getLinkedTrainer()
                                    val linkedTrainer = trainerResult.getOrNull()
                                    
                                    val activeProgramResult = repository.getActiveProgramProgress()
                                    val activeProgram = activeProgramResult.getOrNull()
                                    
                                    uiState = ClientDashboardUiState.Success(
                                        fallbackData, 
                                        linkedTrainer, 
                                        isRefreshing = false,
                                        activeProgram = activeProgram
                                    )
                                }
                            } else {
                                uiState = ClientDashboardUiState.Error("Profile not found and failed to fetch user info")
                            }
                        } catch (ex: Exception) {
                            uiState = ClientDashboardUiState.Error("Profile not found and failed to fetch user info")
                        }
                    } else {
                        // If refreshing, revert to success state with error message event (simplified here)
                        // For now we just go to error state to be safe, or we could handle it better.
                        // But since we are replacing the logic block:
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
            repository.getClientProgress()
                .onSuccess { response ->
                    val newState = uiState as? ClientDashboardUiState.Success ?: return@onSuccess
                    uiState = newState.copy(
                        progress = response,
                        isProgressLoading = false
                    )
                }
                .onFailure {
                     val newState = uiState as? ClientDashboardUiState.Success ?: return@onFailure
                     uiState = newState.copy(isProgressLoading = false)
                }
        }
    }
}
