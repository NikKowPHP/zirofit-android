package com.ziro.fit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziro.fit.model.TemplateType
import com.ziro.fit.model.WorkoutTemplate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WorkoutsUiState(
    val userTemplates: List<WorkoutTemplate> = emptyList(),
    val trainerTemplates: List<WorkoutTemplate> = emptyList(),
    val systemTemplates: List<WorkoutTemplate> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class WorkoutsViewModel @Inject constructor(
    private val workoutRepository: com.ziro.fit.data.repository.WorkoutRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutsUiState())
    val uiState: StateFlow<WorkoutsUiState> = _uiState.asStateFlow()

    init {
        loadTemplates()
    }

    private fun loadTemplates() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val templates = workoutRepository.getTemplates()
                
                _uiState.value = _uiState.value.copy(
                    userTemplates = templates.filter { it.type == TemplateType.USER },
                    systemTemplates = templates.filter { it.type == TemplateType.SYSTEM },
                    trainerTemplates = templates.filter { it.type == TemplateType.TRAINER }, 
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }
}
