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
    val trainerId: String? = null,
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

data class ClientProgramAssignmentDto(
    val assignmentId: String,
    val startDate: String,
    val isActive: Boolean,
    val program: ProgramDto
)

data class TemplateStatus(
    val templateId: String,
    val status: String, // "COMPLETED", "NEXT", "PENDING"
    val lastCompletedAt: String? = null
)

// DTOs for the API response
data class ActiveProgramApiResponse(
    val program: ActiveProgramInfo,
    val progress: ProgramProgressInfo,
    val templates: List<ProgramTemplateDto>
)

data class ActiveProgramInfo(
    val id: String,
    val name: String,
    val description: String?
)

data class ProgramProgressInfo(
    val completedCount: Int,
    val totalCount: Int,
    val progressPercentage: Float,
    val nextTemplateId: String?
)

data class ProgramTemplateDto(
    val id: String,
    val name: String,
    val description: String?,
    val order: Int,
    val status: String,
    val exerciseCount: Int
)

// UI Model
data class ActiveProgramProgress(
    val programId: String,
    val programName: String,
    val programDescription: String? = null,
    val progressPercentage: Float,
    val currentTemplate: WorkoutTemplateDto?,
    val templateStatuses: List<TemplateStatus>
) {
    companion object {
        fun fromApiResponse(response: ActiveProgramApiResponse): ActiveProgramProgress {
            val currentTemplateDto = if (response.progress.nextTemplateId != null) {
                val template = response.templates.find { it.id == response.progress.nextTemplateId }
                template?.let {
                    WorkoutTemplateDto(
                        id = it.id,
                        name = it.name,
                        description = it.description,
                        exerciseCount = it.exerciseCount
                    )
                }
            } else null

            val templateStatuses = response.templates.map {
                TemplateStatus(
                    templateId = it.id,
                    status = it.status,
                    lastCompletedAt = null
                )
            }

            return ActiveProgramProgress(
                programId = response.program.id,
                programName = response.program.name,
                programDescription = response.program.description,
                progressPercentage = response.progress.progressPercentage,
                currentTemplate = currentTemplateDto,
                templateStatuses = templateStatuses
            )
        }
    }
}
