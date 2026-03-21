package com.ziro.fit.model

/**
 * Represents the two isolated app modes, mirroring the iOS AppMode enum.
 * - Professional (trainer): Trainer/Center Manager dashboard, client calendar, programs
 * - Personal (ziroMe): Client/athlete dashboard, workout history, analytics
 */
enum class AppMode(val displayName: String) {
    TRAINER("Professional"),
    PERSONAL("Personal");

    companion object {
        private const val PREFS_KEY = "active_mode"

        fun fromString(value: String?): AppMode {
            return when (value?.lowercase()) {
                "trainer", "professional" -> TRAINER
                "personal" -> PERSONAL
                else -> TRAINER
            }
        }
    }
}
