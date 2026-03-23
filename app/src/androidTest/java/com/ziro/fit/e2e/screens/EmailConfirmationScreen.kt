package com.ziro.fit.e2e.screens

import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until

class EmailConfirmationScreen(private val device: UiDevice) {

    companion object {
        private const val TIMEOUT_MS = 5_000L
    }

    fun isCheckYourEmailTextVisible(): Boolean {
        return device.hasObject(By.text("Check your email"))
    }

    fun isWeSentVerificationTextVisible(): Boolean {
        return device.hasObject(By.text("We sent a verification link to"))
    }

    fun isOpenEmailAppButtonVisible(): Boolean {
        return device.hasObject(By.text("Open email app"))
    }

    fun isBackToLoginButtonVisible(): Boolean {
        return device.hasObject(By.text("Back to Login"))
    }

    fun clickBackToLogin() {
        val backButton = device.findObject(By.text("Back to Login"))
        backButton?.click()
    }

    fun clickOpenEmailApp() {
        val openEmailButton = device.findObject(By.text("Open email app"))
        openEmailButton?.click()
    }

    fun isEmailDisplayed(expectedEmail: String): Boolean {
        return device.hasObject(By.text(expectedEmail))
    }

    fun waitForScreen(timeoutMs: Long = TIMEOUT_MS): Boolean {
        return device.wait(Until.hasObject(By.text("Check your email")), timeoutMs)
    }
}