package com.ziro.fit.e2e.screens

import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until

class ClientDetailsScreen(private val device: UiDevice) {

    companion object {
        private const val TIMEOUT_MS = 5_000L
    }

    /**
     * Checks if the client details screen is visible.
     */
    fun isVisible(): Boolean {
        return device.wait(Until.hasObject(By.text("Client Details")), TIMEOUT_MS) ||
                device.wait(Until.hasObject(By.text("Sessions")), TIMEOUT_MS) ||
                device.wait(Until.hasObject(By.text("Programs")), TIMEOUT_MS)
    }

    /**
     * Checks if the client's name is visible.
     */
    fun isClientNameVisible(): Boolean {
        return device.hasObject(By.text("Calendar Test Client")) ||
                device.hasObject(By.text("Test Client"))
    }

    /**
     * Navigates to Sessions tab/section.
     */
    fun clickSessions() {
        val sessionsTab = device.findObject(By.text("Sessions"))
            ?: device.findObject(By.text("Workout Sessions"))
        sessionsTab?.click()
        device.waitForIdle(2000)
    }

    /**
     * Navigates to Programs tab/section.
     */
    fun clickPrograms() {
        val programsTab = device.findObject(By.text("Programs"))
            ?: device.findObject(By.text("Workout Programs"))
        programsTab?.click()
        device.waitForIdle(2000)
    }

    /**
     * Navigates to Assessments tab/section.
     */
    fun clickAssessments() {
        val assessmentsTab = device.findObject(By.text("Assessments"))
            ?: device.findObject(By.text("Fitness Assessments"))
        assessmentsTab?.click()
        device.waitForIdle(2000)
    }

    /**
     * Navigates to Measurements section.
     */
    fun clickMeasurements() {
        val measurementsTab = device.findObject(By.text("Measurements"))
        measurementsTab?.click()
        device.waitForIdle(2000)
    }

    /**
     * Navigates to Transformation Photos section.
     */
    fun clickPhotos() {
        val photosTab = device.findObject(By.text("Photos"))
            ?: device.findObject(By.text("Transformation Photos"))
        photosTab?.click()
        device.waitForIdle(2000)
    }

    /**
     * Checks if the Chat button is visible.
     */
    fun isChatButtonVisible(): Boolean {
        return device.hasObject(By.text("Chat")) ||
                device.hasObject(By.desc("Chat with client"))
    }

    /**
     * Clicks the Chat button.
     */
    fun clickChat() {
        val chatButton = device.findObject(By.text("Chat"))
            ?: device.findObject(By.desc("Chat with client"))
            ?: device.findObject(By.desc("Message"))
        chatButton?.click()
        device.waitForIdle(2000)
    }

    /**
     * Navigates back to clients list.
     */
    fun navigateBack() {
        device.pressBack()
        device.waitForIdle(1000)
    }

    /**
     * Scrolls down on the client details screen.
     */
    fun scrollDown() {
        device.swipe(
            device.displayWidth / 2,
            device.displayHeight * 3 / 4,
            device.displayWidth / 2,
            device.displayHeight / 4,
            50
        )
        device.waitForIdle(1000)
    }
}
