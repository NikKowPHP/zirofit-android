package com.ziro.fit.model

import java.util.UUID

enum class TemplateType {
    USER,
    TRAINER,
    SYSTEM
}

data class WorkoutTemplate(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val exerciseCount: Int,
    val description: String? = null,
    val lastPerformed: String? = null,
    val type: TemplateType = TemplateType.USER,
    val exercises: List<String> = emptyList() // List of exercise names or IDs for summary
)

data class CreateWorkoutTemplateRequest(
    val name: String,
    val description: String?,
    val exercises: List<CreateTemplateExercise>
)

data class CreateTemplateExercise(
    val name: String,
    val sets: Int,
    val reps: String,
    val restSeconds: Int,
    val notes: String?,
    val order: Int
)

data class CreateWorkoutTemplateResponse(
    val id: String,
    val name: String
)
