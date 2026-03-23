package com.ziro.fit.util

import android.util.Log
import com.ziro.fit.model.ApiError
import io.mockk.every
import io.mockk.mockkStatic
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class ApiErrorParserEdgeCasesTest {
    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.e(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>(), any<Throwable>()) } returns 0
        every { Log.w(any<String>(), any<String>()) } returns 0
    }

    @Test
    fun `parse HttpException 404 with empty body returns default message`() {
        val response = Response.error<Any>(404, "".toResponseBody(null))
        val exception = HttpException(response)

        val error = ApiErrorParser.parse(exception)

        assertEquals("Resource not found.", error.message)
        assertEquals(404, error.statusCode)
    }

    @Test
    fun `parse HttpException 404 with JSON body returns parsed message`() {
        val json = """{"message": "User not found"}"""
        val response = Response.error<Any>(404, json.toResponseBody("application/json".toMediaTypeOrNull()))
        val exception = HttpException(response)

        val error = ApiErrorParser.parse(exception)

        assertEquals("User not found", error.message)
        assertEquals(404, error.statusCode)
    }

    @Test
    fun `parse HttpException 404 with raw text body returns raw text`() {
        val rawText = "Not Found: /api/users/999"
        val response = Response.error<Any>(404, rawText.toResponseBody("text/plain".toMediaTypeOrNull()))
        val exception = HttpException(response)

        val error = ApiErrorParser.parse(exception)

        assertEquals(rawText, error.message)
        assertEquals(404, error.statusCode)
    }

    @Test
    fun `parse HttpException 400 with empty body returns default message`() {
        val response = Response.error<Any>(400, "".toResponseBody(null))
        val exception = HttpException(response)

        val error = ApiErrorParser.parse(exception)

        assertEquals("Invalid request. Please check your input.", error.message)
        assertEquals(400, error.statusCode)
    }

    @Test
    fun `parse HttpException 401 with empty body returns default message`() {
        val response = Response.error<Any>(401, "".toResponseBody(null))
        val exception = HttpException(response)

        val error = ApiErrorParser.parse(exception)

        assertEquals("Authentication required. Please log in.", error.message)
        assertEquals(401, error.statusCode)
    }

    @Test
    fun `parse HttpException 403 with empty body returns default message`() {
        val response = Response.error<Any>(403, "".toResponseBody(null))
        val exception = HttpException(response)

        val error = ApiErrorParser.parse(exception)

        assertEquals("You don't have permission to perform this action.", error.message)
        assertEquals(403, error.statusCode)
    }

    @Test
    fun `parse HttpException 409 with empty body returns default message`() {
        val response = Response.error<Any>(409, "".toResponseBody(null))
        val exception = HttpException(response)

        val error = ApiErrorParser.parse(exception)

        assertEquals("A conflict occurred. Please try again.", error.message)
        assertEquals(409, error.statusCode)
    }

    @Test
    fun `parse HttpException 422 with empty body returns default message`() {
        val response = Response.error<Any>(422, "".toResponseBody(null))
        val exception = HttpException(response)

        val error = ApiErrorParser.parse(exception)

        assertEquals("Validation failed. Please check your input.", error.message)
        assertEquals(422, error.statusCode)
    }

    @Test
    fun `parse HttpException 429 with empty body returns default message`() {
        val response = Response.error<Any>(429, "".toResponseBody(null))
        val exception = HttpException(response)

        val error = ApiErrorParser.parse(exception)

        assertEquals("Too many requests. Please try again later.", error.message)
        assertEquals(429, error.statusCode)
    }

    @Test
    fun `parse HttpException 500 with empty body returns default message`() {
        val response = Response.error<Any>(500, "".toResponseBody(null))
        val exception = HttpException(response)

        val error = ApiErrorParser.parse(exception)

        assertEquals("Server error. Please try again later.", error.message)
        assertEquals(500, error.statusCode)
    }

    @Test
    fun `parse HttpException 503 with empty body returns default message`() {
        val response = Response.error<Any>(503, "".toResponseBody(null))
        val exception = HttpException(response)

        val error = ApiErrorParser.parse(exception)

        assertEquals("Service temporarily unavailable.", error.message)
        assertEquals(503, error.statusCode)
    }

    @Test
    fun `parse HttpException with malformed JSON body returns raw body`() {
        val malformedJson = "{invalid json"
        val response = Response.error<Any>(400, malformedJson.toResponseBody("application/json".toMediaTypeOrNull()))
        val exception = HttpException(response)

        val error = ApiErrorParser.parse(exception)

        assertEquals(malformedJson, error.message)
        assertEquals(400, error.statusCode)
    }

    @Test
    fun `parse unexpected RuntimeException returns fallback message`() {
        val exception = RuntimeException("Something went wrong")

        val error = ApiErrorParser.parse(exception)

        assertEquals("Something went wrong", error.message)
        assertEquals(null, error.statusCode)
    }

    @Test
    fun `parse unexpected exception with null message returns default error`() {
        val exception = Exception(null as String?)

        val error = ApiErrorParser.parse(exception)

        assertEquals("An unexpected error occurred", error.message)
        assertEquals(null, error.statusCode)
    }

    @Test
    fun `getErrorMessage with ApiError no validationErrors returns just message`() {
        val apiError = ApiError(message = "Something went wrong", statusCode = 500)

        val message = ApiErrorParser.getErrorMessage(apiError)

        assertEquals("Something went wrong", message)
    }

    @Test
    fun `getErrorMessage with ApiError validationErrors returns message plus formatted errors`() {
        val validationErrors = mapOf("email" to listOf("Invalid format"))
        val apiError = ApiError(message = "Validation failed", statusCode = 422, validationErrors = validationErrors)

        val message = ApiErrorParser.getErrorMessage(apiError)

        assertTrue(message.contains("Validation failed"))
        assertTrue(message.contains("Email: Invalid format"))
    }

    @Test
    fun `formatValidationErrors with single field single error formats correctly`() {
        val validationErrors = mapOf("email" to listOf("Email is required"))

        val formatted = ApiErrorParser.formatValidationErrors(validationErrors)

        assertEquals("Email: Email is required", formatted)
    }

    @Test
    fun `formatValidationErrors with empty map returns empty string`() {
        val validationErrors = emptyMap<String, List<String>>()

        val formatted = ApiErrorParser.formatValidationErrors(validationErrors)

        assertEquals("", formatted)
    }

    @Test
    fun `formatValidationErrors capitalizes first letter of field names`() {
        val validationErrors = mapOf(
            "name" to listOf("Required"),
            "email" to listOf("Invalid"),
            "password" to listOf("Too short")
        )

        val formatted = ApiErrorParser.formatValidationErrors(validationErrors)

        assertTrue(formatted.contains("Name: Required"))
        assertTrue(formatted.contains("Email: Invalid"))
        assertTrue(formatted.contains("Password: Too short"))
    }

    @Test
    fun `formatValidationErrors with empty list produces trailing space`() {
        val validationErrors = mapOf("user" to emptyList<String>())

        val formatted = ApiErrorParser.formatValidationErrors(validationErrors)

        assertEquals("User: ", formatted)
    }
}
