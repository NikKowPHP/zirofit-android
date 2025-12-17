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
    data class Success(val data: ClientDashboardData) : ClientDashboardUiState()
    data class Error(val message: String) : ClientDashboardUiState()
}

@HiltViewModel
class ClientDashboardViewModel @Inject constructor(
    private val repository: ClientDashboardRepository
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
                    uiState = ClientDashboardUiState.Success(data)
                }
                .onFailure { e ->
                    uiState = ClientDashboardUiState.Error(e.message ?: "Unknown error")
                }
        }
    }
}
