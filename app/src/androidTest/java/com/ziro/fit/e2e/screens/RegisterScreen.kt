package com.ziro.fit.e2e.screens

import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until

class RegisterScreen(private val device: UiDevice) {

    companion object {
        private const val TIMEOUT_MS = 5_000L
    }

    fun enterName(name: String) {
        val editTexts = device.findObjects(By.clazz("android.widget.EditText"))
        if (editTexts.isNotEmpty()) {
            editTexts[0].text = name
        }
    }

    fun enterEmail(email: String) {
        val editTexts = device.findObjects(By.clazz("android.widget.EditText"))
        if (editTexts.size > 1) {
            editTexts[1].text = email
        }
    }

    fun enterPassword(password: String) {
        val editTexts = device.findObjects(By.clazz("android.widget.EditText"))
        if (editTexts.size > 2) {
            editTexts[2].text = password
        }
    }

    fun enterConfirmPassword(password: String) {
        val editTexts = device.findObjects(By.clazz("android.widget.EditText"))
        if (editTexts.size > 3) {
            editTexts[3].text = password
        }
    }

    fun clickCreateAccount() {
        val createButton = device.findObject(By.text("Create Account"))
        createButton?.click()
    }

    fun clickLoginLink() {
        val loginLink = device.findObject(By.text("Log In"))
        loginLink?.click()
    }

    fun isCreateAccountButtonVisible(): Boolean {
        return device.hasObject(By.text("Create Account"))
    }

    fun isLoginLinkVisible(): Boolean {
        return device.hasObject(By.text("Log In"))
    }

    fun isErrorVisible(): Boolean {
        return device.hasObject(By.text("Error"))
    }

    fun isJoinZiroFitTextVisible(): Boolean {
        return device.hasObject(By.text("Join ZIRO.FIT"))
    }

    fun waitForScreen(timeoutMs: Long = TIMEOUT_MS): Boolean {
        return device.wait(Until.hasObject(By.text("Create Account")), timeoutMs)
    }
}