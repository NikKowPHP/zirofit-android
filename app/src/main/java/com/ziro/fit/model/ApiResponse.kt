package com.ziro.fit.model

data class ApiResponse<T>(
    val success: Boolean? = null,
    val data: T?,
    val message: String? = null,
    val error: String? = null
)
