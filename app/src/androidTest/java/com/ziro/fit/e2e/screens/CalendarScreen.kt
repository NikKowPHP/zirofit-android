package com.ziro.fit.e2e.screens

import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until

class CalendarScreen(private val device: UiDevice) {

    companion object {
        private const val TIMEOUT_MS = 5_000L
    }

    /**
     * Checks if the calendar screen is visible.
     */
    fun isVisible(): Boolean {
        return device.wait(Until.hasObject(By.text("Calendar")), TIMEOUT_MS) ||
                device.wait(Until.hasObject(By.text("Schedule")), TIMEOUT_MS) ||
                device.wait(Until.hasObject(By.text("My Calendar")), TIMEOUT_MS)
    }

    /**
     * Checks if the trainer dashboard content is visible.
     */
    fun isDashboardContentVisible(): Boolean {
        return device.hasObject(By.text("Today")) ||
                device.hasObject(By.text("Upcoming")) ||
                device.hasObject(By.text("Upcoming Sessions"))
    }

    /**
     * Checks if the create session button or FAB is visible.
     */
    fun isCreateSessionButtonVisible(): Boolean {
        return device.hasObject(By.text("Create Session")) ||
                device.hasObject(By.desc("Create Session")) ||
                device.hasObject(By.text("New Session"))
    }

    /**
     * Clicks the create session button.
     */
    fun clickCreateSession() {
        val createButton = device.findObject(By.text("Create Session"))
            ?: device.findObject(By.desc("Create Session"))
            ?: device.findObject(By.text("New Session"))
        createButton?.click()
        device.waitForIdle(2000)
    }

    /**
     * Checks if session cards are visible on the calendar.
     */
    fun hasSessionCards(): Boolean {
        return device.hasObject(By.text("Session")) ||
                device.hasObject(By.textContains("Workout"))
    }

    /**
     * Scrolls to the next time period (next day/week).
     */
    fun scrollToNextPeriod() {
        device.swipe(
            device.displayWidth / 4,
            device.displayHeight / 2,
            device.displayWidth * 3 / 4,
            device.displayHeight / 2,
            50
        )
        device.waitForIdle(1000)
    }

    /**
     * Scrolls to the previous time period.
     */
    fun scrollToPreviousPeriod() {
        device.swipe(
            device.displayWidth * 3 / 4,
            device.displayHeight / 2,
            device.displayWidth / 4,
            device.displayHeight / 2,
            50
        )
        device.waitForIdle(1000)
    }

    /**
     * Clicks on a specific date in the calendar.
     */
    fun clickDate(dayOfMonth: String) {
        // Try to find a date text that matches
        val dateElement = device.findObject(By.text(dayOfMonth))
        dateElement?.click()
        device.waitForIdle(1000)
    }

    /**
     * Waits for sessions to load.
     */
    fun waitForSessionsLoad() {
        device.waitForIdle(3000)
    }

    /**
     * Navigates to the clients list from the bottom nav.
     */
    fun navigateToClients() {
        val clientsTab = device.findObject(By.text("Clients"))
            ?: device.findObject(By.text("CLIENTS"))
        clientsTab?.click()
        device.waitForIdle(2000)
    }

    /**
     * Navigates to the "More" menu from bottom nav.
     */
    fun navigateToMore() {
        val moreTab = device.findObject(By.text("More"))
            ?: device.findObject(By.text("MORE"))
        moreTab?.click()
        device.waitForIdle(2000)
    }
}
