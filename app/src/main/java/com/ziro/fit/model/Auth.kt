package com.ziro.fit.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val message: String,
    val role: String,
    val accessToken: String,
    val refreshToken: String?,
    val user: User
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val role: String = "pending",
    val redirect: String? = null
)

data class RegisterResponse(
    val userId: String,
    val message: String,
    val requiresSubscription: Boolean?,
    val confirmationRequired: Boolean = false
)

data class RefreshTokenRequest(
    val refreshToken: String
)

data class RefreshTokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val expiresAt: Long,
    val user: User
)
