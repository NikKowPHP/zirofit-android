package com.ziro.fit.model

import com.google.gson.annotations.SerializedName

data class ExploreEvent(
    val id: String,
    val title: String,
    val description: String?,
    val startTime: String,
    val endTime: String?,
    val price: Double?,
    val currency: String?,
    val locationName: String,
    val address: String?,
    val latitude: Double?,
    val longitude: Double?,
    val imageUrl: String?,
    val categoryId: String?,
    val cityId: String?,
    val priceDisplay: String?,
    val hostName: String?,
    val hostId: String?,
    val trainerName: String?,
    val trainerId: String?,
    val enrolledCount: Int?,
    val capacity: Int?,
    val isBooked: Boolean?,
    val isNearCapacity: Boolean?,
    val trainer: EventTrainer?
) {
    val resolvedHostName: String?
        get() = hostName ?: trainerName ?: trainer?.name
        
    val isFull: Boolean
        get() = (enrolledCount ?: 0) >= (capacity ?: 1)
        
    val spotsLeft: Int
        get() = maxOf(0, (capacity ?: 0) - (enrolledCount ?: 0))
}

data class EventTrainer(
    val name: String?,
    val username: String?,
    val profile: EventTrainerProfile?
)

data class EventTrainerProfile(
    val profilePhotoPath: String?,
    val aboutMe: String?
)

data class ExploreEventsResponse(
    val events: List<ExploreEvent>,
    val pagination: PaginationData?
)

data class PaginationData(
    val total: Int?,
    val page: Int,
    val hasMore: Boolean
)

data class EventDetailResponse(
    val event: ExploreEvent
)
