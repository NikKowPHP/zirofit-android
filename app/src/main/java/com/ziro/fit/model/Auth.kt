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
