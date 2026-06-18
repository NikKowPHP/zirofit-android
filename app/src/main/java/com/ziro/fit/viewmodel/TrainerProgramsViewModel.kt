package com.ziro.fit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziro.fit.data.repository.WorkoutRepository
import com.ziro.fit.model.ProgramDto
import com.ziro.fit.model.WorkoutTemplate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TrainerProgramsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val programs: List<ProgramDto> = emptyList(),
    val templates: List<WorkoutTemplate> = emptyList(),
    val searchQuery: String = ""
)

@HiltViewModel
class TrainerProgramsViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrainerProgramsUiState())
    val uiState: StateFlow<TrainerProgramsUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            // Fetch programs and templates in parallel
            val programsResult = workoutRepository.fetchTrainerPrograms()
            val templates = workoutRepository.getTemplates()

            val programs = programsResult.getOrNull()?.let {
                it.userPrograms + it.systemPrograms
            } ?: emptyList()

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                programs = programs,
                templates = templates,
                error = if (programsResult.isFailure && templates.isEmpty()) {
                    "Failed to load programs and templates"
                } else null
            )
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun getFilteredPrograms(): List<ProgramDto> {
        val state = _uiState.value
        if (state.searchQuery.isBlank()) return state.programs
        return state.programs.filter {
            it.name.contains(state.searchQuery, ignoreCase = true) ||
            it.description?.contains(state.searchQuery, ignoreCase = true) == true
        }
    }

    fun getFilteredTemplates(): List<WorkoutTemplate> {
        val state = _uiState.value
        if (state.searchQuery.isBlank()) return state.templates
        return state.templates.filter {
            it.name.contains(state.searchQuery, ignoreCase = true) ||
            it.description?.contains(state.searchQuery, ignoreCase = true) == true
        }
    }
}
