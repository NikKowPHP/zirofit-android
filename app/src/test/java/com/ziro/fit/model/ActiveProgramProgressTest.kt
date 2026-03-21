package com.ziro.fit.model

import org.junit.Assert.*
import org.junit.Test

class ActiveProgramProgressTest {
    @Test
    fun `fromApiResponse with nextTemplateId maps currentTemplate correctly`() {
        val response = ActiveProgramApiResponse(
            program = ActiveProgramInfo(id = "prog1", name = "Bulk", description = "12 week bulk"),
            progress = ProgramProgressInfo(completedCount = 3, totalCount = 12, progressPercentage = 0.25f, nextTemplateId = "tmpl2"),
            templates = listOf(
                ProgramTemplateDto(id = "tmpl1", name = "Workout A", description = "Push day", order = 0, status = "COMPLETED", exerciseCount = 6),
                ProgramTemplateDto(id = "tmpl2", name = "Workout B", description = "Pull day", order = 1, status = "NEXT", exerciseCount = 7)
            )
        )

        val result = ActiveProgramProgress.fromApiResponse(response)

        assertEquals("prog1", result.programId)
        assertEquals("Bulk", result.programName)
        assertEquals("12 week bulk", result.programDescription)
        assertEquals(0.25f, result.progressPercentage)
        assertNotNull(result.currentTemplate)
        assertEquals("tmpl2", result.currentTemplate!!.id)
        assertEquals("Workout B", result.currentTemplate!!.name)
        assertEquals("Pull day", result.currentTemplate!!.description)
        assertEquals(7, result.currentTemplate!!.exerciseCount)
        assertEquals(2, result.templateStatuses.size)
    }

    @Test
    fun `fromApiResponse with nextTemplateId null maps currentTemplate as null`() {
        val response = ActiveProgramApiResponse(
            program = ActiveProgramInfo(id = "prog1", name = "Test Program", description = null),
            progress = ProgramProgressInfo(completedCount = 10, totalCount = 10, progressPercentage = 1.0f, nextTemplateId = null),
            templates = listOf(
                ProgramTemplateDto(id = "tmpl1", name = "Final", description = "Last one", order = 0, status = "COMPLETED", exerciseCount = 5)
            )
        )

        val result = ActiveProgramProgress.fromApiResponse(response)

        assertNull(result.currentTemplate)
    }

    @Test
    fun `fromApiResponse with nextTemplateId not found in templates maps currentTemplate as null`() {
        val response = ActiveProgramApiResponse(
            program = ActiveProgramInfo(id = "prog1", name = "Test Program", description = null),
            progress = ProgramProgressInfo(completedCount = 1, totalCount = 5, progressPercentage = 0.2f, nextTemplateId = "nonexistent"),
            templates = listOf(
                ProgramTemplateDto(id = "tmpl1", name = "Workout 1", description = "First", order = 0, status = "COMPLETED", exerciseCount = 4),
                ProgramTemplateDto(id = "tmpl2", name = "Workout 2", description = "Second", order = 1, status = "COMPLETED", exerciseCount = 5)
            )
        )

        val result = ActiveProgramProgress.fromApiResponse(response)

        assertNull(result.currentTemplate)
    }

    @Test
    fun `fromApiResponse maps program id name description correctly`() {
        val response = ActiveProgramApiResponse(
            program = ActiveProgramInfo(id = "custom-id", name = "Custom Program", description = "Custom description"),
            progress = ProgramProgressInfo(completedCount = 0, totalCount = 4, progressPercentage = 0.0f, nextTemplateId = "tmpl1"),
            templates = listOf(
                ProgramTemplateDto(id = "tmpl1", name = "First", description = null, order = 0, status = "NEXT", exerciseCount = 3)
            )
        )

        val result = ActiveProgramProgress.fromApiResponse(response)

        assertEquals("custom-id", result.programId)
        assertEquals("Custom Program", result.programName)
        assertEquals("Custom description", result.programDescription)
    }

    @Test
    fun `fromApiResponse maps progressPercentage correctly`() {
        val response = ActiveProgramApiResponse(
            program = ActiveProgramInfo(id = "prog1", name = "Test", description = null),
            progress = ProgramProgressInfo(completedCount = 5, totalCount = 8, progressPercentage = 0.625f, nextTemplateId = "tmpl2"),
            templates = listOf(
                ProgramTemplateDto(id = "tmpl1", name = "A", description = null, order = 0, status = "COMPLETED", exerciseCount = 5),
                ProgramTemplateDto(id = "tmpl2", name = "B", description = null, order = 1, status = "NEXT", exerciseCount = 6)
            )
        )

        val result = ActiveProgramProgress.fromApiResponse(response)

        assertEquals(0.625f, result.progressPercentage)
    }

    @Test
    fun `fromApiResponse maps all templates to TemplateStatus list with correct ids and statuses`() {
        val response = ActiveProgramApiResponse(
            program = ActiveProgramInfo(id = "prog1", name = "Test", description = null),
            progress = ProgramProgressInfo(completedCount = 2, totalCount = 5, progressPercentage = 0.4f, nextTemplateId = "tmpl3"),
            templates = listOf(
                ProgramTemplateDto(id = "tmpl1", name = "Workout 1", description = null, order = 0, status = "COMPLETED", exerciseCount = 5),
                ProgramTemplateDto(id = "tmpl2", name = "Workout 2", description = null, order = 1, status = "COMPLETED", exerciseCount = 6),
                ProgramTemplateDto(id = "tmpl3", name = "Workout 3", description = null, order = 2, status = "NEXT", exerciseCount = 7),
                ProgramTemplateDto(id = "tmpl4", name = "Workout 4", description = null, order = 3, status = "NOT_STARTED", exerciseCount = 8),
                ProgramTemplateDto(id = "tmpl5", name = "Workout 5", description = null, order = 4, status = "NOT_STARTED", exerciseCount = 9)
            )
        )

        val result = ActiveProgramProgress.fromApiResponse(response)

        assertEquals(5, result.templateStatuses.size)
        assertEquals("tmpl1", result.templateStatuses[0].templateId)
        assertEquals("COMPLETED", result.templateStatuses[0].status)
        assertEquals("tmpl2", result.templateStatuses[1].templateId)
        assertEquals("COMPLETED", result.templateStatuses[1].status)
        assertEquals("tmpl3", result.templateStatuses[2].templateId)
        assertEquals("NEXT", result.templateStatuses[2].status)
        assertEquals("tmpl4", result.templateStatuses[3].templateId)
        assertEquals("NOT_STARTED", result.templateStatuses[3].status)
        assertEquals("tmpl5", result.templateStatuses[4].templateId)
        assertEquals("NOT_STARTED", result.templateStatuses[4].status)
    }

    @Test
    fun `fromApiResponse maps lastCompletedAt as null in all TemplateStatus`() {
        val response = ActiveProgramApiResponse(
            program = ActiveProgramInfo(id = "prog1", name = "Test", description = null),
            progress = ProgramProgressInfo(completedCount = 1, totalCount = 3, progressPercentage = 0.33f, nextTemplateId = "tmpl2"),
            templates = listOf(
                ProgramTemplateDto(id = "tmpl1", name = "A", description = null, order = 0, status = "COMPLETED", exerciseCount = 5),
                ProgramTemplateDto(id = "tmpl2", name = "B", description = null, order = 1, status = "NEXT", exerciseCount = 6),
                ProgramTemplateDto(id = "tmpl3", name = "C", description = null, order = 2, status = "NOT_STARTED", exerciseCount = 7)
            )
        )

        val result = ActiveProgramProgress.fromApiResponse(response)

        result.templateStatuses.forEach { status ->
            assertNull(status.lastCompletedAt)
        }
    }

    @Test
    fun `fromApiResponse with empty templates list maps templateStatuses as empty`() {
        val response = ActiveProgramApiResponse(
            program = ActiveProgramInfo(id = "prog1", name = "Empty Program", description = null),
            progress = ProgramProgressInfo(completedCount = 0, totalCount = 0, progressPercentage = 0.0f, nextTemplateId = null),
            templates = emptyList()
        )

        val result = ActiveProgramProgress.fromApiResponse(response)

        assertTrue(result.templateStatuses.isEmpty())
        assertNull(result.currentTemplate)
    }

    @Test
    fun `fromApiResponse with single template maps templateStatuses with one entry`() {
        val response = ActiveProgramApiResponse(
            program = ActiveProgramInfo(id = "prog1", name = "Single", description = null),
            progress = ProgramProgressInfo(completedCount = 0, totalCount = 1, progressPercentage = 0.0f, nextTemplateId = "tmpl1"),
            templates = listOf(
                ProgramTemplateDto(id = "tmpl1", name = "Only One", description = "Solo workout", order = 0, status = "NEXT", exerciseCount = 10)
            )
        )

        val result = ActiveProgramProgress.fromApiResponse(response)

        assertEquals(1, result.templateStatuses.size)
        assertEquals("tmpl1", result.templateStatuses[0].templateId)
        assertEquals("NEXT", result.templateStatuses[0].status)
    }

    @Test
    fun `fromApiResponse currentTemplate uses only id name description exerciseCount from source template`() {
        val response = ActiveProgramApiResponse(
            program = ActiveProgramInfo(id = "prog1", name = "Test", description = null),
            progress = ProgramProgressInfo(completedCount = 0, totalCount = 2, progressPercentage = 0.0f, nextTemplateId = "tmpl1"),
            templates = listOf(
                ProgramTemplateDto(
                    id = "tmpl1",
                    name = "Test Template",
                    description = "Test Description",
                    order = 5,
                    status = "NEXT",
                    exerciseCount = 12
                )
            )
        )

        val result = ActiveProgramProgress.fromApiResponse(response)

        assertNotNull(result.currentTemplate)
        assertEquals("tmpl1", result.currentTemplate!!.id)
        assertEquals("Test Template", result.currentTemplate!!.name)
        assertEquals("Test Description", result.currentTemplate!!.description)
        assertEquals(12, result.currentTemplate!!.exerciseCount)
        assertNull(result.currentTemplate!!.lastPerformed)
        assertNull(result.currentTemplate!!.exercises)
        assertNull(result.currentTemplate!!._count)
    }
}
