package com.ziro.fit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziro.fit.data.repository.ClientRepository
import com.ziro.fit.model.ClientSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ClientSessionsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val sessions: List<ClientSession> = emptyList()
)

@HiltViewModel
class ClientSessionsViewModel @Inject constructor(
    private val clientRepository: ClientRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClientSessionsUiState())
    val uiState: StateFlow<ClientSessionsUiState> = _uiState.asStateFlow()

    fun loadSessions(clientId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            clientRepository.getClientSessions(clientId)
                .onSuccess { sessions ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        sessions = sessions
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to load sessions"
                    )
                }
        }
    }
    fun updateSession(clientId: String, sessionId: String, notes: String?, status: String?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = clientRepository.updateSession(clientId, sessionId, notes, status)
            result.onSuccess {
                loadSessions(clientId)
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to update session"
                )
            }
        }
    }

    fun deleteSession(clientId: String, sessionId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = clientRepository.deleteSession(clientId, sessionId)
            result.onSuccess {
                loadSessions(clientId)
            }.onFailure { e ->
                 _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to delete session"
                )
            }
        }
    }
}
