package com.ziro.fit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziro.fit.data.repository.TrainerRepository
import com.ziro.fit.model.PublicTrainerProfileResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TrainerPublicProfileUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val profile: PublicTrainerProfileResponse? = null
)

@HiltViewModel
class TrainerPublicProfileViewModel @Inject constructor(
    private val repository: TrainerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrainerPublicProfileUiState())
    val uiState: StateFlow<TrainerPublicProfileUiState> = _uiState.asStateFlow()

    fun loadTrainerProfile(trainerId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = repository.getPublicTrainerProfile(trainerId)
            
            result.onSuccess { profile ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    profile = profile
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = error.message ?: "Failed to load trainer profile"
                )
            }
        }
    }
}
