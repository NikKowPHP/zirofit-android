package com.ziro.fit.e2e.screens

import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until

class MoreScreen(private val device: UiDevice) {

    companion object {
        private const val TIMEOUT_MS = 5_000L
    }

    /**
     * Checks if the More screen is visible.
     */
    fun isVisible(): Boolean {
        return device.wait(Until.hasObject(By.text("More")), TIMEOUT_MS) ||
                device.wait(Until.hasObject(By.text("Settings")), TIMEOUT_MS) ||
                device.wait(Until.hasObject(By.text("Account")), TIMEOUT_MS)
    }

    /**
     * Navigates to Assessments library.
     */
    fun clickAssessments() {
        val assessmentsItem = device.findObject(By.text("Assessments"))
            ?: device.findObject(By.textContains("Assessment"))
        assessmentsItem?.click()
        device.waitForIdle(2000)
    }

    /**
     * Navigates to Bookings list.
     */
    fun clickBookings() {
        val bookingsItem = device.findObject(By.text("Bookings"))
            ?: device.findObject(By.text("Manage Bookings"))
        bookingsItem?.click()
        device.waitForIdle(2000)
    }

    /**
     * Navigates to Check-ins list.
     */
    fun clickCheckIns() {
        val checkInsItem = device.findObject(By.text("Check-Ins"))
            ?: device.findObject(By.text("Client Check-Ins"))
        checkInsItem?.click()
        device.waitForIdle(2000)
    }

    /**
     * Navigates to Events list.
     */
    fun clickEvents() {
        val eventsItem = device.findObject(By.text("Events"))
            ?: device.findObject(By.text("Community Events"))
        eventsItem?.click()
        device.waitForIdle(2000)
    }

    /**
     * Navigates to Profile settings.
     */
    fun clickProfile() {
        val profileItem = device.findObject(By.text("Profile"))
            ?: device.findObject(By.text("My Profile"))
            ?: device.findObject(By.text("Account"))
        profileItem?.click()
        device.waitForIdle(2000)
    }

    /**
     * Checks if the Profile option is visible.
     */
    fun isProfileOptionVisible(): Boolean {
        return device.hasObject(By.text("Profile")) ||
                device.hasObject(By.text("My Profile"))
    }

    /**
     * Checks if Assessments option is visible.
     */
    fun isAssessmentsOptionVisible(): Boolean {
        return device.hasObject(By.text("Assessments")) ||
                device.hasObject(By.textContains("Assessment"))
    }

    /**
     * Checks if Bookings option is visible.
     */
    fun isBookingsOptionVisible(): Boolean {
        return device.hasObject(By.text("Bookings"))
    }

    /**
     * Navigates back.
     */
    fun navigateBack() {
        device.pressBack()
        device.waitForIdle(1000)
    }

    /**
     * Scrolls down in the more menu.
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
