package com.ziro.fit.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziro.fit.data.repository.WorkoutRepository
import com.ziro.fit.model.ProgramDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TemplateWithStatus(
    val template: com.ziro.fit.model.WorkoutTemplateDto,
    val status: String, // "COMPLETED", "NEXT", "PENDING"
    val lastCompletedAt: String? = null,
    val isLoadingDetails: Boolean = false // Track loading state for expansion
)

data class ProgramDetailUiState(
    val program: ProgramDto? = null,
    val templatesWithStatus: List<TemplateWithStatus> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ProgramDetailViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val clientRepository: com.ziro.fit.data.repository.ClientRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProgramDetailUiState())
    val uiState: StateFlow<ProgramDetailUiState> = _uiState.asStateFlow()

    private val programId: String? = savedStateHandle["programId"]

    init {
        loadProgram()
    }

    private fun loadProgram() {
        if (programId == null) {
            _uiState.update { it.copy(error = "Program ID not found") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val program = workoutRepository.getProgram(programId)
            if (program != null) {
                // Fetch active program progress to get template statuses
                val activeProgramResult = clientRepository.getActiveProgramProgress()
                val activeProgram = activeProgramResult.getOrNull()
                
                // Map templates with their status
                val templatesWithStatus = if (activeProgram != null && activeProgram.programId == programId) {
                    program.templates?.map { template ->
                        val statusInfo = activeProgram.templateStatuses.find { it.templateId == template.id }
                        TemplateWithStatus(
                            template = template,
                            status = statusInfo?.status ?: "PENDING",
                            lastCompletedAt = statusInfo?.lastCompletedAt
                        )
                    } ?: emptyList()
                } else {
                    // If no active program or different program, all templates are PENDING
                    program.templates?.map { template ->
                        TemplateWithStatus(
                            template = template,
                            status = "PENDING",
                            lastCompletedAt = null
                        )
                    } ?: emptyList()
                }
                
                _uiState.update { 
                    it.copy(
                        program = program, 
                        templatesWithStatus = templatesWithStatus,
                        isLoading = false
                    ) 
                }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Program not found") }
            }
        }
    }

    fun loadTemplateDetails(templateId: String) {
        // Mark as loading
        _uiState.update { state ->
            state.copy(templatesWithStatus = state.templatesWithStatus.map {
                if (it.template.id == templateId) it.copy(isLoadingDetails = true) else it
            })
        }

        viewModelScope.launch {
            val result = workoutRepository.getTemplateDetails(templateId)
            result.onSuccess { detailedTemplate ->
                _uiState.update { state ->
                    state.copy(templatesWithStatus = state.templatesWithStatus.map {
                        if (it.template.id == templateId) {
                            // Merge details: Keep original description/status, update exercises
                            val mergedTemplate = it.template.copy(exercises = detailedTemplate.exercises)
                            it.copy(template = mergedTemplate, isLoadingDetails = false)
                        } else {
                            it
                        }
                    })
                }
            }.onFailure {
                // Reset loading state on failure
                _uiState.update { state ->
                    state.copy(templatesWithStatus = state.templatesWithStatus.map {
                        if (it.template.id == templateId) it.copy(isLoadingDetails = false) else it
                    })
                }
            }
        }
    }
}
