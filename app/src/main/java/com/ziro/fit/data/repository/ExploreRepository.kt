package com.ziro.fit.data.repository

import com.ziro.fit.data.remote.ZiroApi
import com.ziro.fit.model.ExploreEvent
import com.ziro.fit.model.ExploreEventsResponse
import com.ziro.fit.model.ExploreFeaturedResponse
import com.ziro.fit.model.ExploreMetadataResponse
import com.ziro.fit.util.ApiErrorParser
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExploreRepository @Inject constructor(
    private val api: ZiroApi
) {
    suspend fun getEvents(
        page: Int = 1,
        limit: Int = 20,
        categoryId: String? = null,
        search: String? = null,
        isFree: Boolean? = null
    ): Result<ExploreEventsResponse> {
        return try {
            val response = api.getExploreEvents(
                page = page,
                limit = limit,
                categoryId = categoryId,
                search = search,
                isFree = isFree
            )
            if (response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Failed to load events"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(ApiErrorParser.getErrorMessage(ApiErrorParser.parse(e))))
        }
    }

    suspend fun getEventDetails(eventId: String): Result<ExploreEvent> {
        return try {
            val response = api.getEventDetails(eventId)
            if (response.data != null) {
                Result.success(response.data.event)
            } else {
                Result.failure(Exception(response.message ?: "Event not found"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(ApiErrorParser.getErrorMessage(ApiErrorParser.parse(e))))
        }
    }

    suspend fun joinFreeEvent(eventId: String): Result<Unit> {
        return try {
            val response = api.joinFreeEvent(eventId)
            if (response.success != false) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message ?: "Failed to join event"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(ApiErrorParser.getErrorMessage(ApiErrorParser.parse(e))))
        }
    }

    suspend fun getMetadata(): Result<ExploreMetadataResponse> {
        return try {
            val response = api.getExploreMetadata()
            if (response.data != null) Result.success(response.data)
            else Result.failure(Exception("Metadata empty"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFeatured(lat: Double? = null, long: Double? = null, cityId: String? = null): Result<ExploreFeaturedResponse> {
        return try {
            val response = api.getExploreFeatured(lat, long, cityId)
            if (response.data != null) Result.success(response.data)
            else Result.failure(Exception("Featured content empty"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
