package com.ziro.fit.ui.analytics

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziro.fit.data.repository.ClientDashboardRepository
import com.ziro.fit.model.AnalyticsWidget
import com.ziro.fit.model.AnalyticsWidgetType
import com.ziro.fit.model.ClientAnalyticsResponse
import com.ziro.fit.model.ClientProgressResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AnalyticsUiState {
    object Loading : AnalyticsUiState()
    data class Success(
        val analytics: ClientAnalyticsResponse? = null,
        val progress: ClientProgressResponse? = null,
        val isLoading: Boolean = false,
        val error: String? = null,
        val widgets: List<AnalyticsWidget> = listOf(
            AnalyticsWidget("1", AnalyticsWidgetType.WORKOUTS_PER_WEEK, true, 0),
            AnalyticsWidget("2", AnalyticsWidgetType.CONSISTENCY, true, 1),
            AnalyticsWidget("3", AnalyticsWidgetType.VOLUME_PROGRESSION, true, 2),
            AnalyticsWidget("4", AnalyticsWidgetType.MUSCLE_FOCUS, true, 3),
            AnalyticsWidget("5", AnalyticsWidgetType.PRS, true, 4),
            AnalyticsWidget("6", AnalyticsWidgetType.HEAT_MAP, true, 5),
            AnalyticsWidget("7", AnalyticsWidgetType.INSIGHTS, true, 6)
        )
    ) : AnalyticsUiState()
    data class Error(val message: String) : AnalyticsUiState()
}

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val repository: ClientDashboardRepository
) : ViewModel() {

    var uiState by mutableStateOf<AnalyticsUiState>(AnalyticsUiState.Loading)
        private set

    init {
        fetchAnalytics()
    }

    fun fetchAnalytics() {
        viewModelScope.launch {
            uiState = AnalyticsUiState.Loading
            
            val analyticsResult = repository.getClientAnalytics()
            val progressResult = repository.getClientProgress()
            
            analyticsResult.fold(
                onSuccess = { analytics ->
                    progressResult.fold(
                        onSuccess = { progress ->
                            uiState = AnalyticsUiState.Success(
                                analytics = analytics,
                                progress = progress
                            )
                        },
                        onFailure = {
                            uiState = AnalyticsUiState.Success(
                                analytics = analytics,
                                progress = null
                            )
                        }
                    )
                },
                onFailure = { error ->
                    uiState = AnalyticsUiState.Error(error.message ?: "Failed to load analytics")
                }
            )
        }
    }

    fun refresh() {
        fetchAnalytics()
    }
}
