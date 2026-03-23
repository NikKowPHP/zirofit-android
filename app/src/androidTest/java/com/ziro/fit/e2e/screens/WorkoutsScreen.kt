package com.ziro.fit.e2e.screens

import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until

class WorkoutsScreen(private val device: UiDevice) {

    fun isTitleVisible(): Boolean {
        return device.wait(Until.hasObject(By.text("Workout")), 5000)
    }

    fun clickStartEmptyWorkout() {
        device.findObject(By.text("START AN EMPTY WORKOUT"))?.click()
        device.waitForIdle(3000)
    }

    fun isQuickStartVisible(): Boolean {
        return device.hasObject(By.text("Quick start"))
    }

    fun isProgramsSectionVisible(): Boolean {
        return device.hasObject(By.text("Programs"))
    }

    fun isTemplatesSectionVisible(): Boolean {
        return device.hasObject(By.text("Templates"))
    }

    fun isVisible(): Boolean {
        return isTitleVisible()
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
