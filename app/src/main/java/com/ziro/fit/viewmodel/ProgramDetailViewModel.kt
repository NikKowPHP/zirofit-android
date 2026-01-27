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

data class ProgramDetailUiState(
    val program: ProgramDto? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ProgramDetailViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
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
                _uiState.update { it.copy(program = program, isLoading = false) }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Program not found") }
            }
        }
    }
}
