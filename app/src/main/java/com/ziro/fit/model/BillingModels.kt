package com.ziro.fit.model

data class CreateCheckoutSessionRequest(
    val packageId: String? = null,
    val eventId: String? = null,
    val type: String? = null,
    val isMobile: Boolean? = true,
    val successUrl: String = "zirofit://payment/success",
    val cancelUrl: String = "zirofit://payment/cancel"
)

data class CreateCheckoutSessionResponse(
    val url: String
)

data class SubscriptionInfo(
    val tier: String?,
    val subscriptionStatus: String?,
    val tierName: String?,
    val tierId: String?,
    val stripeCancelAtPeriodEnd: Boolean?,
    val stripeCurrentPeriodEnd: String?,
    val trialEndsAt: String?,
    val freeMode: Boolean?
)

data class SubscriptionResponse(
    val subscription: SubscriptionInfo
)

data class BillingPortalResponse(val url: String)
