package com.ziro.fit.e2e.screens

import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until

class ExploreScreen(private val device: UiDevice) {

    fun isVisible(): Boolean {
        return isFeaturedTrainersVisible() || isEventsSectionVisible()
    }

    fun isFeaturedTrainersVisible(): Boolean {
        return device.wait(Until.hasObject(By.text("Featured Trainers")), 5000)
    }

    fun isEventsSectionVisible(): Boolean {
        return device.wait(Until.hasObject(By.text("Events")), 5000)
    }

    fun isAllSpecialistsVisible(): Boolean {
        return device.wait(Until.hasObject(By.text("All Specialists")), 5000) ||
                device.wait(Until.hasObject(By.text("Find a Specialist")), 5000)
    }

    fun clickSeeAllTrainers() {
        device.findObject(By.text("See all"))?.click()
        device.waitForIdle(2000)
    }

    fun hasTrainerCards(): Boolean {
        return isFeaturedTrainersVisible() || isAllSpecialistsVisible()
    }

    fun hasEventCards(): Boolean {
        return isEventsSectionVisible()
    }

    fun isLoading(): Boolean {
        return device.wait(Until.hasObject(By.clazz("android.widget.ProgressBar")), 1000)
    }

    fun waitForContentLoad() {
        var attempts = 0
        while (isLoading() && attempts < 10) {
            device.waitForIdle(500)
            attempts++
        }
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
