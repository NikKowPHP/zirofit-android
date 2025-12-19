package com.ziro.fit.service

import android.util.Log
import com.ziro.fit.data.repository.ChatRepository
import com.ziro.fit.util.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GlobalChatManager @Inject constructor(
    private val chatRepository: ChatRepository,
    private val notificationHelper: NotificationHelper
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var listenJob: Job? = null
    private var currentUserId: String? = null
    
    // Track the currently open conversation ID to avoid notifying for the active chat
    var activeConversationId: String? = null

    fun initialize() {
        if (listenJob != null) return // Already running

        scope.launch {
            // 1. Get Current User ID to filter own messages
            chatRepository.getCurrentUserId().onSuccess { userId ->
                currentUserId = userId
                startListening()
            }.onFailure {
                Log.e("GlobalChatManager", "Failed to get user ID for notifications")
            }
        }
    }

    private suspend fun startListening() {
        listenJob?.cancel()
        listenJob = scope.launch {
            chatRepository.listenToGlobalMessages()
                .catch { e -> Log.e("GlobalChatManager", "Error in global chat listener", e) }
                .collect { message ->
                    handleNewMessage(message)
                }
        }
    }

    private fun handleNewMessage(message: com.ziro.fit.model.Message) {
        val myId = currentUserId ?: return

        // 1. Ignore my own messages
        if (message.senderId == myId) return

        // 2. Ignore messages if I am currently looking at this conversation
        if (activeConversationId == message.conversationId) return

        // 3. Ignore system messages if desired (optional)
        if (message.isSystemMessage) return

        // 4. Trigger Notification
        // Ideally we would fetch the sender's name via an ID lookup, 
        // but for now we might use a generic "New Message" or store a cache of trainer info.
        val senderName = "New Message" // TODO: Cache trainer names in Repo
        
        notificationHelper.showChatNotification(
            conversationId = message.conversationId,
            senderId = message.senderId ?: "unknown",
            senderName = senderName,
            messageContent = message.content
        )
    }

    fun onChatOpened(conversationId: String) {
        activeConversationId = conversationId
    }

    fun onChatClosed() {
        activeConversationId = null
    }

    fun clear() {
        listenJob?.cancel()
        listenJob = null
        currentUserId = null
    }
}
