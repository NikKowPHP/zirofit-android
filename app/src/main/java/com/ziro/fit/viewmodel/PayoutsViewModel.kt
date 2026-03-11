package com.ziro.fit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziro.fit.data.repository.ProfileRepository
import com.ziro.fit.model.ProfileBilling
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PayoutsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val billingStatus: ProfileBilling? = null,
    val onboardingUrl: String? = null
)

@HiltViewModel
class PayoutsViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PayoutsUiState())
    val uiState: StateFlow<PayoutsUiState> = _uiState.asStateFlow()

    init {
        fetchStripeStatus()
    }

    fun fetchStripeStatus() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = profileRepository.getBilling()
            result.onSuccess { status ->
                _uiState.update { it.copy(isLoading = false, billingStatus = status) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun fetchStripeOnboardingUrl() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = profileRepository.fetchStripeOnboardingUrl()
            result.onSuccess { url ->
                _uiState.update { it.copy(isLoading = false, onboardingUrl = url) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun clearOnboardingUrl() {
        _uiState.update { it.copy(onboardingUrl = null) }
    }
}
