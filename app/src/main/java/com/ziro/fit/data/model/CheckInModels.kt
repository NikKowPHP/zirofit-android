package com.ziro.fit.data.model

import com.google.gson.annotations.SerializedName

data class CheckInPendingItem(
    val id: String,
    val status: String,
    val client: CheckInClientName,
    val date: String
)

data class CheckInClientName(
    val id: String?,
    val name: String
)

data class CheckInContext(
    val current: CheckInDetailWrapper,
    val previous: CheckInDetailWrapper?,
    val history: List<CheckInDetailWrapper>
)

data class CheckInDetailWrapper(
    val id: String,
    val status: String,
    val date: String,
    val client: CheckInClientName,
    val weight: Double? = null,
    val waistCm: Double? = null,
    val sleepHours: Double? = null,
    val energyLevel: Double? = null,
    val stressLevel: Double? = null,
    val hungerLevel: Double? = null,
    val digestionLevel: Double? = null,
    val nutritionCompliance: String? = null,
    val clientNotes: String? = null,
    val photos: List<CheckInPhoto>? = null,
    // Trainer response might be here too if already reviewed?
    val trainerResponse: String? = null
)

// Extending the wrapper to potentially include full details if the API returns them flattened or nested.
// Based on the API reference, specific details like weight/notes aren't explicitly inside 'current' in the schema shown,
// but usually 'current' would contain the submission data. 
// I will create a separate detailed model that we might map to or assuming 'current' has these fields.
// Let's refine based on the "post" body schema which suggests what a check-in contains.

data class CheckInSubmission(
    val id: String,
    val status: String,
    val date: String,
    val client: CheckInClientName,
    // Fields from the submission schema
    val weight: Double?,
    val waistCm: Double?,
    val sleepHours: Double?,
    val energyLevel: Double?,
    val stressLevel: Double?,
    val hungerLevel: Double?,
    val digestionLevel: Double?,
    val nutritionCompliance: String?,
    val clientNotes: String?,
    val photos: List<CheckInPhoto>?
)

data class CheckInPhoto(
    val imagePath: String,
    val caption: String?,
    val date: String?
)

data class ReviewCheckInRequest(
    val trainerResponse: String
)
