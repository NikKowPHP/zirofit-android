package com.ziro.fit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziro.fit.data.repository.ClientRepository
import com.ziro.fit.model.ProgramDto
import com.ziro.fit.model.ProgramResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ClientProgramsUiState(
    val isLoading: Boolean = false,
    val isGenerating: Boolean = false,
    val error: String? = null,
    val userPrograms: List<ProgramDto> = emptyList(),
    val systemPrograms: List<ProgramDto> = emptyList(),
    val trainerPrograms: List<ProgramDto> = emptyList(),
    val generatedProgramId: String? = null
)

@HiltViewModel
class ClientProgramsViewModel @Inject constructor(
    private val clientRepository: ClientRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClientProgramsUiState())
    val uiState: StateFlow<ClientProgramsUiState> = _uiState.asStateFlow()

    fun loadPrograms(clientId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            clientRepository.getClientPrograms(clientId)
                .onSuccess { response ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        userPrograms = response.userPrograms,
                        systemPrograms = response.systemPrograms,
                        trainerPrograms = response.trainerPrograms
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to load programs"
                    )
                }
        }
    }

    fun generateAiProgram(clientId: String, duration: String, focus: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isGenerating = true, error = null)

            clientRepository.generateAiProgram(clientId, duration, focus)
                .onSuccess { program ->
                    _uiState.value = _uiState.value.copy(
                        isGenerating = false,
                        generatedProgramId = program.programId
                    )
                    // Refresh the list
                    loadPrograms(clientId)
                }
                .onFailure { error ->
                     _uiState.value = _uiState.value.copy(
                        isGenerating = false,
                        error = error.message ?: "Failed to generate program"
                    )
                }
        }
    }
    
    fun clearGeneratedProgramId() {
        _uiState.value = _uiState.value.copy(generatedProgramId = null)
    }
}
