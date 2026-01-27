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
    val templates: List<WorkoutTemplateDto>? = null,
    val weeks: List<ProgramWeekDto>? = null
)

data class ProgramWeekDto(
    val weekNumber: Int,
    val workouts: List<ProgramWorkoutDto>
)

data class ProgramWorkoutDto(
    val name: String,
    val focus: String?,
    val exercises: List<ProgramExerciseDto>
)

data class ProgramExerciseDto(
    val name: String,
    val sets: Int,
    val reps: String,
    val rpe: Int?,
    val rest: Int?,
    val notes: String?
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

data class GenerateProgramRequest(
    val clientId: String,
    val duration: String, // "week" or "month"
    val focus: String
)

data class ProgramResponse(
    val programId: String,
    val name: String,
    val description: String?,
    val weeks: List<ProgramWeekDto>
)
