package com.ziro.fit.e2e.screens

import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until

class RoleSelectionScreen(private val device: UiDevice) {

    companion object {
        private const val TIMEOUT_MS = 5_000L
    }

    fun isVisible(): Boolean {
        return device.wait(Until.hasObject(By.text("Choose Your Role")), TIMEOUT_MS) ||
                device.wait(Until.hasObject(By.text("How will you use ZIRO.FIT?")), TIMEOUT_MS) ||
                device.wait(Until.hasObject(By.text("I'm a")), TIMEOUT_MS)
    }

    fun selectTrainerRole() {
        val trainerButton = device.findObject(By.text("Trainer"))
            ?: device.findObject(By.text("I am a Trainer"))
            ?: device.findObject(By.text("Trainer Mode"))
            ?: device.findObject(By.textContains("Trainer"))
        trainerButton?.click()
        device.waitForIdle(3000)
    }

    fun selectClientRole() {
        val clientButton = device.findObject(By.text("Client"))
            ?: device.findObject(By.text("I am a Client"))
            ?: device.findObject(By.text("Client Mode"))
            ?: device.findObject(By.text("Personal"))
            ?: device.findObject(By.textContains("Personal"))
            ?: device.findObject(By.textContains("Client"))
        clientButton?.click()
        device.waitForIdle(3000)
    }

    fun isTrainerOptionVisible(): Boolean {
        return device.hasObject(By.text("Trainer")) ||
                device.hasObject(By.text("I am a Trainer"))
    }

    fun isClientOptionVisible(): Boolean {
        return device.hasObject(By.text("Client")) ||
                device.hasObject(By.text("I am a Client")) ||
                device.hasObject(By.text("Personal"))
    }

    fun waitForOnboardingComplete(timeoutMs: Long = 15_000L): Boolean {
        return device.wait(Until.hasObject(By.text("Overview")), timeoutMs) ||
                device.wait(Until.hasObject(By.text("Calendar")), timeoutMs) ||
                device.wait(Until.hasObject(By.text("My Clients")), timeoutMs) ||
                device.wait(Until.hasObject(By.text("Home")), timeoutMs)
    }

    fun isSkipButtonVisible(): Boolean {
        return device.hasObject(By.text("Skip")) ||
                device.hasObject(By.text("Skip for now"))
    }

    fun clickSkip() {
        val skipButton = device.findObject(By.text("Skip"))
            ?: device.findObject(By.text("Skip for now"))
        skipButton?.click()
        device.waitForIdle(2000)
    }
}
