package com.ziro.fit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziro.fit.data.repository.ClientRepository
import com.ziro.fit.model.TransformationPhoto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ClientPhotosUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val photos: List<TransformationPhoto> = emptyList()
)

@HiltViewModel
class ClientPhotosViewModel @Inject constructor(
    private val clientRepository: ClientRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClientPhotosUiState())
    val uiState: StateFlow<ClientPhotosUiState> = _uiState.asStateFlow()

    fun loadPhotos(clientId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            clientRepository.getClientPhotos(clientId)
                .onSuccess { photos ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        photos = photos
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to load photos"
                    )
                }
        }
    }
}
