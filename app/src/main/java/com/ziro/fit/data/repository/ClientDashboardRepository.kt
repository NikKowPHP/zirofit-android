package com.ziro.fit.data.repository

import com.ziro.fit.data.remote.ZiroApi
import com.ziro.fit.model.ClientDashboardData
import com.ziro.fit.util.ApiErrorParser
import javax.inject.Inject

class ClientDashboardRepository @Inject constructor(
    private val api: ZiroApi
) {
    suspend fun getClientDashboard(): Result<ClientDashboardData> {
        return try {
            val response = api.getClientDashboard()
            if (response.success != false && response.data != null) {
                Result.success(response.data.clientData)
            } else {
                Result.failure(Exception("Failed to load dashboard data"))
            }
        } catch (e: retrofit2.HttpException) {
            if (e.code() == 404) {
                Result.failure(Exception("ProfileNotFound"))
            } else {
                val apiError = ApiErrorParser.parse(e)
                Result.failure(Exception(ApiErrorParser.getErrorMessage(apiError)))
            }
        } catch (e: Exception) {
            val apiError = ApiErrorParser.parse(e)
            Result.failure(Exception(ApiErrorParser.getErrorMessage(apiError)))
        }
    }
}
