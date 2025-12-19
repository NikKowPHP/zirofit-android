package com.ziro.fit.data.repository

import com.ziro.fit.data.remote.ZiroApi
import com.ziro.fit.model.TemplateType
import com.ziro.fit.model.WorkoutTemplate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutRepository @Inject constructor(
    private val api: ZiroApi
) {
    suspend fun getTemplates(): List<WorkoutTemplate> {
        return try {
            val response = api.getClientPrograms()
            if (response.data != null) {
                val allTemplates = mutableListOf<WorkoutTemplate>()
                
                response.data.userPrograms.forEach { program ->
                    program.templates?.forEach { templateDto ->
                        allTemplates.add(
                            WorkoutTemplate(
                                id = templateDto.id,
                                name = templateDto.name,
                                exerciseCount = templateDto.exerciseCount,
                                description = templateDto.description,
                                lastPerformed = templateDto.lastPerformed,
                                type = TemplateType.USER,
                                exercises = templateDto.exercises?.map { it.name } ?: emptyList()
                            )
                        )
                    }
                }

                response.data.systemPrograms.forEach { program ->
                    program.templates?.forEach { templateDto ->
                         allTemplates.add(
                            WorkoutTemplate(
                                id = templateDto.id,
                                name = templateDto.name,
                                exerciseCount = templateDto.exerciseCount,
                                description = templateDto.description,
                                lastPerformed = templateDto.lastPerformed,
                                type = TemplateType.SYSTEM,
                                exercises = templateDto.exercises?.map { it.name } ?: emptyList()
                            )
                        )
                    }
                }

                response.data.trainerPrograms.forEach { program ->
                    program.templates?.forEach { templateDto ->
                         allTemplates.add(
                            WorkoutTemplate(
                                id = templateDto.id,
                                name = templateDto.name,
                                exerciseCount = templateDto.exerciseCount,
                                description = templateDto.description,
                                lastPerformed = templateDto.lastPerformed,
                                type = TemplateType.TRAINER,
                                exercises = templateDto.exercises?.map { it.name } ?: emptyList()
                            )
                        )
                    }
                }
                
                allTemplates
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
