package com.ziro.fit.e2e.screens

import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until

class ClientsScreen(private val device: UiDevice) {

    companion object {
        private const val TIMEOUT_MS = 5_000L
    }

    /**
     * Checks if the clients screen is visible.
     */
    fun isVisible(): Boolean {
        return device.wait(Until.hasObject(By.text("My Clients")), TIMEOUT_MS) ||
                device.wait(Until.hasObject(By.text("Clients")), TIMEOUT_MS) ||
                device.wait(Until.hasObject(By.text("Clients List")), TIMEOUT_MS)
    }

    /**
     * Checks if the search bar is visible.
     */
    fun isSearchBarVisible(): Boolean {
        return device.hasObject(By.textContains("Search")) ||
                device.hasObject(By.clazz("android.widget.EditText"))
    }

    /**
     * Checks if client cards are visible in the list.
     */
    fun hasClientCards(): Boolean {
        return device.hasObject(By.text("Active")) ||
                device.hasObject(By.text("Inactive")) ||
                device.hasObject(By.text("Pending"))
    }

    /**
     * Clicks on a client card by name.
     */
    fun clickClient(clientName: String) {
        val clientCard = device.findObject(By.textContains(clientName))
        clientCard?.click()
        device.waitForIdle(2000)
    }

    /**
     * Gets the number of client cards visible.
     */
    fun getClientCount(): Int {
        // This is a rough estimate — in practice you'd iterate with scroll
        return device.findObjects(By.clazz("android.widget.CardView")).size
    }

    /**
     * Checks if the "Add Client" button is visible.
     */
    fun isAddClientButtonVisible(): Boolean {
        return device.hasObject(By.text("Add Client")) ||
                device.hasObject(By.text("Invite Client")) ||
                device.hasObject(By.text("Request Link"))
    }

    /**
     * Clicks the add client button.
     */
    fun clickAddClient() {
        val addButton = device.findObject(By.text("Add Client"))
            ?: device.findObject(By.text("Invite Client"))
            ?: device.findObject(By.text("Request Link"))
        addButton?.click()
        device.waitForIdle(2000)
    }

    /**
     * Searches for a client by name.
     */
    fun searchClient(name: String) {
        val searchField = device.findObject(By.clazz("android.widget.EditText"))
        searchField?.setText(name)
        device.waitForIdle(1000)
    }

    /**
     * Filters clients by status.
     */
    fun filterByStatus(status: String) {
        val filterButton = device.findObject(By.text(status))
        filterButton?.click()
        device.waitForIdle(1000)
    }

    /**
     * Scrolls the client list down.
     */
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

    /**
     * Navigates back to calendar.
     */
    fun navigateBack() {
        device.pressBack()
        device.waitForIdle(1000)
    }
}
