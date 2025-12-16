package com.ziro.fit.data.repository

import com.ziro.fit.data.remote.ZiroApi
import com.ziro.fit.model.*
import com.ziro.fit.util.ApiErrorParser
import kotlinx.coroutines.async
import com.ziro.fit.model.UpdateClientRequest
import com.ziro.fit.model.CreateMeasurementRequest
import com.ziro.fit.model.CreateAssessmentRequest
import com.ziro.fit.model.UpdateSessionRequest
import com.ziro.fit.model.UploadPhotoResponse
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.File 
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject
import javax.inject.Singleton

data class ClientProfileData(
    val client: Client,
    val measurements: List<Measurement>,
    val assessments: List<AssessmentResult>,
    val photos: List<TransformationPhoto>,
    val sessions: List<ClientSession>
)

@Singleton
class ClientRepository @Inject constructor(
    private val api: ZiroApi
) {
    suspend fun getClients(): Result<List<Client>> {
        return try {
            val response = api.getClients()
            Result.success(response.data.clients)
        } catch (e: Exception) {
            val apiError = ApiErrorParser.parseError(e)
            Result.failure(Exception(ApiErrorParser.getErrorMessage(apiError)))
        }
    }

    suspend fun createClient(name: String, email: String, phone: String?, status: String): Result<Client> {
        return try {
            val request = CreateClientRequest(name, email, phone, status)
            val response = api.createClient(request)
            Result.success(response.data.client)
        } catch (e: Exception) {
            val apiError = ApiErrorParser.parseError(e)
            Result.failure(Exception(ApiErrorParser.getErrorMessage(apiError)))
        }
    }

    suspend fun updateClient(clientId: String, name: String?, email: String?, phone: String?, status: String?): Result<Unit> {
        return try {
            val request = UpdateClientRequest(name, email, phone, status)
            api.updateClient(clientId, request)
            Result.success(Unit)
        } catch (e: Exception) {
            val apiError = ApiErrorParser.parseError(e)
            Result.failure(Exception(ApiErrorParser.getErrorMessage(apiError)))
        }
    }

    suspend fun getClientProfile(clientId: String): Result<ClientProfileData> {
        return try {
            coroutineScope {
                // Fetch all data in parallel
                val clientDeferred = async { api.getClientDetails(clientId) }
                val measurementsDeferred = async { api.getClientMeasurements(clientId) }
                val assessmentsDeferred = async { api.getClientAssessments(clientId) }
                val photosDeferred = async { api.getClientPhotos(clientId) }
                val sessionsDeferred = async { api.getClientSessions(clientId) }

                // Await all results
                val client = clientDeferred.await().data.client
                val measurements = measurementsDeferred.await().data.measurements ?: emptyList()
                val assessments = assessmentsDeferred.await().data.results ?: emptyList()
                val photos = photosDeferred.await().data.photos ?: emptyList()
                val sessions = sessionsDeferred.await().data.sessions ?: emptyList()

                Result.success(
                    ClientProfileData(
                        client = client,
                        measurements = measurements,
                        assessments = assessments,
                        photos = photos,
                        sessions = sessions
                    )
                )
            }
        } catch (e: Exception) {
            val apiError = ApiErrorParser.parseError(e)
            Result.failure(Exception(ApiErrorParser.getErrorMessage(apiError)))
        }
    }

    suspend fun getClientMeasurements(clientId: String): Result<List<Measurement>> {
        return try {
            val response = api.getClientMeasurements(clientId)
            Result.success(response.data.measurements ?: emptyList())
        } catch (e: Exception) {
            val apiError = ApiErrorParser.parseError(e)
            Result.failure(Exception(ApiErrorParser.getErrorMessage(apiError)))
        }
    }

    suspend fun getClientAssessments(clientId: String): Result<List<AssessmentResult>> {
        return try {
            val response = api.getClientAssessments(clientId)
            Result.success(response.data.results ?: emptyList())
        } catch (e: Exception) {
            val apiError = ApiErrorParser.parseError(e)
            Result.failure(Exception(ApiErrorParser.getErrorMessage(apiError)))
        }
    }

    suspend fun createAssessment(clientId: String, assessmentId: String, date: String, value: Double, notes: String?): Result<AssessmentResult> {
        return try {
            val response = api.createAssessment(
                clientId,
                CreateAssessmentRequest(assessmentId, date, value, notes)
            )
            Result.success(response.data.assessmentResult)
        } catch (e: Exception) {
            val apiError = ApiErrorParser.parseError(e)
            Result.failure(Exception(ApiErrorParser.getErrorMessage(apiError)))
        }
    }

    suspend fun deleteAssessment(clientId: String, resultId: String): Result<Unit> {
        return try {
            api.deleteAssessment(clientId, resultId)
            Result.success(Unit)
        } catch (e: Exception) {
            val apiError = ApiErrorParser.parseError(e)
            Result.failure(Exception(ApiErrorParser.getErrorMessage(apiError)))
        }
    }

    suspend fun getClientPhotos(clientId: String): Result<List<TransformationPhoto>> {
        return try {
            val response = api.getClientPhotos(clientId)
            Result.success(response.data.photos ?: emptyList())
        } catch (e: Exception) {
            val apiError = ApiErrorParser.parseError(e)
            Result.failure(Exception(ApiErrorParser.getErrorMessage(apiError)))
        }
    }

    suspend fun uploadPhoto(clientId: String, file: java.io.File, date: String, caption: String?): Result<TransformationPhoto> {
        return try {
            val mediaTypeImage = "image/*".toMediaTypeOrNull()
            val mediaTypeText = "text/plain".toMediaTypeOrNull()
            
            val requestFile = okhttp3.RequestBody.create(mediaTypeImage, file)
            val body = okhttp3.MultipartBody.Part.createFormData("photo", file.name, requestFile)
            val dateBody = okhttp3.RequestBody.create(mediaTypeText, date)
            val captionBody = caption?.let { okhttp3.RequestBody.create(mediaTypeText, it) }

            val response = api.uploadPhoto(clientId, body, dateBody, captionBody)
            Result.success(response.data.progressPhoto)
        } catch (e: Exception) {
            val apiError = ApiErrorParser.parseError(e)
            Result.failure(Exception(ApiErrorParser.getErrorMessage(apiError)))
        }
    }

    suspend fun deletePhoto(clientId: String, photoId: String): Result<Unit> {
        return try {
            api.deletePhoto(clientId, photoId)
            Result.success(Unit)
        } catch (e: Exception) {
            val apiError = ApiErrorParser.parseError(e)
            Result.failure(Exception(ApiErrorParser.getErrorMessage(apiError)))
        }
    }

    suspend fun createMeasurement(clientId: String, date: String, weight: Double?, bodyFat: Double?, notes: String?): Result<Measurement> {
        return try {
            val response = api.createMeasurement(
                clientId,
                CreateMeasurementRequest(
                    measurementDate = date,
                    weightKg = weight?.toString(),
                    bodyFatPercentage = bodyFat?.toString(),
                    notes = notes
                )
            )
            Result.success(response.data.measurement)
        } catch (e: Exception) {
            val apiError = ApiErrorParser.parseError(e)
            Result.failure(Exception(ApiErrorParser.getErrorMessage(apiError)))
        }
    }

    suspend fun deleteMeasurement(clientId: String, measurementId: String): Result<Unit> {
        return try {
            api.deleteMeasurement(clientId, measurementId)
            Result.success(Unit)
        } catch (e: Exception) {
            val apiError = ApiErrorParser.parseError(e)
            Result.failure(Exception(ApiErrorParser.getErrorMessage(apiError)))
        }
    }

    suspend fun getClientSessions(clientId: String): Result<List<ClientSession>> {
        return try {
            val response = api.getClientSessions(clientId)
            Result.success(response.data.sessions ?: emptyList())
        } catch (e: Exception) {
            val apiError = ApiErrorParser.parseError(e)
            Result.failure(Exception(ApiErrorParser.getErrorMessage(apiError)))
        }
    }

    suspend fun updateSession(clientId: String, sessionId: String, notes: String?, status: String?): Result<ClientSession> {
        return try {
            val response = api.updateSession(clientId, sessionId, UpdateSessionRequest(notes, status))
            Result.success(response.data)
        } catch (e: Exception) {
            val apiError = ApiErrorParser.parseError(e)
            Result.failure(Exception(ApiErrorParser.getErrorMessage(apiError)))
        }
    }

    suspend fun deleteSession(clientId: String, sessionId: String): Result<Unit> {
        return try {
            api.deleteSession(clientId, sessionId)
            Result.success(Unit)
        } catch (e: Exception) {
            val apiError = ApiErrorParser.parseError(e)
            Result.failure(Exception(ApiErrorParser.getErrorMessage(apiError)))
        }
    }

    suspend fun deleteClient(clientId: String): Result<Unit> {
        return try {
            api.deleteClient(clientId)
            Result.success(Unit)
        } catch (e: Exception) {
            val apiError = ApiErrorParser.parseError(e)
            Result.failure(Exception(ApiErrorParser.getErrorMessage(apiError)))
        }
    }
}
