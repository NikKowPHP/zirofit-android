package com.ziro.fit.e2e.screens

import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until

class LoginScreen(private val device: UiDevice) {

    companion object {
        private const val TIMEOUT_MS = 5_000L
    }

    fun enterEmail(email: String) {
        val editTexts = device.findObjects(By.clazz("android.widget.EditText"))
        if (editTexts.isNotEmpty()) {
            editTexts[0].text = email
        }
    }

    fun enterPassword(password: String) {
        val editTexts = device.findObjects(By.clazz("android.widget.EditText"))
        if (editTexts.size > 1) {
            editTexts[1].text = password
        }
    }

    fun clickSignIn() {
        val signInButton = device.findObject(By.text("Sign In"))
        signInButton?.click()
    }

    fun clickRegisterLink() {
        val registerLink = device.findObject(By.text("Sign Up"))
        registerLink?.click()
    }

    fun clickGoogleSignIn() {
        val googleButton = device.findObject(By.text("Sign in with Google"))
        googleButton?.click()
    }

    fun isLoginButtonVisible(): Boolean {
        return device.hasObject(By.text("Sign In"))
    }

    fun isGoogleButtonVisible(): Boolean {
        return device.hasObject(By.text("Sign in with Google"))
    }

    fun isRegisterLinkVisible(): Boolean {
        return device.hasObject(By.text("Sign Up"))
    }

    fun isErrorVisible(): Boolean {
        return device.hasObject(By.text("Error"))
    }

    fun isWelcomeBackTextVisible(): Boolean {
        return device.hasObject(By.text("Welcome Back"))
    }

    fun waitForScreen(timeoutMs: Long = TIMEOUT_MS): Boolean {
        return device.wait(Until.hasObject(By.text("Sign In")), timeoutMs)
    }
}