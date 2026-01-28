package com.ziro.fit.data.repository

import com.ziro.fit.data.remote.ZiroApi
import com.ziro.fit.model.TemplateType
import com.ziro.fit.model.WorkoutTemplate
import com.ziro.fit.model.WorkoutTemplateDto
import com.ziro.fit.model.ExerciseDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutRepository @Inject constructor(
    private val api: ZiroApi
) {
    suspend fun getTemplates(): List<WorkoutTemplate> {
        return try {
            val assignments = api.getClientPrograms()
            val allTemplates = mutableListOf<WorkoutTemplate>()
            
            assignments.forEach { assignment ->
                val program = assignment.program
                // If trainerId is present, it's a trainer program. 
                // If not, it's likely an AI or System program assigned to the user.
                // We'll treat AI programs (null trainerId) as USER templates so they show in "My Templates".
                val type = if (program.trainerId != null) TemplateType.TRAINER else TemplateType.USER
                
                program.templates?.forEach { templateDto ->
                    allTemplates.add(
                        WorkoutTemplate(
                            id = templateDto.id,
                            name = templateDto.name,
                            exerciseCount = templateDto.exerciseCount,
                            description = templateDto.description,
                            lastPerformed = templateDto.lastPerformed,
                            type = type,
                            exercises = templateDto.exercises?.map { it.name } ?: emptyList()
                        )
                    )
                }
            }
            allTemplates
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getPrograms(): List<com.ziro.fit.model.ProgramDto> {
        return try {
            val assignments = api.getClientPrograms()
            assignments.map { it.program }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getProgram(programId: String): com.ziro.fit.model.ProgramDto? {
        return try {
            val assignments = api.getClientPrograms()
            assignments.find { it.program.id == programId }?.program
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getTemplateDetails(templateId: String): Result<WorkoutTemplateDto> {
        return try {
            val response = api.getWorkoutTemplate(templateId)
            val serverTemplate = response.data 
                ?: return Result.failure(Exception("Template not found"))

            // Map ServerTemplate to WorkoutTemplateDto
            val exercises = serverTemplate.exercises.sortedBy { it.order }.map { step ->
                // Try to get exercise name safely
                val exerciseName = step.exercise?.name ?: run {
                    val noteName = step.notes?.let { note ->
                        val match = Regex("Exercise: (.*?)[\\.,]").find(note)
                        match?.groupValues?.get(1)
                    }
                    noteName ?: "Exercise ${step.order}"
                }
                
                ExerciseDto(
                    id = step.exerciseId ?: step.id,
                    name = exerciseName
                )
            }

            val templateDto = WorkoutTemplateDto(
                id = serverTemplate.id,
                name = serverTemplate.name,
                exerciseCount = exercises.size,
                description = null, // ServerTemplate doesn't have description in the model we used?
                lastPerformed = null,
                exercises = exercises
            )
            
            Result.success(templateDto)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
