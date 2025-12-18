package com.ziro.fit.util

import android.util.Log
import com.ziro.fit.model.ApiError
import io.mockk.every
import io.mockk.mockkStatic
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class ApiErrorParserTest {

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.e(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>(), any<Throwable>()) } returns 0
        every { Log.w(any<String>(), any<String>()) } returns 0
    }

    @Test
    fun `parse IOException returns network error message`() {
        val exception = IOException("No internet")
        val error = ApiErrorParser.parse(exception)
        
        assertEquals("Network error. Please check your connection.", error.message)
        assertEquals(null, error.statusCode)
    }

    @Test
    fun `parse HttpException with 400 and JSON body returns parsed message`() {
        val json = """{"message": "Invalid data", "details": {"email": ["Invalid format"]}}"""
        val response = Response.error<Any>(400, json.toResponseBody("application/json".toMediaTypeOrNull()))
        val exception = HttpException(response)
        
        val error = ApiErrorParser.parse(exception)
        
        assertEquals("Invalid data", error.message)
        assertEquals(400, error.statusCode)
        assertNotNull(error.validationErrors)
        assertEquals(listOf("Invalid format"), error.validationErrors!!["email"])
    }

    @Test
    fun `parse HttpException with 500 and raw text returns raw text`() {
        val rawText = "Internal Server Error"
        val response = Response.error<Any>(500, rawText.toResponseBody("text/plain".toMediaTypeOrNull()))
        val exception = HttpException(response)
        
        val error = ApiErrorParser.parse(exception)
        
        assertEquals(rawText, error.message)
        assertEquals(500, error.statusCode)
    }

    @Test
    fun `parse HttpException with nested error object returns parsed message`() {
        val json = """{"error": {"message": "Unauthorized access"}}"""
        val response = Response.error<Any>(401, json.toResponseBody("application/json".toMediaTypeOrNull()))
        val exception = HttpException(response)
        
        val error = ApiErrorParser.parse(exception)
        
        assertEquals("Unauthorized access", error.message)
        assertEquals(401, error.statusCode)
    }

    @Test
    fun `formatValidationErrors correctly formats multiple errors`() {
        val validationErrors = mapOf(
            "email" to listOf("Invalid format", "Already taken"),
            "password" to listOf("Too short")
        )
        
        val formatted = ApiErrorParser.formatValidationErrors(validationErrors)
        
        val expected = "Email: Invalid format, Already taken\nPassword: Too short"
        assertEquals(expected, formatted)
    }
}
