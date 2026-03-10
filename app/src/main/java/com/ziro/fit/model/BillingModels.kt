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
