package com.ziro.fit.model

data class Exercise(
    val id: String,
    val name: String,
    val muscleGroup: String?,
    val equipment: String?,
    val videoUrl: String?,
    val isCustom: Boolean = false
)

data class GetExercisesResponse(
    val exercises: List<Exercise>,
    val total: Int,
    val page: Int,
    val pageSize: Int,
    val hasMore: Boolean
)

data class CreateExerciseRequest(
    val name: String,
    val muscleGroup: String?,
    val equipment: String?,
    val videoUrl: String?
)

data class CreateExerciseResponse(
    val exercise: Exercise
)
