package com.ziro.fit.viewmodel

import android.util.Log
import com.ziro.fit.data.remote.ZiroApi
import com.ziro.fit.model.*
import com.ziro.fit.util.MainDispatcherRule
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BillingPortalViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val api: ZiroApi = mockk(relaxed = true)

    private fun createSubscriptionInfo(
        tier: String = "premium",
        tierName: String = "Premium Plan"
    ) = SubscriptionInfo(
        tier = tier,
        subscriptionStatus = "active",
        tierName = tierName,
        tierId = "tier_123",
        stripeCancelAtPeriodEnd = false,
        stripeCurrentPeriodEnd = "2026-04-22T00:00:00Z",
        trialEndsAt = null,
        freeMode = false
    )

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.e(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>(), any<Throwable>()) } returns 0
    }

    @Test
    fun `loadSubscription success populates subscription and sets isLoading to false`() = runTest {
        val subscription = createSubscriptionInfo()
        val response = ApiResponse(
            success = true,
            data = subscription,
            message = null
        )
        coEvery { api.getBillingSubscription() } returns response

        val viewModel = BillingPortalViewModel(api)
        viewModel.loadSubscription()
        advanceUntilIdle()

        assertNotNull(viewModel.subscription)
        assertEquals("premium", viewModel.subscription?.tier)
        assertFalse(viewModel.isLoading)
    }

    @Test
    fun `loadSubscription failure sets error and isLoading to false`() = runTest {
        val response: ApiResponse<SubscriptionInfo> = mockk()
        coEvery { response.success } returns false
        coEvery { response.message } returns "Network Error"
        coEvery { response.data } returns null
        coEvery { api.getBillingSubscription() } returns response

        val viewModel = BillingPortalViewModel(api)
        viewModel.loadSubscription()
        advanceUntilIdle()

        assertEquals("Network Error", viewModel.error)
        assertFalse(viewModel.isLoading)
    }

    @Test
    fun `loadSubscription with null data sets error`() = runTest {
        val response: ApiResponse<SubscriptionInfo> = mockk()
        coEvery { response.success } returns false
        coEvery { response.message } returns null
        coEvery { response.data } returns null
        coEvery { api.getBillingSubscription() } returns response

        val viewModel = BillingPortalViewModel(api)
        viewModel.loadSubscription()
        advanceUntilIdle()

        assertEquals("Failed to load subscription", viewModel.error)
        assertFalse(viewModel.isLoading)
    }

    @Test
    fun `openBillingPortal success sets portalUrl and isLoadingPortal to false`() = runTest {
        val response = ApiResponse(
            success = true,
            data = BillingPortalResponse(url = "https://billing.stripe.com/test"),
            message = null
        )
        coEvery { api.getBillingPortalUrl() } returns response

        val viewModel = BillingPortalViewModel(api)
        advanceUntilIdle()

        viewModel.openBillingPortal()
        advanceUntilIdle()

        assertEquals("https://billing.stripe.com/test", viewModel.portalUrl)
        assertFalse(viewModel.isLoadingPortal)
    }

    @Test
    fun `openBillingPortal failure sets error`() = runTest {
        val response: ApiResponse<BillingPortalResponse> = mockk()
        coEvery { response.success } returns false
        coEvery { response.message } returns "Failed to get billing portal URL"
        coEvery { response.data } returns null
        coEvery { api.getBillingPortalUrl() } returns response

        val viewModel = BillingPortalViewModel(api)
        advanceUntilIdle()

        viewModel.openBillingPortal()
        advanceUntilIdle()

        assertEquals("Failed to get billing portal URL", viewModel.error)
        assertFalse(viewModel.isLoadingPortal)
    }

    @Test
    fun `clearPortalUrl sets portalUrl to null`() = runTest {
        val response = ApiResponse(
            success = true,
            data = BillingPortalResponse(url = "https://billing.stripe.com/test"),
            message = null
        )
        coEvery { api.getBillingPortalUrl() } returns response

        val viewModel = BillingPortalViewModel(api)
        advanceUntilIdle()

        viewModel.openBillingPortal()
        advanceUntilIdle()

        assertNotNull(viewModel.portalUrl)

        viewModel.clearPortalUrl()

        assertNull(viewModel.portalUrl)
    }

    @Test
    fun `clearError sets error to null`() = runTest {
        val response: ApiResponse<SubscriptionInfo> = mockk()
        coEvery { response.success } returns false
        coEvery { response.message } returns "Network Error"
        coEvery { response.data } returns null
        coEvery { api.getBillingSubscription() } returns response

        val viewModel = BillingPortalViewModel(api)
        viewModel.loadSubscription()
        advanceUntilIdle()

        assertNotNull(viewModel.error)

        viewModel.clearError()

        assertNull(viewModel.error)
    }
}
