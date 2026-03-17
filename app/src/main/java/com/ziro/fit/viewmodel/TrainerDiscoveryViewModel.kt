package com.ziro.fit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziro.fit.data.repository.TrainerRepository
import com.ziro.fit.data.repository.ExploreRepository
import com.ziro.fit.model.ExploreEvent
import com.ziro.fit.model.ExploreEventsResponse
import com.ziro.fit.model.TrainerSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TrainerDiscoveryUiState(
    val isLoading: Boolean = false,
    val trainers: List<TrainerSummary> = emptyList(),
    val events: List<ExploreEvent> = emptyList(),
    val error: String? = null
)

@OptIn(FlowPreview::class)
@HiltViewModel
class TrainerDiscoveryViewModel @Inject constructor(
    private val trainerRepository: TrainerRepository,
    private val exploreRepository: ExploreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrainerDiscoveryUiState())
    val uiState: StateFlow<TrainerDiscoveryUiState> = _uiState.asStateFlow()

    val searchQuery = MutableStateFlow("")
    val selectedSpecialty = MutableStateFlow<String?>(null)
    val selectedLocation = MutableStateFlow("")
    val minRating = MutableStateFlow(0.0)
    val discoveryType = MutableStateFlow(DiscoveryType.ALL)
    val selectedSortOption = MutableStateFlow(SortOption.CLOSEST)

    enum class DiscoveryType(val label: String) {
        SPECIALISTS("Specialists"),
        EVENTS("Events"),
        ALL("All")
    }

    enum class SortOption(val label: String, val apiKey: String) {
        CLOSEST("Closest", "distance"),
        BEST_RATED("Highest Rated", "rating"),
        NEWEST("Newest", "newest"),
        NAME_ASC("Name (A-Z)", "name_asc")
    }

    private data class FilterState(
        val search: String,
        val specialty: String?,
        val location: String,
        val rating: Double,
        val type: DiscoveryType,
        val sort: SortOption
    )

    init {
        val searchFlow = searchQuery.debounce(500).distinctUntilChanged()
        
        val filterFlow1 = combine(
            searchFlow,
            selectedSpecialty,
            selectedLocation,
            minRating
        ) { search, specialty, location, rating ->
            Pair(Pair(search, specialty), Pair(location, rating))
        }
        
        val filterFlow2 = combine(
            discoveryType,
            selectedSortOption
        ) { type, sort ->
            Pair(type, sort)
        }
        
        viewModelScope.launch {
            combine(filterFlow1, filterFlow2) { f1, f2 ->
                FilterState(
                    search = f1.first.first,
                    specialty = f1.first.second,
                    location = f1.second.first,
                    rating = f1.second.second,
                    type = f2.first,
                    sort = f2.second
                )
            }.collect { filter ->
                loadResults(filter)
            }
        }
    }

    private suspend fun loadResults(filter: FilterState) {
        _uiState.update { it.copy(isLoading = true, error = null) }

        val fetchTrainers = filter.type == DiscoveryType.SPECIALISTS || filter.type == DiscoveryType.ALL
        val fetchEvents = filter.type == DiscoveryType.EVENTS || filter.type == DiscoveryType.ALL

        var trainersList = emptyList<TrainerSummary>()
        var eventsList = emptyList<ExploreEvent>()

        try {
            if (fetchTrainers) {
                val result = trainerRepository.getTrainers(
                    search = filter.search.ifBlank { null },
                    location = filter.location.ifBlank { null },
                    minRating = if (filter.rating > 0.0) filter.rating else null,
                    specialties = filter.specialty,
                    sortBy = filter.sort.apiKey
                )
                trainersList = result.getOrDefault(emptyList())
            }

            if (fetchEvents) {
                val result = exploreRepository.getEvents(
                    search = filter.search.ifBlank { null }
                )
                eventsList = result.getOrDefault(ExploreEventsResponse(emptyList(), null)).events
            }

            _uiState.update { 
                it.copy(
                    isLoading = false,
                    trainers = trainersList,
                    events = eventsList
                )
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(isLoading = false, error = e.message) }
        }
    }

    fun resetFilters() {
        searchQuery.value = ""
        selectedSpecialty.value = null
        selectedLocation.value = ""
        minRating.value = 0.0
        selectedSortOption.value = SortOption.CLOSEST
    }
}
