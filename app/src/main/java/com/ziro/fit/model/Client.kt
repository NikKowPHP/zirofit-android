package com.ziro.fit.model

import com.google.gson.annotations.SerializedName

data class Client(
    val id: String,
    val name: String,
    val email: String,
    val phone: String?,
    val status: String,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("checkInDay") val checkInDay: Int? = null,
    @SerializedName("checkInHour") val checkInHour: Int? = null,
    @SerializedName("trainerId") val trainerId: String? = null
)

data class GetClientsResponse(
    val clients: List<Client>
)

data class GetClientDetailsResponse(
    val client: Client
)

data class Measurement(
    val id: String,
    val measurementDate: String,
    val weightKg: Double?,
    val bodyFatPercentage: Double?,
    val notes: String?,
    val customMetrics: Map<String, Any>?
)

data class GetMeasurementsResponse(
    val measurements: List<Measurement>?
)

data class AssessmentResult(
    val id: String,
    val assessmentId: String,
    val assessmentName: String,
    val value: Double,
    val date: String,
    val notes: String?,
    val unit: String?
)

data class GetAssessmentsResponse(
    val results: List<AssessmentResult>?
)

data class TransformationPhoto(
    val id: String,
    val photoUrl: String,
    val photoDate: String,
    val caption: String?
)

data class GetPhotosResponse(
    val photos: List<TransformationPhoto>?
)

data class ClientSession(
    val id: String,
    val startTime: String,
    val endTime: String?,
    val status: String,
    val notes: String?,
    val templateName: String?
)

data class GetClientSessionsResponse(
    val sessions: List<ClientSession>?
)
