package com.ziro.fit.model
import com.google.gson.annotations.SerializedName


import kotlinx.serialization.json.Json

data class ApiResponse<T>(
    val success: Boolean? = null,
  
    // FIX: PARSE STATUS
    @SerializedName("status") val statusWrapper: StatusWrapper? = null,  
    val data: T?,
    val message: String? = null,
    val error: String? = null
)

data class StatusWrapper(
    val status: Int
)