package com.ziro.fit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziro.fit.data.repository.ClientRepository
import com.ziro.fit.model.Client
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ClientsUiState(
    val clients: List<Client> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ClientsViewModel @Inject constructor(
    private val clientRepository: ClientRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClientsUiState())
    val uiState: StateFlow<ClientsUiState> = _uiState.asStateFlow()

    init {
        fetchClients()
    }

    fun fetchClients() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = clientRepository.getClients()
            result.onSuccess { clients ->
                _uiState.value = _uiState.value.copy(
                    clients = clients,
                    isLoading = false
                )
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to fetch clients"
                )
            }
        }
    }

    fun deleteClient(clientId: String) {
        viewModelScope.launch {
            // Optimistic update could be done here, but safe bet is to just load
            // Or remove from list immediately then sync
            val currentList = _uiState.value.clients
            val updatedList = currentList.filter { it.id != clientId }
            _uiState.value = _uiState.value.copy(clients = updatedList)

            val result = clientRepository.deleteClient(clientId)
            if (result.isFailure) {
                // Revert or show error
                // For now, just re-fetch to ensure state consistency
                fetchClients()
                _uiState.value = _uiState.value.copy(error = "Failed to delete client")
            }
        }
    }

    fun createClient(name: String, email: String, phone: String?, status: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = clientRepository.createClient(name, email, phone, status)
            result.onSuccess {
                fetchClients() // Refresh list to get new client
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to create client"
                )
            }
        }
    }
}
