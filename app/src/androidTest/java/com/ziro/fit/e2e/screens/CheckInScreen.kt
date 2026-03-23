package com.ziro.fit.e2e.screens

import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until

class CheckInScreen(private val device: UiDevice) {

    fun isListVisible(): Boolean {
        return device.wait(Until.hasObject(By.text("My Check-Ins")), 5000)
    }

    fun isCheckInNowVisible(): Boolean {
        return device.hasObject(By.text("Check-In Now")) || device.hasObject(By.text("Check-In Overdue"))
    }

    fun clickCheckInNow() {
        val checkInButton = device.findObject(By.text("Check-In Now"))
        if (checkInButton != null) {
            checkInButton.click()
        } else {
            device.findObject(By.text("Check-In Overdue"))?.click()
        }
        device.waitForIdle(2000)
    }

    fun isSubmissionVisible(): Boolean {
        return device.wait(Until.hasObject(By.text("New Check-In")), 5000)
    }

    fun isSubmitButtonVisible(): Boolean {
        return device.wait(Until.hasObject(By.text("Submit Check-In")), 5000)
    }

    fun enterWeight(weight: String) {
        val weightField = device.findObject(By.clazz("android.widget.EditText"))
        weightField?.text = weight
        device.waitForIdle(500)
    }

    fun enterSleep(sleep: String) {
        val sleepField = device.findObject(By.clazz("android.widget.EditText"))
        sleepField?.text = sleep
        device.waitForIdle(500)
    }

    fun setEnergy(level: Int) {
        val slider = device.findObject(By.clazz("android.widget.SeekBar"))
        if (slider != null) {
            val bounds = slider.visibleBounds
            val cx = (bounds.left + bounds.right) / 2
            val cy = (bounds.top + bounds.bottom) / 2
            val targetX = cx + ((level - 5) * 50)
            device.swipe(cx, cy, targetX, cy, 10)
        }
        device.waitForIdle(500)
    }

    fun setStress(level: Int) {
        val slider = device.findObject(By.clazz("android.widget.SeekBar"))
        if (slider != null) {
            val bounds = slider.visibleBounds
            val cx = (bounds.left + bounds.right) / 2
            val cy = (bounds.top + bounds.bottom) / 2
            val targetX = cx + ((level - 5) * 50)
            device.swipe(cx, cy, targetX, cy, 10)
        }
        device.waitForIdle(500)
    }

    fun enterNotes(notes: String) {
        val notesField = device.findObject(By.clazz("android.widget.EditText"))
        notesField?.text = notes
        device.waitForIdle(500)
    }

    fun clickSubmit() {
        device.findObject(By.text("Submit Check-In"))?.click()
        device.waitForIdle(3000)
    }

    fun isSubmissionSuccessful(): Boolean {
        return isListVisible()
    }

    fun scrollDown() {
        device.swipe(
            device.displayWidth / 2,
            device.displayHeight * 3 / 4,
            device.displayWidth / 2,
            device.displayHeight / 4,
            50
        )
        device.waitForIdle(500)
    }
}
