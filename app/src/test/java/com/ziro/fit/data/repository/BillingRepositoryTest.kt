package com.ziro.fit.data.repository

import android.util.Log
import com.ziro.fit.data.remote.ZiroApi
import com.ziro.fit.model.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class BillingRepositoryTest {
    private val api: ZiroApi = mockk(relaxed = true)
    private val repository = BillingRepository(api)

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.e(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>(), any<Throwable>()) } returns 0
    }

    @Test
    fun `createCheckoutSession for event success returns checkout URL`() = runBlocking {
        val checkoutUrl = "https://checkout.stripe.com/pay_test123"
        val response = ApiResponse(
            success = true,
            data = CreateCheckoutSessionResponse(url = checkoutUrl)
        )
        coEvery { api.createCheckoutSession(any<CreateCheckoutSessionRequest>()) } returns response

        val result = repository.createCheckoutSession(eventId = "evt1", type = "event")

        assertTrue(result.isSuccess)
        assertEquals(checkoutUrl, result.getOrNull())
    }

    @Test
    fun `createCheckoutSession failure returns Result failure`() = runBlocking {
        coEvery { api.createCheckoutSession(any<CreateCheckoutSessionRequest>()) } throws
            RuntimeException("Network Error")

        val result = repository.createCheckoutSession(eventId = "evt1", type = "event")

        assertTrue(result.isFailure)
        assertEquals("Network Error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `createCheckoutSession null data returns Result failure`() = runBlocking {
        val response = ApiResponse<CreateCheckoutSessionResponse>(
            success = true,
            data = null
        )
        coEvery { api.createCheckoutSession(any<CreateCheckoutSessionRequest>()) } returns response

        val result = repository.createCheckoutSession(eventId = "evt1", type = "event")

        assertTrue(result.isFailure)
    }

    @Test
    fun `createCheckoutSession API returns success false returns Result failure`() = runBlocking {
        val response = ApiResponse<CreateCheckoutSessionResponse>(
            success = false,
            data = null,
            message = "Failed to initiate checkout"
        )
        coEvery { api.createCheckoutSession(any<CreateCheckoutSessionRequest>()) } returns response

        val result = repository.createCheckoutSession(eventId = "evt1", type = "event")

        assertTrue(result.isFailure)
        assertEquals("Failed to initiate checkout", result.exceptionOrNull()?.message)
    }

    @Test
    fun `createCheckoutSession passes correct type and eventId`() = runBlocking {
        val response = ApiResponse(
            success = true,
            data = CreateCheckoutSessionResponse(url = "https://checkout.stripe.com")
        )
        coEvery { api.createCheckoutSession(any<CreateCheckoutSessionRequest>()) } returns response

        repository.createCheckoutSession(eventId = "evt123", type = "event")

        coVerify {
            api.createCheckoutSession(
                match { request ->
                    request.eventId == "evt123" &&
                    request.type == "event" &&
                    request.isMobile == true
                }
            )
        }
    }

    @Test
    fun `createCheckoutSession for package with packageId`() = runBlocking {
        val response = ApiResponse(
            success = true,
            data = CreateCheckoutSessionResponse(url = "https://checkout.stripe.com")
        )
        coEvery { api.createCheckoutSession(any<CreateCheckoutSessionRequest>()) } returns response

        val result = repository.createCheckoutSession(packageId = "pkg123", type = "package")

        assertTrue(result.isSuccess)
        coVerify {
            api.createCheckoutSession(
                match { request ->
                    request.packageId == "pkg123" &&
                    request.type == "package"
                }
            )
        }
    }
}
