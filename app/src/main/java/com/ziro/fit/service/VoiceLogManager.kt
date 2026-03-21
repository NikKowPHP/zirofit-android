package com.ziro.fit.service

import com.ziro.fit.util.search.FuzzySearchEngine
import javax.inject.Inject
import java.util.regex.Pattern

data class ParsedVoiceCommand(
    var exercise: String? = null,
    var sets: Int? = null,
    var reps: Int? = null,
    var weight: Double? = null,
    var matchConfidence: MatchConfidence = MatchConfidence.NONE,
    // Adjustment fields for ADJUST_LAST_SET command type
    var adjustmentWeight: Double? = null,      // e.g., "Add 5kg" -> 5.0
    var adjustmentReps: Int? = null,           // e.g., "Add 2 reps" -> 2
    var isAbsoluteReps: Boolean = false        // true for "Set reps to 10", false for "Add 3 reps"
)

enum class MatchConfidence {
    NONE,
    EXACT,
    STARTS_WITH,
    FUZZY
}

class VoiceLogManager @Inject constructor() {

    private val possibleExercises = listOf(
        "barbell bench press", "inclined bench press", "declined bench press",
        "bench press", "benchpress", "chest press", "dumbbell press",
        "back squat", "front squat", "goblet squat", "hack squat", "squat",
        "romanian deadlift", "sumo deadlift", "deadlift",
        "archer pushup", "diamond pushup", "pushups", "push ups", "push-up", "push-ups",
        "pullups", "pull ups", "chin ups", "lat pulldown",
        "bicep curls", "hammer curls", "preacher curls", "curls",
        "shoulder press", "overhead press", "military press",
        "bent over rows", "seated rows", "cable rows", "rows",
        "walking lunges", "reverse lunges", "lunges",
        "tricep dips", "chest dips", "dips",
        "side plank", "plank",
        "burpees", "jumping jacks", "mountain climbers"
    ).sortedByDescending { it.length }

    fun parseVoiceCommand(text: String): ParsedVoiceCommand {
        val lowerText = text.lowercase()
        var command = ParsedVoiceCommand()

        // === ADJUSTMENT PATTERNS (check these first for ADJUST_LAST_SET commands) ===
        
        // Weight adjustment: "Add 5kg", "Increase 10 kilos", "Up 2.5 kg"
        val weightAdjustPattern = Pattern.compile("(?i)(?:add|increase|up)\\s+(\\d+(?:\\.\\d+)?)\\s*(?:kg|kilos|kilograms)?")
        val weightAdjustMatcher = weightAdjustPattern.matcher(lowerText)
        if (weightAdjustMatcher.find()) {
            val numStr = weightAdjustMatcher.group(1)
            command.adjustmentWeight = numStr?.toDoubleOrNull()
        }
        
        // Reps adjustment (relative): "Add 2 reps", "Increase 5 reps", "Up 3 reps"
        val repsAdjustPattern = Pattern.compile("(?i)(?:add|increase|up)\\s+(\\d+)\\s*(?:reps|rep|repetitions)?")
        val repsAdjustMatcher = repsAdjustPattern.matcher(lowerText)
        if (repsAdjustMatcher.find() && !lowerText.contains("kg") && !lowerText.contains("kilo")) {
            val numStr = repsAdjustMatcher.group(1)
            command.adjustmentReps = numStr?.toIntOrNull()
            command.isAbsoluteReps = false
        }
        
        // Reps absolute set: "Set reps to 10", "Set reps 10"
        val setRepsPattern = Pattern.compile("(?i)set\\s+reps\\s+(?:to\\s+)?(\\d+)")
        val setRepsMatcher = setRepsPattern.matcher(lowerText)
        if (setRepsMatcher.find()) {
            val numStr = setRepsMatcher.group(1)
            command.adjustmentReps = numStr?.toIntOrNull()
            command.isAbsoluteReps = true
        }

        // Standard reps pattern
        val repsPattern = Pattern.compile("(\\d+|one|two|three|four|five|six|seven|eight|nine|ten)\\s*(reps|rep|repetitions|wraps|wrap|rocks|rock|laps|lap)")
        val repsMatcher = repsPattern.matcher(lowerText)
        if (repsMatcher.find()) {
            val numStr = repsMatcher.group(1)
            command.reps = numStr?.toIntOrNull() ?: wordToNumber(numStr ?: "")
        }

        val setsPattern = Pattern.compile("(\\d+|one|two|three|four|five|six|seven|eight|nine|ten)\\s*(sets|set)")
        val setsMatcher = setsPattern.matcher(lowerText)
        if (setsMatcher.find()) {
            val numStr = setsMatcher.group(1)
            command.sets = numStr?.toIntOrNull() ?: wordToNumber(numStr ?: "")
        }

        val weightPattern = Pattern.compile("(\\d+(?:\\.\\d+)?|one|two|three|four|five|six|seven|eight|nine|ten)\\s*(kg|kilograms|kilos|lbs|pounds|lb)")
        val weightMatcher = weightPattern.matcher(lowerText)
        if (weightMatcher.find()) {
            val numStr = weightMatcher.group(1)
            command.weight = numStr?.toDoubleOrNull() ?: wordToNumber(numStr ?: "")?.toDouble()
        }

        if (command.reps == null && command.weight == null) {
            val numbers = extractNumbers(lowerText)
            if (numbers.size >= 2) {
                command.reps = numbers[0].toInt()
                command.weight = numbers[1]
            } else if (numbers.size == 1) {
                command.reps = numbers[0].toInt()
            }
        }

        for (exercise in possibleExercises) {
            if (lowerText.contains(exercise)) {
                command.exercise = exercise.replaceFirstChar { it.uppercase() }
                break
            }
        }

        return command
    }

    private fun extractNumbers(text: String): List<Double> {
        val pattern = Pattern.compile("\\d+(?:\\.\\d+)?")
        val matcher = pattern.matcher(text)
        val numbers = mutableListOf<Double>()
        while (matcher.find()) {
            matcher.group()?.toDoubleOrNull()?.let { numbers.add(it) }
        }
        return numbers
    }

    private fun wordToNumber(word: String): Int? {
        val numberWords = mapOf(
            "one" to 1, "two" to 2, "three" to 3, "four" to 4, "five" to 5,
            "six" to 6, "seven" to 7, "eight" to 8, "nine" to 9, "ten" to 10
        )
        return numberWords[word.lowercase()]
    }

    fun findExerciseMatch(text: String, availableExercises: List<String>): String? {
        val normalizedInput = text.lowercase().replace(Regex("[^a-z0-9]"), "")
        
        val exactMatch = availableExercises.firstOrNull { 
            it.lowercase().replace(Regex("[^a-z0-9]"), "") == normalizedInput
        }
        if (exactMatch != null) return exactMatch

        return availableExercises.lastOrNull()
    }

    // Enhanced matching: exact > starts-with > fuzzy (mirrors iOS matching priority)
    fun findBestExerciseMatch(
        text: String, 
        availableExercises: List<String>,
        fuzzyThreshold: Double = 0.5
    ): Pair<String?, MatchConfidence> {
        val normalizedInput = text.lowercase().replace(Regex("[^a-z0-9]"), "").trim()
        
        // 1. Exact match (normalized)
        val exactMatch = availableExercises.firstOrNull { 
            it.lowercase().replace(Regex("[^a-z0-9]"), "").trim() == normalizedInput
        }
        if (exactMatch != null) {
            return Pair(exactMatch.replaceFirstChar { it.uppercase() }, MatchConfidence.EXACT)
        }

        // 2. Starts-with match
        val startsWithMatch = availableExercises.firstOrNull { 
            it.lowercase().replace(Regex("[^a-z0-9]"), "").trim().startsWith(normalizedInput) ||
            normalizedInput.startsWith(it.lowercase().replace(Regex("[^a-z0-9]"), "").trim())
        }
        if (startsWithMatch != null) {
            return Pair(startsWithMatch.replaceFirstChar { it.uppercase() }, MatchConfidence.STARTS_WITH)
        }

        // 3. Fuzzy match (Levenshtein-based)
        val fuzzyMatch = FuzzySearchEngine.findBestMatch(
            normalizedInput,
            availableExercises.map { it.lowercase().replace(Regex("[^a-z0-9]"), "").trim() },
            threshold = fuzzyThreshold
        )
        if (fuzzyMatch != null) {
            val originalCase = availableExercises.firstOrNull { 
                it.lowercase().replace(Regex("[^a-z0-9]"), "").trim() == fuzzyMatch 
            }
            return Pair(originalCase?.replaceFirstChar { it.uppercase() }, MatchConfidence.FUZZY)
        }

        return Pair(null, MatchConfidence.NONE)
    }
}
