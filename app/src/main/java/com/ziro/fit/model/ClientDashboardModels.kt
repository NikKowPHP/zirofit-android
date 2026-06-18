package com.ziro.fit.model

import com.google.gson.annotations.SerializedName

// Expanded response matching iOS ClientDashboardResponse
data class ClientDashboardResponse(
    val clientData: ClientDashboardData,
    val weightUnit: String? = null,
    val upcomingClientSessions: List<ClientDashboardSession>? = null,
    val lastCheckIn: String? = null
)

data class ClientDashboardData(
    val id: String,
    val name: String,
    val email: String,
    val trainer: TrainerInfo?,
    val workoutSessions: List<ClientSession>?,
    val measurements: List<Measurement>?,
    val remainingCredits: Int? = null
)

data class TrainerInfo(
    val id: String,
    val name: String?,
    val username: String,
    val email: String
)

// Upcoming session model (matches iOS ClientDashboardSession)
data class ClientDashboardSession(
    val id: String,
    val title: String,
    @SerializedName("date") val date: String,
    val duration: Int,
    @SerializedName("is_trainer_assigned") val isTrainerAssigned: Boolean? = null
)
