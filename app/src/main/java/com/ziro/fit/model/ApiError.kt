package com.ziro.fit.model

import com.google.gson.annotations.SerializedName

/**
 * Error response model for API errors
 * Matches the backend error format
 */
data class ApiErrorResponse(
    val message: String?,  // Nullable because backend might send null
    val details: Any? = null,  // Can be validation details or additional context
    val statusCode: Int? = null
)

/**
 * Represents a parsed API error with user-friendly message
 */
data class ApiError(
    val message: String,
    val statusCode: Int? = null,
    val validationErrors: Map<String, List<String>>? = null
)
