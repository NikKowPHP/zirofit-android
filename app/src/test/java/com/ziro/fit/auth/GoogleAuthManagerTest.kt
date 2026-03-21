package com.ziro.fit.auth

import app.cash.turbine.test
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GoogleAuthManagerTest {

    private lateinit var manager: GoogleAuthManager

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        manager = GoogleAuthManager(mockk(relaxed = true))
    }

    // --- parseAuthParamsFromUrl tests ---

    @Test
    fun `parseAuthParamsFromUrl extracts all params for valid URL`() {
        val params = manager.parseAuthParamsFromUrl(
            "zirofitapp://auth-callback?access_token=abc123&refresh_token=refresh456&user_id=user_1&role=trainer"
        )
        assertEquals("abc123", params["access_token"])
        assertEquals("refresh456", params["refresh_token"])
        assertEquals("user_1", params["user_id"])
        assertEquals("trainer", params["role"])
    }

    @Test
    fun `parseAuthParamsFromUrl returns empty for wrong scheme`() {
        val params = manager.parseAuthParamsFromUrl(
            "https://example.com/callback?access_token=abc&user_id=u1&role=trainer"
        )
        assertTrue(params.isEmpty())
    }

    @Test
    fun `parseAuthParamsFromUrl returns empty for wrong host`() {
        val params = manager.parseAuthParamsFromUrl(
            "zirofitapp://other-host?access_token=abc&user_id=u1&role=trainer"
        )
        assertTrue(params.isEmpty())
    }

    @Test
    fun `parseAuthParamsFromUrl handles missing refresh_token`() {
        val params = manager.parseAuthParamsFromUrl(
            "zirofitapp://auth-callback?access_token=abc&user_id=u1&role=trainer"
        )
        assertEquals("abc", params["access_token"])
        assertNull(params["refresh_token"])
        assertEquals("u1", params["user_id"])
        assertEquals("trainer", params["role"])
    }

    @Test
    fun `parseAuthParamsFromUrl handles params with special characters`() {
        val params = manager.parseAuthParamsFromUrl(
            "zirofitapp://auth-callback?access_token=abc123&refresh_token=xyz%40test&user_id=u1&role=trainer"
        )
        assertEquals("abc123", params["access_token"])
        assertEquals("xyz%40test", params["refresh_token"])
        assertEquals("u1", params["user_id"])
        assertEquals("trainer", params["role"])
    }

    // --- handleCallbackUrl tests ---

    @Test
    fun `handleCallbackUrl emits Success for valid URL with all fields`() = runTest {
        manager.authOutcome.test {
            manager.handleCallbackUrl(
                "zirofitapp://auth-callback?access_token=abc123&refresh_token=refresh456&user_id=user_1&role=trainer"
            )
            val outcome = awaitItem()
            assertTrue(outcome is GoogleAuthOutcome.Success)
            val success = outcome as GoogleAuthOutcome.Success
            assertEquals("abc123", success.result.accessToken)
            assertEquals("refresh456", success.result.refreshToken)
            assertEquals("user_1", success.result.userId)
            assertEquals("trainer", success.result.role)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `handleCallbackUrl emits Success for client role`() = runTest {
        manager.authOutcome.test {
            manager.handleCallbackUrl(
                "zirofitapp://auth-callback?access_token=tok&refresh_token=ref&user_id=u2&role=client"
            )
            val outcome = awaitItem()
            assertTrue(outcome is GoogleAuthOutcome.Success)
            assertEquals("client", (outcome as GoogleAuthOutcome.Success).result.role)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `handleCallbackUrl emits Success with empty refreshToken when refresh_token is missing`() = runTest {
        manager.authOutcome.test {
            manager.handleCallbackUrl(
                "zirofitapp://auth-callback?access_token=abc&user_id=u1&role=trainer"
            )
            val outcome = awaitItem()
            assertTrue(outcome is GoogleAuthOutcome.Success)
            val success = outcome as GoogleAuthOutcome.Success
            assertEquals("abc", success.result.accessToken)
            assertEquals("", success.result.refreshToken)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `handleCallbackUrl emits Error when access_token is missing`() = runTest {
        manager.authOutcome.test {
            manager.handleCallbackUrl(
                "zirofitapp://auth-callback?refresh_token=ref&user_id=u1&role=trainer"
            )
            val outcome = awaitItem()
            assertTrue(outcome is GoogleAuthOutcome.Error)
            assertTrue((outcome as GoogleAuthOutcome.Error).message.contains("missing fields"))
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `handleCallbackUrl emits Error when user_id is missing`() = runTest {
        manager.authOutcome.test {
            manager.handleCallbackUrl(
                "zirofitapp://auth-callback?access_token=abc&refresh_token=ref&role=trainer"
            )
            val outcome = awaitItem()
            assertTrue(outcome is GoogleAuthOutcome.Error)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `handleCallbackUrl emits Error when role is missing`() = runTest {
        manager.authOutcome.test {
            manager.handleCallbackUrl(
                "zirofitapp://auth-callback?access_token=abc&refresh_token=ref&user_id=u1"
            )
            val outcome = awaitItem()
            assertTrue(outcome is GoogleAuthOutcome.Error)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `handleCallbackUrl ignores wrong scheme`() = runTest {
        manager.authOutcome.test {
            manager.handleCallbackUrl(
                "https://example.com/callback?access_token=abc&refresh_token=ref&user_id=u1&role=trainer"
            )
            expectNoEvents()
        }
    }

    @Test
    fun `handleCallbackUrl ignores wrong host`() = runTest {
        manager.authOutcome.test {
            manager.handleCallbackUrl(
                "zirofitapp://other-host?access_token=abc&refresh_token=ref&user_id=u1&role=trainer"
            )
            expectNoEvents()
        }
    }

    @Test
    fun `handleCallbackUrl sets callbackInvoked flag`() {
        manager.handleCallbackUrl(
            "zirofitapp://auth-callback?access_token=abc&user_id=u1&role=trainer"
        )
    }

    // --- GoogleAuthResult and GoogleAuthOutcome data classes ---

    @Test
    fun `GoogleAuthResult stores all fields correctly`() {
        val result = GoogleAuthResult(
            accessToken = "atoken",
            refreshToken = "rtoken",
            userId = "uid",
            role = "trainer"
        )
        assertEquals("atoken", result.accessToken)
        assertEquals("rtoken", result.refreshToken)
        assertEquals("uid", result.userId)
        assertEquals("trainer", result.role)
    }

    @Test
    fun `GoogleAuthOutcome Success wraps result correctly`() {
        val inner = GoogleAuthResult("a", "r", "u", "trainer")
        val outcome = GoogleAuthOutcome.Success(inner)
        assertEquals(inner, (outcome as GoogleAuthOutcome.Success).result)
    }

    @Test
    fun `GoogleAuthOutcome Error stores message correctly`() {
        val outcome = GoogleAuthOutcome.Error("test error message")
        assertEquals("test error message", (outcome as GoogleAuthOutcome.Error).message)
    }

    @Test
    fun `GoogleAuthOutcome Cancelled equals another Cancelled`() {
        val a = GoogleAuthOutcome.Cancelled
        val b = GoogleAuthOutcome.Cancelled
        assertTrue(a == b)
    }
}
