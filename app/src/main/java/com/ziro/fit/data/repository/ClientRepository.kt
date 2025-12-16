package com.ziro.fit.data.repository

import com.ziro.fit.data.remote.ZiroApi
import com.ziro.fit.model.*
import kotlinx.coroutines.async
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
            Result.failure(e)
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
            Result.failure(e)
        }
    }

    suspend fun getClientMeasurements(clientId: String): Result<List<Measurement>> {
        return try {
            val response = api.getClientMeasurements(clientId)
            Result.success(response.data.measurements ?: emptyList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getClientAssessments(clientId: String): Result<List<AssessmentResult>> {
        return try {
            val response = api.getClientAssessments(clientId)
            Result.success(response.data.results ?: emptyList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getClientPhotos(clientId: String): Result<List<TransformationPhoto>> {
        return try {
            val response = api.getClientPhotos(clientId)
            Result.success(response.data.photos ?: emptyList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getClientSessions(clientId: String): Result<List<ClientSession>> {
        return try {
            val response = api.getClientSessions(clientId)
            Result.success(response.data.sessions ?: emptyList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteClient(clientId: String): Result<Unit> {
        return try {
            api.deleteClient(clientId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
