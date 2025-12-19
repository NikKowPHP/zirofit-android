package com.ziro.fit.model

import com.google.gson.annotations.SerializedName

data class ClientDashboardResponse(
    val clientData: ClientDashboardData
)

data class ClientDashboardData(
    val id: String,
    val name: String,
    val email: String,
    val trainer: TrainerInfo?,
    val workoutSessions: List<ClientSession>?,
    val measurements: List<Measurement>?
)

data class TrainerInfo(
    val id: String,
    val name: String?,
    val username: String,
    val email: String
)
