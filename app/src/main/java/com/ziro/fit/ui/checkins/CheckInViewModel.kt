package com.ziro.fit.ui.checkins

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziro.fit.data.model.CheckInContext
import com.ziro.fit.data.model.CheckInPendingItem
import com.ziro.fit.data.repository.CheckInRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CheckInListUiState(
    val isLoading: Boolean = false,
    val pendingCheckIns: List<CheckInPendingItem> = emptyList(),
    val error: String? = null
)

data class CheckInDetailUiState(
    val isLoading: Boolean = false,
    val checkInContext: CheckInContext? = null,
    val isReviewSubmitting: Boolean = false,
    val reviewError: String? = null,
    val reviewSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CheckInViewModel @Inject constructor(
    private val repository: CheckInRepository
) : ViewModel() {

    private val _listUiState = MutableStateFlow(CheckInListUiState())
    val listUiState: StateFlow<CheckInListUiState> = _listUiState.asStateFlow()

    private val _detailUiState = MutableStateFlow(CheckInDetailUiState())
    val detailUiState: StateFlow<CheckInDetailUiState> = _detailUiState.asStateFlow()

    fun loadPendingCheckIns() {
        viewModelScope.launch {
            _listUiState.update { it.copy(isLoading = true, error = null) }
            val result = repository.getPendingCheckIns()
            result.onSuccess { items ->
                _listUiState.update { it.copy(isLoading = false, pendingCheckIns = items) }
            }.onFailure { e ->
                _listUiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun loadCheckInDetails(id: String) {
        viewModelScope.launch {
            _detailUiState.update { it.copy(isLoading = true, error = null, checkInContext = null, reviewSuccess = false) }
            val result = repository.getCheckInDetails(id)
            result.onSuccess { context ->
                 _detailUiState.update { it.copy(isLoading = false, checkInContext = context) }
            }.onFailure { e ->
                _detailUiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun submitReview(id: String, review: String) {
        if (review.isBlank()) return

        viewModelScope.launch {
            _detailUiState.update { it.copy(isReviewSubmitting = true, reviewError = null) }
            val result = repository.reviewCheckIn(id, review)
            result.onSuccess {
                _detailUiState.update { it.copy(isReviewSubmitting = false, reviewSuccess = true) }
            }.onFailure { e ->
                _detailUiState.update { it.copy(isReviewSubmitting = false, reviewError = e.message) }
            }
        }
    }
    
    fun resetReviewState() {
        _detailUiState.update { it.copy(reviewSuccess = false, reviewError = null) }
    }
}
