package com.ziro.fit.service

import android.content.Context
import com.ziro.fit.data.remote.ZiroApi
import com.ziro.fit.model.DailyTarget
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.lang.reflect.Method

class DailyTargetManagerTest {
    private lateinit var manager: DailyTargetManager
    private lateinit var targetsToJsonMethod: Method
    private lateinit var parseTargetsFromJsonMethod: Method

    @Before
    fun setup() {
        val mockContext = mockk<Context>(relaxed = true)
        val mockApi = mockk<ZiroApi>(relaxed = true)
        manager = DailyTargetManager(mockContext, mockApi)

        targetsToJsonMethod = DailyTargetManager::class.java.getDeclaredMethod("targetsToJson", List::class.java)
        targetsToJsonMethod.isAccessible = true

        parseTargetsFromJsonMethod = DailyTargetManager::class.java.getDeclaredMethod("parseTargetsFromJson", String::class.java)
        parseTargetsFromJsonMethod.isAccessible = true
    }

    @Test
    fun isTargetMet_returnsTrueWhenCurrentExceedsGoal() {
        val target = DailyTarget(
            id = "1",
            type = "reps",
            goal = 10,
            current = 15,
            exerciseId = null,
            exerciseName = null,
            isCompleted = false,
            date = "2024-01-01"
        )
        assertTrue(manager.isTargetMet(target))
    }

    @Test
    fun isTargetMet_returnsFalseWhenCurrentBelowGoal() {
        val target = DailyTarget(
            id = "1",
            type = "reps",
            goal = 10,
            current = 5,
            exerciseId = null,
            exerciseName = null,
            isCompleted = false,
            date = "2024-01-01"
        )
        assertFalse(manager.isTargetMet(target))
    }

    @Test
    fun isTargetMet_returnsTrueWhenExactlyEqual() {
        val target = DailyTarget(
            id = "1",
            type = "reps",
            goal = 10,
            current = 10,
            exerciseId = null,
            exerciseName = null,
            isCompleted = true,
            date = "2024-01-01"
        )
        assertTrue(manager.isTargetMet(target))
    }

    @Test
    fun getTargetProgress_returnsCorrectRatio() {
        val target = DailyTarget(
            id = "1",
            type = "reps",
            goal = 100,
            current = 50,
            exerciseId = null,
            exerciseName = null,
            isCompleted = false,
            date = "2024-01-01"
        )
        assertEquals(0.5f, manager.getTargetProgress(target), 0.001f)
    }

    @Test
    fun getTargetProgress_returnsZeroWhenGoalIsZero() {
        val target = DailyTarget(
            id = "1",
            type = "reps",
            goal = 0,
            current = 50,
            exerciseId = null,
            exerciseName = null,
            isCompleted = false,
            date = "2024-01-01"
        )
        assertEquals(0f, manager.getTargetProgress(target), 0.001f)
    }

    @Test
    fun getTargetProgress_clampsToMaximumOne() {
        val target = DailyTarget(
            id = "1",
            type = "reps",
            goal = 10,
            current = 100,
            exerciseId = null,
            exerciseName = null,
            isCompleted = true,
            date = "2024-01-01"
        )
        assertEquals(1.0f, manager.getTargetProgress(target), 0.001f)
    }

    @Test
    fun targetsToJson_serializesToPipeDelimitedString() {
        val targets = listOf(
            DailyTarget(
                id = "t1",
                type = "reps",
                goal = 10,
                current = 5,
                exerciseId = "ex1",
                exerciseName = "Pushups",
                isCompleted = false,
                date = "2024-01-01"
            )
        )
        val json = targetsToJsonMethod.invoke(manager, targets) as String
        assertTrue(json.contains("t1"))
        assertTrue(json.contains("reps"))
        assertTrue(json.contains("10"))
        assertTrue(json.contains("5"))
        assertTrue(json.contains("ex1"))
        assertTrue(json.contains("Pushups"))
        assertTrue(json.contains("false"))
        assertTrue(json.contains("2024-01-01"))
    }

    @Test
    fun parseTargetsFromJson_deserializesBackToList() {
        val originalTargets = listOf(
            DailyTarget(
                id = "t1",
                type = "reps",
                goal = 10,
                current = 5,
                exerciseId = "ex1",
                exerciseName = "Pushups",
                isCompleted = false,
                date = "2024-01-01"
            )
        )
        val json = targetsToJsonMethod.invoke(manager, originalTargets) as String
        val parsed = parseTargetsFromJsonMethod.invoke(manager, json) as List<DailyTarget>
        assertEquals(1, parsed.size)
        assertEquals("t1", parsed[0].id)
        assertEquals("reps", parsed[0].type)
        assertEquals(10, parsed[0].goal)
        assertEquals(5, parsed[0].current)
        assertEquals("ex1", parsed[0].exerciseId)
        assertEquals("Pushups", parsed[0].exerciseName)
        assertFalse(parsed[0].isCompleted)
        assertEquals("2024-01-01", parsed[0].date)
    }

    @Test
    fun parseTargetsFromJson_returnsEmptyListForBlankString() {
        val result = parseTargetsFromJsonMethod.invoke(manager, "") as List<DailyTarget>
        assertTrue(result.isEmpty())
    }

    @Test
    fun parseTargetsFromJson_returnsEmptyListForWhitespaceString() {
        val result = parseTargetsFromJsonMethod.invoke(manager, "   ") as List<DailyTarget>
        assertTrue(result.isEmpty())
    }

    @Test
    fun parseTargetsFromJson_returnsEmptyListForEmptyArray() {
        val result = parseTargetsFromJsonMethod.invoke(manager, "[]") as List<DailyTarget>
        assertTrue(result.isEmpty())
    }

    @Test
    fun parseTargetsFromJson_skipsMalformedEntry() {
        val json = "validid|reps|10|5|ex1|Ex|true|2024-01-01;incomplete"
        val result = parseTargetsFromJsonMethod.invoke(manager, json) as List<DailyTarget>
        assertEquals(1, result.size)
        assertEquals("validid", result[0].id)
    }

    @Test
    fun parseTargetsFromJson_parsesNullExerciseId() {
        val json = "t1|reps|10|5||ExName|true|2024-01-01"
        val result = parseTargetsFromJsonMethod.invoke(manager, json) as List<DailyTarget>
        assertEquals(1, result.size)
        assertNull(result[0].exerciseId)
    }

    @Test
    fun parseTargetsFromJson_parsesBooleanTrue() {
        val json = "t1|reps|10|5|||true|2024-01-01"
        val result = parseTargetsFromJsonMethod.invoke(manager, json) as List<DailyTarget>
        assertEquals(1, result.size)
        assertTrue(result[0].isCompleted)
    }

    @Test
    fun parseTargetsFromJson_parsesBooleanFalse() {
        val json = "t1|reps|10|5|||false|2024-01-01"
        val result = parseTargetsFromJsonMethod.invoke(manager, json) as List<DailyTarget>
        assertEquals(1, result.size)
        assertFalse(result[0].isCompleted)
    }
}