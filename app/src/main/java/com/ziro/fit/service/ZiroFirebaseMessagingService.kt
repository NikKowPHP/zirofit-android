package com.ziro.fit.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ziro.fit.data.repository.ProfileRepository
import com.ziro.fit.util.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ZiroFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var notificationHelper: NotificationHelper

    @Inject
    lateinit var profileRepository: ProfileRepository

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Refreshed token: $token")
        
        // Send the new token to your backend so it knows where to send notifications
        scope.launch {
            try {
                profileRepository.registerPushToken(token)
            } catch (e: Exception) {
                Log.e("FCM", "Failed to send token to backend", e)
            }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        // Handle data payload (preferred for customization)
        if (remoteMessage.data.isNotEmpty()) {
            val type = remoteMessage.data["type"]
            
            if (type == "chat_message") {
                val conversationId = remoteMessage.data["conversationId"] ?: return
                val senderId = remoteMessage.data["senderId"] ?: "unknown"
                val senderName = remoteMessage.data["senderName"] ?: "New Message"
                val content = remoteMessage.data["content"] ?: "You have a new message"
                
                // Show notification using our helper
                // Note: You might want to check here if the app is in the foreground and the specific chat is open
                // using GlobalChatManager singleton to decide whether to show or not.
                notificationHelper.showChatNotification(
                    conversationId = conversationId,
                    senderId = senderId,
                    senderName = senderName,
                    messageContent = content
                )
            }
        }

        // Handle notification payload (if sent by backend as a display notification)
        remoteMessage.notification?.let {
            // If data payload was missing, we can fallback here, but typically data is sufficient.
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
