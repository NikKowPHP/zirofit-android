package com.ziro.fit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziro.fit.data.repository.ExploreRepository
import com.ziro.fit.model.ExploreEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EventsUiState(
    val isLoading: Boolean = false,
    val events: List<ExploreEvent> = emptyList(),
    val error: String? = null,
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    val isFreeOnly: Boolean? = null,
    val currentPage: Int = 1,
    val hasMore: Boolean = false
)

@HiltViewModel
class EventsViewModel @Inject constructor(
    private val repository: ExploreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EventsUiState())
    val uiState: StateFlow<EventsUiState> = _uiState.asStateFlow()

    init {
        loadEvents()
    }

    fun loadEvents(refresh: Boolean = false) {
        viewModelScope.launch {
            if (refresh) {
                _uiState.update { it.copy(currentPage = 1, events = emptyList(), isLoading = true) }
            } else {
                _uiState.update { it.copy(isLoading = true) }
            }

            val result = repository.getEvents(
                page = _uiState.value.currentPage,
                categoryId = _uiState.value.selectedCategory,
                search = _uiState.value.searchQuery.ifBlank { null },
                isFree = _uiState.value.isFreeOnly
            )

            result.onSuccess { response ->
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        events = if (refresh) response.events else state.events + response.events,
                        hasMore = response.pagination?.hasMore ?: false,
                        currentPage = (response.pagination?.page ?: state.currentPage) + 1,
                        error = null
                    )
                }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, error = error.message) }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        loadEvents(refresh = true)
    }

    fun onCategorySelected(categoryId: String?) {
        _uiState.update { it.copy(selectedCategory = categoryId) }
        loadEvents(refresh = true)
    }

    fun onFilterFree(isFree: Boolean?) {
        _uiState.update { it.copy(isFreeOnly = isFree) }
        loadEvents(refresh = true)
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
