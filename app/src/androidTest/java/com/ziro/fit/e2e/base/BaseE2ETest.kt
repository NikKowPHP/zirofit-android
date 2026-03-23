package com.ziro.fit.e2e.base

import android.content.Intent
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TestName
import java.io.File

abstract class BaseE2ETest {

    lateinit var device: UiDevice

    @get:Rule
    val testNameRule = TestName()

    @Before
    open fun setUp() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.pressHome()
        device.waitForIdle(1000)
    }

    protected fun takeScreenshot(name: String) {
        try {
            val screenshotDir = File("/sdcard/Pictures")
            if (!screenshotDir.exists()) {
                screenshotDir.mkdirs()
            }
            val screenshotFile = File(screenshotDir, "${name}_${testNameRule.methodName}.png")
            device.takeScreenshot(screenshotFile)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    protected fun waitForAppLaunch(timeoutMs: Long = 10_000) {
        val packageName = "com.ziro.fit"
        device.waitForWindowUpdate(packageName, timeoutMs)
    }

    protected fun pressBack() {
        device.pressBack()
        device.waitForIdle(500)
    }

    protected fun pressHome() {
        device.pressHome()
        device.waitForIdle(500)
    }

    protected fun openApp() {
        val packageName = "com.ziro.fit"
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.context
        val launcherIntent = context.packageManager.getLaunchIntentForPackage(packageName)
        launcherIntent?.let {
            it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            context.startActivity(it)
        }
        device.waitForIdle(2000)
    }
}