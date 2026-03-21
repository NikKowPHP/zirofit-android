package com.ziro.fit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziro.fit.data.repository.ProfileRepository
import com.ziro.fit.model.RevenueResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject

data class RevenueUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val revenueData: RevenueResponse? = null
) {
    val totalEarnings: String
        get() = revenueData?.let { formatCurrency(it.totalEarnings) } ?: "$0.00"

    val availableForPayout: String
        get() = revenueData?.let { formatCurrency(it.availableForPayout) } ?: "$0.00"

    private fun formatCurrency(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale.US)
        return format.format(amount)
    }
}

@HiltViewModel
class RevenueViewModel @Inject constructor(
    private val repository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RevenueUiState())
    val uiState: StateFlow<RevenueUiState> = _uiState.asStateFlow()

    fun loadRevenue() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.getRevenue().collect { result ->
                result.onSuccess { data ->
                    _uiState.update { it.copy(isLoading = false, revenueData = data, error = null) }
                }.onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
            }
        }
    }
}
