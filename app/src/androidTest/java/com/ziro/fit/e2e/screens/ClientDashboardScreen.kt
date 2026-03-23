package com.ziro.fit.e2e.screens

import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice

class ClientDashboardScreen(private val device: UiDevice) {

    fun clickOverviewTab() {
        device.findObject(By.text("Overview"))?.click()
        device.waitForIdle(1000)
    }

    fun clickHistoryTab() {
        device.findObject(By.text("History"))?.click()
        device.waitForIdle(1000)
    }

    fun clickStatsTab() {
        device.findObject(By.text("Stats"))?.click()
        device.waitForIdle(1000)
    }

    fun isOverviewTabVisible(): Boolean {
        return device.hasObject(By.text("Overview"))
    }

    fun isHistoryTabVisible(): Boolean {
        return device.hasObject(By.text("History"))
    }

    fun isStatsTabVisible(): Boolean {
        return device.hasObject(By.text("Stats"))
    }

    fun areTabsVisible(): Boolean {
        return isOverviewTabVisible() && isHistoryTabVisible() && isStatsTabVisible()
    }

    fun clickExploreEvents() {
        device.findObject(By.text("Explore Events"))?.click()
        device.waitForIdle(2000)
    }

    fun clickAICoach() {
        device.findObject(By.text("Start AI Coach"))?.click()
        device.waitForIdle(2000)
    }

    fun clickViewCheckIns() {
        device.findObject(By.text("View Check-Ins"))?.click()
        device.waitForIdle(2000)
    }

    fun clickFindTrainer() {
        device.findObject(By.text("Find a Trainer"))?.click()
        device.waitForIdle(2000)
    }

    fun isEventsSectionVisible(): Boolean {
        return device.hasObject(By.text("Events"))
    }

    fun isAICoachVisible(): Boolean {
        return device.hasObject(By.text("AI Coach"))
    }

    fun isCheckInSectionVisible(): Boolean {
        return device.hasObject(By.text("Weekly Check-In"))
    }

    fun isVisible(): Boolean {
        return isOverviewTabVisible()
    }

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
