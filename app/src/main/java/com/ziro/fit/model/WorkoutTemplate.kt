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
