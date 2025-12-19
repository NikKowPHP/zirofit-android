package com.ziro.fit.data.repository

import com.ziro.fit.data.remote.ZiroApi
import com.ziro.fit.model.Message
import com.ziro.fit.model.SendMessageRequest
import com.ziro.fit.model.StartChatResponse
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.postgrest.query.filter.FilterOperation
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val api: ZiroApi,
    private val supabase: SupabaseClient
) {

    suspend fun getChatHistory(clientId: String, trainerId: String): Result<StartChatResponse> {
        return try {
            val response = api.getChatHistory(clientId, trainerId)
            if (response.success == true && response.data != null) {
                 Result.success(response.data)
            } else {
                 Result.failure(Exception(response.message ?: response.error ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendMessage(request: SendMessageRequest): Result<Unit> {
        return try {
            val response = api.sendMessage(request)
            if (response.success == true) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message ?: response.error))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun connectToChat(conversationId: String): Flow<Message> {
        val channel = supabase.channel("chat:$conversationId")
        channel.subscribe()
        
        return channel.postgresChangeFlow<PostgresAction.Insert>(schema = "public") {
            table = "messages"
            filter(FilterOperation("conversation_id", FilterOperator.EQ, conversationId))
        }.map { action ->
             mapToMessage(action.record)
        }
    }

    private fun mapToMessage(data: Map<String, Any>): Message {
        return Message(
            id = data["id"].toString(),
            content = data["content"].toString(),
            isSystemMessage = data["is_system_message"].toString().toBoolean(),
            senderId = data["sender_id"]?.toString(),
            conversationId = data["conversation_id"].toString(),
            mediaUrl = data["media_url"]?.toString(),
            mediaType = data["media_type"]?.toString(),
            workoutSessionId = data["workout_session_id"]?.toString(),
            createdAt = data["created_at"].toString(),
            readAt = data["read_at"]?.toString()
        )
    }

    suspend fun getCurrentUserId(): Result<String> {
        return try {
            val response = api.getMe()
            // api/auth/me might not return success=true explicitly, so we check data
            if (response.data != null) {
                Result.success(response.data.id)
            } else {
                Result.failure(Exception("Failed to get user"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
