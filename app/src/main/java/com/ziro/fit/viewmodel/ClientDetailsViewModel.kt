package com.ziro.fit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziro.fit.data.repository.ClientProfileData
import com.ziro.fit.data.repository.ClientRepository
import com.ziro.fit.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ClientDetailsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val client: Client? = null,
    val measurements: List<Measurement> = emptyList(),
    val assessments: List<AssessmentResult> = emptyList(),
    val photos: List<TransformationPhoto> = emptyList(),
    val sessions: List<ClientSession> = emptyList()
)

@HiltViewModel
class ClientDetailsViewModel @Inject constructor(
    private val clientRepository: ClientRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClientDetailsUiState())
    val uiState: StateFlow<ClientDetailsUiState> = _uiState.asStateFlow()

    fun loadClientProfile(clientId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            val result = clientRepository.getClientProfile(clientId)
            
            result.onSuccess { profileData ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    client = profileData.client,
                    measurements = profileData.measurements,
                    assessments = profileData.assessments,
                    photos = profileData.photos,
                    sessions = profileData.sessions
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = error.message ?: "Failed to load client profile"
                )
            }
        }
    }

    fun refresh(clientId: String) {
        loadClientProfile(clientId)
    }

    fun updateClient(clientId: String, name: String, email: String, phone: String?, status: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = clientRepository.updateClient(clientId, name, email, phone, status)
            result.onSuccess {
                loadClientProfile(clientId) // Refresh profile
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to update client"
                )
            }
        }
    }
    
    fun deleteClient(clientId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = clientRepository.deleteClient(clientId)
            result.onSuccess {
                onSuccess()
            }.onFailure { e ->
                 _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to delete client"
                )
            }
        }
    }
}
