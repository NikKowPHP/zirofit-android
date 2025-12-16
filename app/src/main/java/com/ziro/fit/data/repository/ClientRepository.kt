package com.ziro.fit.data.repository

import com.ziro.fit.data.remote.ZiroApi
import com.ziro.fit.model.Client
import com.ziro.fit.model.GetClientsResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClientRepository @Inject constructor(
    private val api: ZiroApi
) {
    suspend fun getClients(): Result<List<Client>> {
        return try {
            val response = api.getClients()
            // Assuming response.data is GetClientsResponse which has a 'clients' list
            Result.success(response.data.clients)
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
