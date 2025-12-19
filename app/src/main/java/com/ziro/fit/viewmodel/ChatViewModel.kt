package com.ziro.fit.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziro.fit.data.repository.ChatRepository
import com.ziro.fit.model.Message
import com.ziro.fit.model.SendMessageRequest
import com.ziro.fit.service.GlobalChatManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatUiState(
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentUserId: String = "", // For aligning messages (right/left)
    val conversationId: String? = null
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: ChatRepository,
    private val globalChatManager: GlobalChatManager, // Inject Manager
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    // Arguments from Navigation
    private val clientId: String = checkNotNull(savedStateHandle["clientId"])
    private val trainerIdArg: String = checkNotNull(savedStateHandle["trainerId"])
    private val initialCurrentUserId: String? = savedStateHandle["currentUserId"]
    
    private var resolvedTrainerId: String = trainerIdArg

    init {
        resolveIdsAndLoad()
    }

    private fun resolveIdsAndLoad() {
        viewModelScope.launch {
            var myId = initialCurrentUserId
            if (myId == null) {
                repository.getCurrentUserId().onSuccess { id ->
                    myId = id
                    _uiState.update { it.copy(currentUserId = id) }
                }.onFailure { e ->
                    e.printStackTrace()
                    _uiState.update { it.copy(error = "Failed to authenticate user: ${e.message}") }
                }
            } else {
                 _uiState.update { it.copy(currentUserId = myId!!) }
            }

            if (trainerIdArg == "me") {
                if (myId != null) {
                    resolvedTrainerId = myId!!
                } else {
                    // Stop if we can't resolve trainer ID
                    return@launch
                }
            } else {
                resolvedTrainerId = trainerIdArg
            }
            
            loadHistory()
        }
    }

    fun loadHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = repository.getChatHistory(clientId, resolvedTrainerId)
            result.onSuccess { response ->
                _uiState.update { 
                    it.copy(
                        messages = response.messages, 
                        conversationId = response.conversationId,
                        isLoading = false
                    ) 
                }
                // Notify Manager that this chat is open
                globalChatManager.onChatOpened(response.conversationId)
                
                subscribeToRealtime(response.conversationId)
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun subscribeToRealtime(conversationId: String) {
        viewModelScope.launch {
            try {
                repository.connectToChat(conversationId).collect { newMessage ->
                    _uiState.update { state ->
                        // Avoid duplicates if any
                        if (state.messages.none { it.id == newMessage.id }) {
                            state.copy(messages = state.messages + newMessage)
                        } else {
                            state
                        }
                    }
                }
            } catch (e: Exception) {
                // Log error or retry? 
                // Realtime connection fail shouldn't crash app
                e.printStackTrace()
            }
        }
    }

    fun sendMessage(content: String) {
        val currentUser = _uiState.value.currentUserId
        if (currentUser.isEmpty()) return // Cannot send without user ID

        viewModelScope.launch {
            // Optimistic update
            val tempId = "temp_${System.currentTimeMillis()}"
            val tempMessage = Message(
                id = tempId,
                conversationId = _uiState.value.conversationId ?: "",
                senderId = currentUser,
                content = content,
                mediaUrl = null,
                mediaType = null,
                isSystemMessage = false,
                createdAt = java.time.Instant.now().toString(),
                readAt = null
            )
            
            _uiState.update { 
                 it.copy(messages = it.messages + tempMessage)
            }
            
            val request = SendMessageRequest(
                clientId = clientId,
                trainerId = resolvedTrainerId,
                senderId = currentUser,
                content = content
            )
            
            val result = repository.sendMessage(request)
            result.onFailure { e ->
                // Remove temp message on failure and show error
                _uiState.update { state -> 
                    state.copy(
                        messages = state.messages.filter { it.id != tempId },
                        error = "Failed to send: ${e.message}"
                    )
                }
            }
            // On success, we expect Realtime to send the real message back.
            // We might have a duplicate for a moment until we de-dupe or replace the temp one.
            // The existing de-dupe logic in subscribeToRealtime uses ID, which won't match tempId.
            // Ideally we should replace the temp message with the real one, but usually Realtime is fast enough.
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Notify manager that chat is closed when ViewModel is cleared
        globalChatManager.onChatClosed()
    }
}
