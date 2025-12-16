package com.ziro.fit.model

import com.google.gson.annotations.SerializedName

data class Client(
    val id: String,
    val name: String,
    val email: String,
    val phone: String?,
    val status: String,
    // Add other fields as necessary, these are the core ones from API reference
    @SerializedName("createdAt") val createdAt: String? = null
)

data class GetClientsResponse(
    val clients: List<Client>
)
