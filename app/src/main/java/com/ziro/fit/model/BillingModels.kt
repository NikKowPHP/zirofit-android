package com.ziro.fit.model

data class CreateCheckoutSessionRequest(
    val packageId: String,
    val successUrl: String = "zirofit://payment/success",
    val cancelUrl: String = "zirofit://payment/cancel"
)

data class CreateCheckoutSessionResponse(
    val url: String
)
