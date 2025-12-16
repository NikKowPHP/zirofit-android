package com.ziro.fit.model

import com.google.gson.annotations.SerializedName

data class Assessment(
    val id: String,
    val name: String,
    val description: String?,
    val unit: String,
    val trainerId: String?
)

data class CreateAssessmentRequest(
    val name: String,
    val description: String? = null,
    val unit: String
)

data class CreateAssessmentResponse(
    val newAssessment: Assessment
)

// Assuming standard update body structure, usually similar to create but partial or full
data class UpdateAssessmentRequest(
    val name: String,
    val description: String? = null,
    val unit: String
)

data class GetAssessmentsResponse(
    val assessments: List<Assessment>
)
