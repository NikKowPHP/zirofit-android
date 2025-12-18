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
    @SerializedName("trainerId") val trainerId: String? = null,
    @SerializedName("avatarUrl") val avatarUrl: String? = null
)

data class CreateClientRequest(
    val name: String,
    val email: String,
    val phone: String?,
    val status: String = "active"
)

data class UpdateClientRequest(
    val name: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val status: String? = null,
    val checkInDay: Int? = null,
    val checkInHour: Int? = null
)

data class CreateClientResponse(
    val client: Client
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

data class CreateMeasurementRequest(
    val measurementDate: String,
    val weightKg: String?,
    val bodyFatPercentage: String?,
    val notes: String?,
    val customMetrics: Map<String, Any>? = null
)

data class CreateMeasurementResponse(
    val measurement: Measurement
)

data class GetMeasurementsResponse(
    val measurements: List<Measurement>?
)

data class CreateClientAssessmentRequest(
    val assessmentId: String,
    val date: String,
    val value: Double,
    val notes: String?
)

data class CreateClientAssessmentResponse(
    val assessmentResult: AssessmentResult
)

data class UploadPhotoResponse(
    val progressPhoto: TransformationPhoto
)

data class UpdateSessionRequest(
    val notes: String?,
    val status: String? // "Completed", "Cancelled", etc.
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

data class GetClientAssessmentsResponse(
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
    @SerializedName("name") val name: String?,
    val plannedDate: String?,
    val workoutTemplateId: String?
)

data class GetClientSessionsResponse(
    val sessions: List<ClientSession>?
)
