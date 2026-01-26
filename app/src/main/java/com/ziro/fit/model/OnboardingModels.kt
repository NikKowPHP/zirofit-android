package com.ziro.fit.model

data class CompleteOnboardingRequest(
    val role: String,
    val name: String,
    val location: String? = null,
    val bio: String? = null
)
