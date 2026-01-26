package com.ziro.fit.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val message: String,
    val role: String,
    val accessToken: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val role: String = "pending"
)

data class RegisterResponse(
    val userId: String,
    val message: String,
    val requiresSubscription: Boolean?
)
