package com.ziro.fit.model

data class CalendarSummaryResponse(
    val summary: List<ClientSummaryItem>?
)

data class ClientSummaryItem(
    val date: String,
    val clientId: String,
    val clientFirstName: String,
    val clientLastName: String,
    val clientAvatarUrl: String?
)
