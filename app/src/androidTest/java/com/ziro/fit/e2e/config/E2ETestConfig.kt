package com.ziro.fit.e2e.config

import android.os.Bundle
import androidx.test.platform.app.InstrumentationRegistry

/**
 * Test configuration for E2E tests.
 * Reads from testInstrumentationRunnerArguments (set in build.gradle.kts via local.properties).
 * Falls back to hardcoded defaults for local development.
 */
object E2ETestConfig {

    private val arguments: Bundle by lazy {
        InstrumentationRegistry.getArguments()
    }

    private fun getArg(key: String, default: String): String =
        arguments.getString(key) ?: default

    val TRAINER_EMAIL: String get() = getArg("E2E_TRAINER_EMAIL", "e2e-test-trainer@ziro.fit")
    val TRAINER_PASSWORD: String get() = getArg("E2E_TRAINER_PASSWORD", "test-password-123")

    val CLIENT_EMAIL: String get() = getArg("E2E_CLIENT_EMAIL", "calendar.client@test.com")
    val CLIENT_PASSWORD: String get() = getArg("E2E_CLIENT_PASSWORD", "test-password-123")

    const val PACKAGE_NAME = "com.ziro.fit"
    const val LOGIN_TIMEOUT_MS = 15_000L
    const val SCREEN_TRANSITION_TIMEOUT_MS = 5_000L
    const val IDLE_TIMEOUT_MS = 2_000L
}
