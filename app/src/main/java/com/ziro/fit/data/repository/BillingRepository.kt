package com.ziro.fit.data.repository

import com.ziro.fit.data.remote.ZiroApi
import com.ziro.fit.model.CreateCheckoutSessionRequest
import com.ziro.fit.util.ApiErrorParser
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BillingRepository @Inject constructor(
    private val api: ZiroApi
) {
    suspend fun createCheckoutSession(packageId: String): Result<String> {
        return try {
            val request = CreateCheckoutSessionRequest(packageId = packageId)
            val response = api.createCheckoutSession(request)
            if (response.success != false && response.data != null) {
                Result.success(response.data.url)
            } else {
                Result.failure(Exception(response.message ?: "Failed to initiate checkout"))
            }
        } catch (e: Exception) {
            val apiError = ApiErrorParser.parse(e)
            Result.failure(Exception(ApiErrorParser.getErrorMessage(apiError)))
        }
    }
}
