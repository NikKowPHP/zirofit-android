package com.ziro.fit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziro.fit.data.repository.ExploreRepository
import com.ziro.fit.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExploreUiState(
    val isLoading: Boolean = false,
    val cities: List<ExploreCity> = emptyList(),
    val categories: List<ExploreCategory> = emptyList(),
    val featuredEvents: List<ExploreEvent> = emptyList(),
    val featuredTrainers: List<TrainerSummary> = emptyList(),
    val upcomingEvents: Map<String, List<ExploreEvent>> = emptyMap(),
    val selectedCity: ExploreCity? = null,
    val selectedCategory: ExploreCategory? = null,
    val error: String? = null
)

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val repository: ExploreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadMetadata()
    }

    fun loadMetadata() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.getMetadata().onSuccess { meta ->
                _uiState.update { it.copy(
                    cities = listOf(ExploreCity("current", "Current Location", isCurrentLocation = true)) + meta.cities,
                    categories = listOf(ExploreCategory("all", "All")) + meta.categories,
                    selectedCity = ExploreCity("current", "Current Location", isCurrentLocation = true)
                ) }
                refreshContent()
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun refreshContent() {
        val city = _uiState.value.selectedCity
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Load Featured
            repository.getFeatured(cityId = city?.id.takeIf { it != "current" }).onSuccess { featured ->
                _uiState.update { it.copy(
                    featuredEvents = featured.featuredEvents,
                    featuredTrainers = featured.featuredTrainers
                ) }
            }

            // Load Grouped Events
            repository.getEvents(
                categoryId = _uiState.value.selectedCategory?.id.takeIf { it != "all" },
                limit = 50
            ).onSuccess { res ->
                val grouped = res.events.groupBy { it.startTime.take(10) } // YYYY-MM-DD
                _uiState.update { it.copy(upcomingEvents = grouped, isLoading = false) }
            }
        }
    }

    fun selectCity(city: ExploreCity) {
        _uiState.update { it.copy(selectedCity = city) }
        refreshContent()
    }

    fun selectCategory(category: ExploreCategory) {
        _uiState.update { it.copy(selectedCategory = category) }
        refreshContent()
    }
}
