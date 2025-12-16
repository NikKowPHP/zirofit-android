package com.ziro.fit.model

import com.google.gson.annotations.SerializedName

/**
 * Request model for creating a new calendar session
 */
data class CreateSessionRequest(
    val clientId: String,
    val startTime: String, // ISO 8601 format
    val endTime: String,   // ISO 8601 format
    val notes: String? = null,
    val templateId: String? = null,
    val repeats: Boolean = false,
    val repeatWeeks: Int? = null,
    @SerializedName("repeatDays") val repeatDays: String? = null // Comma-separated day numbers or names
)

/**
 * Response model for session creation
 */
data class CreateSessionResponse(
    val message: String
)
