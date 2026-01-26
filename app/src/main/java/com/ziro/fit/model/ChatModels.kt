package com.ziro.fit.model

data class Message(
    val id: String,
    val content: String,
    val isSystemMessage: Boolean,
    val senderId: String?,
    val conversationId: String,
    val mediaUrl: String?,
    val mediaType: String?, // "text" | "image" | "video" | "workout_plan"
    val workoutSessionId: String? = null,
    val createdAt: String,
    val readAt: String?
) {
    companion object {
        const val TYPE_TEXT = "text"
        const val TYPE_IMAGE = "image"
        const val TYPE_VIDEO = "video"
        const val TYPE_WORKOUT_PLAN = "workout_plan"
    }
}

data class ClientStartWorkoutRequest(
    val templateId: String
)

data class AiGenerationRequest(
    val clientId: String,
    val userIntent: String
)

data class WorkoutGenerationResponse(
    val result: WorkoutPlan,
    val serviceUsed: String
)

data class WorkoutPlan(
    val name: String,
    val focus: String,
    val reasoning: String,
    val exercises: List<PlanExercise>
)

data class WorkoutPlanContent(
    val name: String,
    val focus: String,
    val reasoning: String,
    val exercises: List<PlanExercise>,
    val templateId: String? = null // Nullable if coming from API response without ID, but usually present in Chat
)

data class PlanExercise(
    val name: String,
    val sets: Int,
    val reps: String,
    val rpe: Int? = null,
    val rest: Int,
    val notes: String?
)

data class StartChatResponse(
    val conversationId: String?,
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
