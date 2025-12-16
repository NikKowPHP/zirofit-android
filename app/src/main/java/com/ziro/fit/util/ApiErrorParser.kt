package com.ziro.fit.util

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.ziro.fit.model.ApiError
import com.ziro.fit.model.ApiErrorResponse
import retrofit2.HttpException
import java.io.IOException

/**
 * Utility object for parsing API errors
 */
object ApiErrorParser {
    
    private const val TAG = "ApiErrorParser"
    private val gson = Gson()
    
    /**
     * Parse an exception into a user-friendly ApiError
     */
    fun parseError(throwable: Throwable): ApiError {
        Log.e(TAG, "════════════════════════════════════════")
        Log.e(TAG, "API ERROR CAUGHT")
        Log.e(TAG, "Exception Type: ${throwable.javaClass.simpleName}")
        Log.e(TAG, "Message: ${throwable.message}")
        
        return when (throwable) {
            is HttpException -> {
                Log.e(TAG, "HTTP Exception - Status Code: ${throwable.code()}")
                parseHttpException(throwable)
            }
            is IOException -> {
                Log.e(TAG, "Network IOException", throwable)
                ApiError(
                    message = "Network error. Please check your connection.",
                    statusCode = null
                )
            }
            else -> {
                Log.e(TAG, "Unexpected Exception", throwable)
                ApiError(
                    message = throwable.message ?: "An unexpected error occurred",
                    statusCode = null
                )
            }
        }.also {
            Log.e(TAG, "Parsed Error Message: ${it.message}")
            it.validationErrors?.let { errors ->
                Log.e(TAG, "Validation Errors:")
                errors.forEach { (field, messages) ->
                    Log.e(TAG, "  - $field: ${messages.joinToString(", ")}")
                }
            }
            Log.e(TAG, "════════════════════════════════════════")
        }
    }
    
    /**
     * Parse HTTP exception to extract error details
     */
    private fun parseHttpException(exception: HttpException): ApiError {
        val errorBody = exception.response()?.errorBody()?.string()
        val statusCode = exception.code()
        
        Log.e(TAG, "HTTP Error Details:")
        Log.e(TAG, "  Status Code: $statusCode")
        Log.e(TAG, "  URL: ${exception.response()?.raw()?.request?.url}")
        Log.e(TAG, "  Method: ${exception.response()?.raw()?.request?.method}")
        Log.e(TAG, "  Error Body: ${errorBody ?: "(empty)"}")
        
        if (errorBody.isNullOrBlank()) {
            val defaultMessage = getDefaultErrorMessage(statusCode)
            Log.w(TAG, "  No error body, using default message: $defaultMessage")
            return ApiError(
                message = defaultMessage,
                statusCode = statusCode
            )
        }
        
        return try {
            // First try to parse as a nested error structure {"error": {...}}
            val jsonElement = gson.fromJson(errorBody, com.google.gson.JsonElement::class.java)
            val errorData = if (jsonElement.isJsonObject) {
                val rootObj = jsonElement.asJsonObject
                // Check if error data is nested under "error" key
                if (rootObj.has("error") && rootObj.get("error").isJsonObject) {
                    rootObj.getAsJsonObject("error")
                } else {
                    rootObj
                }
            } else {
                jsonElement
            }
            
            val errorResponse = gson.fromJson(errorData, ApiErrorResponse::class.java)
            
            Log.e(TAG, "  Parsed Error Response:")
            Log.e(TAG, "    Message: ${errorResponse.message ?: "(null)"}")
            Log.e(TAG, "    Details: ${errorResponse.details}")
            
            // Try to extract validation errors if present
            val validationErrors = extractValidationErrors(errorResponse.details)
            
            // Use message from response or fallback to default
            val errorMessage = errorResponse.message 
                ?: validationErrors?.let { "Validation failed: ${formatValidationErrors(it)}" }
                ?: getDefaultErrorMessage(statusCode)
            
            ApiError(
                message = errorMessage,
                statusCode = statusCode,
                validationErrors = validationErrors
            )
        } catch (e: Exception) {
            Log.e(TAG, "  Failed to parse error body as JSON", e)
            Log.e(TAG, "  Using raw body as error message")
            // If parsing fails, return the raw body or default message
            val fallbackMessage = if (errorBody.length <= 200) {
                errorBody
            } else {
                "${errorBody.take(197)}..."
            }
            ApiError(
                message = fallbackMessage,
                statusCode = statusCode
            )
        }
    }
    
    /**
     * Extract validation errors from details object
     */
    private fun extractValidationErrors(details: Any?): Map<String, List<String>>? {
        if (details == null) return null
        
        return try {
            when (details) {
                is Map<*, *> -> {
                    val errors = mutableMapOf<String, List<String>>()
                    details.forEach { (key, value) ->
                        if (key is String) {
                            when (value) {
                                is List<*> -> {
                                    val messages = value.filterIsInstance<String>()
                                    if (messages.isNotEmpty()) {
                                        errors[key] = messages
                                    }
                                }
                                is Map<*, *> -> {
                                    // Handle nested validation format like Zod errors
                                    val errorMessages = extractNestedErrors(value)
                                    if (errorMessages.isNotEmpty()) {
                                        errors[key] = errorMessages
                                    }
                                }
                                is String -> {
                                    errors[key] = listOf(value)
                                }
                            }
                        }
                    }
                    errors.ifEmpty { null }
                }
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Extract error messages from nested validation structures
     */
    private fun extractNestedErrors(nestedMap: Map<*, *>): List<String> {
        val errors = mutableListOf<String>()
        
        // Look for _errors field (Zod format)
        (nestedMap["_errors"] as? List<*>)?.let { errorList ->
            errorList.filterIsInstance<String>().forEach { errors.add(it) }
        }
        
        // Look for message field
        (nestedMap["message"] as? String)?.let { errors.add(it) }
        
        return errors
    }
    
    /**
     * Get user-friendly error message based on status code
     */
    private fun getDefaultErrorMessage(statusCode: Int): String {
        return when (statusCode) {
            400 -> "Invalid request. Please check your input."
            401 -> "Authentication required. Please log in."
            403 -> "You don't have permission to perform this action."
            404 -> "Resource not found."
            409 -> "A conflict occurred. Please try again."
            422 -> "Validation failed. Please check your input."
            429 -> "Too many requests. Please try again later."
            500 -> "Server error. Please try again later."
            503 -> "Service temporarily unavailable."
            else -> "An error occurred. Please try again."
        }
    }
    
    /**
     * Format validation errors into a readable message
     */
    fun formatValidationErrors(validationErrors: Map<String, List<String>>): String {
        return validationErrors.entries.joinToString("\n") { (field, errors) ->
            val fieldName = field.replaceFirstChar { it.uppercase() }
            "$fieldName: ${errors.joinToString(", ")}"
        }
    }
    
    /**
     * Get a complete user-friendly error message
     */
    fun getErrorMessage(apiError: ApiError): String {
        return buildString {
            append(apiError.message)
            
            apiError.validationErrors?.let { validationErrors ->
                if (validationErrors.isNotEmpty()) {
                    append("\n\n")
                    append(formatValidationErrors(validationErrors))
                }
            }
        }
    }
}
