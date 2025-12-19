package com.ziro.fit.model

data class Message(
    val id: String,
    val content: String,
    val isSystemMessage: Boolean,
    val senderId: String?,
    val conversationId: String,
    val mediaUrl: String?,
    val mediaType: String?, // "image" | "video"
    val workoutSessionId: String? = null,
    val createdAt: String,
    val readAt: String?
)

data class StartChatResponse(
    val conversationId: String,
    val messages: List<Message>
)

data class SendMessageRequest(
    val clientId: String,
    val trainerId: String,
    val senderId: String,
    val content: String,
    val mediaUrl: String? = null,
    val mediaType: String? = null
)
