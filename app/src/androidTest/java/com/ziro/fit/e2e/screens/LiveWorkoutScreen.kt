package com.ziro.fit.e2e.screens

import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until

class LiveWorkoutScreen(private val device: UiDevice) {

    fun isVisible(): Boolean {
        return device.wait(Until.hasObject(By.text("PERSONAL SESSION")), 5000)
    }

    fun isAddExerciseButtonVisible(): Boolean {
        return device.wait(Until.hasObject(By.text("Add Exercise")), 5000)
    }

    fun isFinishButtonVisible(): Boolean {
        return device.wait(Until.hasObject(By.text("Finish")), 5000)
    }

    fun isCancelButtonVisible(): Boolean {
        return device.wait(Until.hasObject(By.text("Cancel")), 5000)
    }

    fun clickAddExercise() {
        device.findObject(By.text("Add Exercise"))?.click()
        device.waitForIdle(2000)
    }

    fun clickFinish() {
        device.findObject(By.text("Finish"))?.click()
        device.waitForIdle(1000)
    }

    fun clickCancel() {
        device.findObject(By.text("Cancel"))?.click()
        device.waitForIdle(1000)
    }

    fun confirmCancel() {
        device.findObject(By.text("Discard"))?.click()
        device.waitForIdle(2000)
    }

    fun isCancelDialogVisible(): Boolean {
        return device.wait(Until.hasObject(By.text("Cancel Workout?")), 5000)
    }

    fun isEmptyStateVisible(): Boolean {
        return device.wait(Until.hasObject(By.text("Start by adding an exercise")), 5000)
    }

    fun isExerciseVisible(exerciseName: String): Boolean {
        return device.wait(Until.hasObject(By.textContains(exerciseName)), 5000)
    }

    fun isExerciseBrowserVisible(): Boolean {
        return device.wait(Until.hasObject(By.clazz("android.widget.EditText")), 5000)
    }

    fun searchExercise(exerciseName: String) {
        val searchInput = device.findObject(UiSelector().className("android.widget.EditText"))
        searchInput?.setText(exerciseName)
        device.waitForIdle(2000)
    }

    fun selectExerciseFromBrowser(exerciseName: String) {
        device.findObject(By.textContains(exerciseName))?.click()
        device.waitForIdle(1000)
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
