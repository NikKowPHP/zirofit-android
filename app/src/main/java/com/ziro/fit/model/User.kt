package com.ziro.fit.model

data class User(
    val id: String,
    val email: String,
    val name: String?,
    val role: String?,
    val username: String?,
    val hasCompletedOnboarding: Boolean,
    val subscriptionStatus: String?,
    val profilePhotoPath: String?,
    val isFreeAccessModeEnabled: Boolean,
    val tier: String?
)
