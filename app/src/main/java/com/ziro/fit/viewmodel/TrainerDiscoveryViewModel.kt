package com.ziro.fit.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziro.fit.data.repository.TrainerRepository
import com.ziro.fit.model.TrainerSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class TrainerDiscoveryUiState {
    object Loading : TrainerDiscoveryUiState()
    data class Success(val trainers: List<TrainerSummary>) : TrainerDiscoveryUiState()
    data class Error(val message: String) : TrainerDiscoveryUiState()
}

@HiltViewModel
class TrainerDiscoveryViewModel @Inject constructor(
    private val repository: TrainerRepository
) : ViewModel() {

    var uiState by mutableStateOf<TrainerDiscoveryUiState>(TrainerDiscoveryUiState.Loading)
        private set

    init {
        loadTrainers()
    }

    fun loadTrainers(search: String? = null) {
        viewModelScope.launch {
            uiState = TrainerDiscoveryUiState.Loading
            val result = repository.getTrainers(search)
            uiState = if (result.isSuccess) {
                TrainerDiscoveryUiState.Success(result.getOrDefault(emptyList()))
            } else {
                TrainerDiscoveryUiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }
}
