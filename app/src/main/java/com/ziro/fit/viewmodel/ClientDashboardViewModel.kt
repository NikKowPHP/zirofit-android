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
        val linkedTrainer: com.ziro.fit.model.LinkedTrainer? = null
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
    }

    fun fetchDashboard() {
        viewModelScope.launch {
            uiState = ClientDashboardUiState.Loading
            repository.getClientDashboard()
                .onSuccess { data ->
                    // Fetch dedicated trainer info in parallel
                    viewModelScope.launch {
                        val trainerResult = trainerRepository.getLinkedTrainer()
                        val linkedTrainer = trainerResult.getOrNull()
                        uiState = ClientDashboardUiState.Success(data, linkedTrainer)
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
                                // Also try to fetch trainer info for fallback
                                viewModelScope.launch {
                                    val trainerResult = trainerRepository.getLinkedTrainer()
                                    val linkedTrainer = trainerResult.getOrNull()
                                    uiState = ClientDashboardUiState.Success(fallbackData, linkedTrainer)
                                }
                            } else {
                                uiState = ClientDashboardUiState.Error("Profile not found and failed to fetch user info")
                            }
                        } catch (ex: Exception) {
                            uiState = ClientDashboardUiState.Error("Profile not found and failed to fetch user info")
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
}
