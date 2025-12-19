package com.ziro.fit.model

data class GetClientProgramsResponse(
    val userPrograms: List<ProgramDto> = emptyList(),
    val systemPrograms: List<ProgramDto> = emptyList(),
    val trainerPrograms: List<ProgramDto> = emptyList() 
)

data class ProgramDto(
    val id: String,
    val name: String,
    val description: String? = null,
    val templates: List<WorkoutTemplateDto>? = null
)

data class WorkoutTemplateDto(
    val id: String,
    val name: String,
    val exerciseCount: Int = 0,
    val description: String? = null,
    val lastPerformed: String? = null,
    val exercises: List<ExerciseDto>? = null
)

data class ExerciseDto(
    val name: String,
    val id: String? = null
)
