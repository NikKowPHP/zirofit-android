package com.ziro.fit.e2e.screens

import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until

class ProfileScreen(private val device: UiDevice) {

    fun isVisible(): Boolean {
        return device.wait(Until.hasObject(By.text("Profile")), 5000)
    }

    fun isLogoutButtonVisible(): Boolean {
        return device.wait(Until.hasObject(By.text("Logout")), 5000)
    }

    fun isUserNameVisible(): Boolean {
        return device.hasObject(By.textContains("Name:"))
    }

    fun isUserEmailVisible(): Boolean {
        return device.hasObject(By.textContains("Email:")) ||
                device.hasObject(By.textContains("@"))
    }

    fun isLoading(): Boolean {
        return device.hasObject(By.text("Loading...")) ||
                device.hasObject(By.clazz("android.widget.ProgressBar"))
    }

    fun waitForLoad() {
        var attempts = 0
        while (isLoading() && attempts < 10) {
            device.waitForIdle(500)
            attempts++
        }
    }

    fun hasUserInfo(): Boolean {
        waitForLoad()
        return isUserNameVisible() && isUserEmailVisible()
    }

    fun clickLogout() {
        val logoutButton = device.findObject(By.text("Logout"))
            ?: device.findObject(By.text("Log Out"))
            ?: device.findObject(By.textContains("logout"))
        logoutButton?.click()
        device.waitForIdle(2000)
    }
}
