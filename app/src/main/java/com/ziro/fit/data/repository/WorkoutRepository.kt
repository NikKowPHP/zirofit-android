package com.ziro.fit.data.repository

import com.ziro.fit.data.remote.ZiroApi
import com.ziro.fit.model.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutRepository @Inject constructor(
    private val api: ZiroApi
) {
    suspend fun getTemplates(): List<WorkoutTemplate> {
        return try {
            val response = api.getWorkoutTemplates()
            val data = response.data ?: return emptyList()

            val userTemplates = data.templates.map { dto ->
                WorkoutTemplate(
                    id = dto.id,
                    name = dto.name,
                    exerciseCount = dto.exerciseCount,
                    description = dto.description,
                    type = TemplateType.USER,
                    exercises = dto.exercises?.map { it.name } ?: emptyList()
                )
            }

            val systemTemplates = data.systemTemplates.map { dto ->
                WorkoutTemplate(
                    id = dto.id,
                    name = dto.name,
                    exerciseCount = dto.exerciseCount,
                    description = dto.description,
                    type = TemplateType.SYSTEM,
                    exercises = dto.exercises?.map { it.name } ?: emptyList()
                )
            }

            userTemplates + systemTemplates
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

            val exercises = serverTemplate.exercises.sortedBy { it.order }.map { step ->
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
                description = serverTemplate.description,
                exercises = exercises
            )
            
            Result.success(templateDto)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun createTemplate(request: com.ziro.fit.model.CreateWorkoutTemplateRequest): Result<com.ziro.fit.model.CreateWorkoutTemplateResponse> {
        return try {
            val response = api.createWorkoutTemplate(request)
            Result.success(response.data!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateTemplate(id: String, request: com.ziro.fit.model.CreateWorkoutTemplateRequest): Result<com.ziro.fit.model.CreateWorkoutTemplateResponse> {
        return try {
            val response = api.updateWorkoutTemplate(id, request)
            Result.success(response.data!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteTemplate(id: String): Result<Unit> {
        return try {
            api.deleteWorkoutTemplate(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
