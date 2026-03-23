package com.ziro.fit.e2e.screens

import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until

class CreateSessionScreen(private val device: UiDevice) {

    companion object {
        private const val TIMEOUT_MS = 5_000L
    }

    /**
     * Checks if the create session screen is visible.
     */
    fun isVisible(): Boolean {
        return device.wait(Until.hasObject(By.text("Create Session")), TIMEOUT_MS) ||
                device.wait(Until.hasObject(By.text("New Session")), TIMEOUT_MS) ||
                device.wait(Until.hasObject(By.text("Schedule Session")), TIMEOUT_MS)
    }

    /**
     * Selects a client from the client picker.
     */
    fun selectClient(clientName: String) {
        // First, open the client selector
        val clientSelector = device.findObject(By.textContains("Select client"))
            ?: device.findObject(By.text("Client"))
            ?: device.findObject(By.text("Choose Client"))
        clientSelector?.click()
        device.waitForIdle(1000)

        // Then search and select the client
        val searchField = device.findObject(By.clazz("android.widget.EditText"))
        searchField?.setText(clientName)
        device.waitForIdle(1000)

        val clientOption = device.findObject(By.textContains(clientName))
        clientOption?.click()
        device.waitForIdle(1000)
    }

    /**
     * Sets the session name/title.
     */
    fun setSessionName(name: String) {
        val nameField = device.findObject(By.clazz("android.widget.EditText"))
        nameField?.setText(name)
        device.waitForIdle(500)
    }

    /**
     * Sets the session duration in minutes.
     */
    fun setDuration(minutes: Int) {
        // Duration is usually a dropdown or stepper
        val durationField = device.findObject(By.textContains("Duration"))
        durationField?.click()
        device.waitForIdle(1000)

        // Select from picker
        val option = device.findObject(By.text("${minutes} min"))
            ?: device.findObject(By.text("$minutes minutes"))
        option?.click()
        device.waitForIdle(500)
    }

    /**
     * Clicks the date picker to set session date.
     */
    fun clickDatePicker() {
        val dateField = device.findObject(By.textContains("Date"))
            ?: device.findObject(By.text("Date"))
        dateField?.click()
        device.waitForIdle(1000)
    }

    /**
     * Clicks the time picker to set session time.
     */
    fun clickTimePicker() {
        val timeField = device.findObject(By.textContains("Time"))
            ?: device.findObject(By.text("Time"))
            ?: device.findObject(By.text("Start Time"))
        timeField?.click()
        device.waitForIdle(1000)
    }

    /**
     * Confirms the date selection (clicks OK/done in picker).
     */
    fun confirmDateSelection() {
        val confirmButton = device.findObject(By.text("OK"))
            ?: device.findObject(By.text("Confirm"))
            ?: device.findObject(By.text("Done"))
            ?: device.findObject(By.text("Set"))
        confirmButton?.click()
        device.waitForIdle(1000)
    }

    /**
     * Confirms the time selection.
     */
    fun confirmTimeSelection() {
        confirmDateSelection()
    }

    /**
     * Saves the session.
     */
    fun clickSave() {
        val saveButton = device.findObject(By.text("Save"))
            ?: device.findObject(By.text("Create"))
            ?: device.findObject(By.text("Save Session"))
        saveButton?.click()
        device.waitForIdle(3000)
    }

    /**
     * Cancels session creation.
     */
    fun clickCancel() {
        val cancelButton = device.findObject(By.text("Cancel"))
        cancelButton?.click()
        device.waitForIdle(1000)
    }

    /**
     * Navigates back without saving.
     */
    fun navigateBack() {
        device.pressBack()
        device.waitForIdle(1000)
    }
}
