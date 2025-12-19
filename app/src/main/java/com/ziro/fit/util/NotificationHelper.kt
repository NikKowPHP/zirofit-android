package com.ziro.fit.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.net.toUri
import com.ziro.fit.MainActivity
import com.ziro.fit.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val CHANNEL_ID = "chat_notifications"
        const val CHANNEL_NAME = "Chat Messages"
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for new messages from your trainer"
                enableVibration(true)
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    fun showChatNotification(
        conversationId: String,
        senderId: String,
        senderName: String,
        messageContent: String
    ) {
        // Create Deep Link Intent to open specific chat
        // Navigates to: chat/{clientId}/{trainerId}
        // We assume senderId is the trainerId for the client app context
        val deepLinkIntent = Intent(
            Intent.ACTION_VIEW,
            "zirofit://chat/client/$senderId".toUri(), // Simplified URI scheme, assuming client mode for now. TODO: Fix this for trainer mode if needed
            context,
            MainActivity::class.java
        )

        val pendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(deepLinkIntent)
            getPendingIntent(
                conversationId.hashCode(),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Use appropriate app icon
            .setContentTitle(senderName)
            .setContentText(messageContent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .build()

        val manager = context.getSystemService(NotificationManager::class.java)
        // Use senderId.hashCode as notification ID to group by sender, or distinct for every message
        manager.notify(conversationId.hashCode(), notification)
    }
}
