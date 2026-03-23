package com.ziro.fit.e2e.helpers

import android.content.Intent
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.ziro.fit.e2e.config.E2ETestConfig

class AuthHelper(private val device: UiDevice) {

    companion object {
        private const val APP_PACKAGE = E2ETestConfig.PACKAGE_NAME
        private const val TIMEOUT_MS = 10_000L
    }

    fun openApp() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val launcherIntent = context.packageManager.getLaunchIntentForPackage(APP_PACKAGE)
        launcherIntent?.let {
            it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            context.startActivity(it)
        }
        device.waitForIdle(2000)
    }

    fun login(email: String, password: String) {
        val editTexts = device.findObjects(By.clazz("android.widget.EditText"))
        editTexts.getOrNull(0)?.text = email
        device.waitForIdle(500)
        editTexts.getOrNull(1)?.text = password
        device.waitForIdle(500)
        device.findObject(By.text("Sign In")).click()
        device.waitForWindowUpdate(APP_PACKAGE, TIMEOUT_MS)
    }

    fun loginAsTrainer() {
        login(E2ETestConfig.TRAINER_EMAIL, E2ETestConfig.TRAINER_PASSWORD)
    }

    fun loginAsClient() {
        login(E2ETestConfig.CLIENT_EMAIL, E2ETestConfig.CLIENT_PASSWORD)
    }

    fun navigateToRegister() {
        device.findObject(By.text("Sign Up"))?.click()
        device.waitForIdle(2000)
    }

    fun register(name: String, email: String, password: String) {
        val editTexts = device.findObjects(By.clazz("android.widget.EditText"))
        editTexts.getOrNull(0)?.text = name
        device.waitForIdle(300)
        editTexts.getOrNull(1)?.text = email
        device.waitForIdle(300)
        editTexts.getOrNull(2)?.text = password
        device.waitForIdle(300)
        editTexts.getOrNull(3)?.text = password
        device.waitForIdle(300)
        device.findObject(By.text("Create Account")).click()
        device.waitForWindowUpdate(APP_PACKAGE, TIMEOUT_MS)
    }

    fun navigateToLogin() {
        device.findObject(By.text("Log In"))?.click()
        device.waitForIdle(2000)
    }

    fun waitForLoginScreen(timeoutMs: Long = TIMEOUT_MS): Boolean {
        return device.wait(Until.hasObject(By.text("Sign In")), timeoutMs)
    }

    fun waitForRegisterScreen(timeoutMs: Long = TIMEOUT_MS): Boolean {
        return device.wait(Until.hasObject(By.text("Create Account")), timeoutMs)
    }

    fun waitForEmailConfirmationScreen(timeoutMs: Long = TIMEOUT_MS): Boolean {
        return device.wait(Until.hasObject(By.text("Check your email")), timeoutMs)
    }

    fun isErrorVisible(): Boolean {
        return device.hasObject(By.text("Error"))
    }

    fun isAuthenticated(): Boolean {
        return !device.hasObject(By.text("Sign In")) &&
                !device.hasObject(By.text("Create Account"))
    }

    fun logout(): Boolean {
        device.findObject(By.text("More"))?.click()
        device.waitForIdle(2000)
        val logoutButton = device.findObject(By.text("Logout"))
            ?: device.findObject(By.text("Log Out"))
            ?: device.findObject(By.textContains("logout"))
        logoutButton?.click()
        device.waitForIdle(3000)
        return waitForLoginScreen()
    }

    fun resetAppState() {
        device.pressHome()
        device.waitForIdle(1000)
        openApp()
    }
}
