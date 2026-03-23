package com.ziro.fit.e2e.screens

import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until

class BookingsScreen(private val device: UiDevice) {

    companion object {
        private const val TIMEOUT_MS = 5_000L
    }

    /**
     * Checks if the bookings screen is visible.
     */
    fun isVisible(): Boolean {
        return device.wait(Until.hasObject(By.text("Bookings")), TIMEOUT_MS) ||
                device.wait(Until.hasObject(By.text("My Bookings")), TIMEOUT_MS) ||
                device.wait(Until.hasObject(By.text("Manage Bookings")), TIMEOUT_MS)
    }

    /**
     * Checks if booking cards are visible.
     */
    fun hasBookingCards(): Boolean {
        return device.hasObject(By.text("Confirmed")) ||
                device.hasObject(By.text("Pending")) ||
                device.hasObject(By.text("Declined"))
    }

    /**
     * Checks if the create booking button is visible.
     */
    fun isCreateBookingButtonVisible(): Boolean {
        return device.hasObject(By.text("Create Booking")) ||
                device.hasObject(By.text("New Booking"))
    }

    /**
     * Clicks the create booking button.
     */
    fun clickCreateBooking() {
        val createButton = device.findObject(By.text("Create Booking"))
            ?: device.findObject(By.text("New Booking"))
        createButton?.click()
        device.waitForIdle(2000)
    }

    /**
     * Clicks on a booking card to view details.
     */
    fun clickBooking(bookingText: String) {
        val bookingCard = device.findObject(By.textContains(bookingText))
        bookingCard?.click()
        device.waitForIdle(2000)
    }

    /**
     * Confirms a pending booking.
     */
    fun confirmBooking() {
        val confirmButton = device.findObject(By.text("Confirm"))
            ?: device.findObject(By.text("Accept"))
        confirmButton?.click()
        device.waitForIdle(2000)
    }

    /**
     * Declines a pending booking.
     */
    fun declineBooking() {
        val declineButton = device.findObject(By.text("Decline"))
            ?: device.findObject(By.text("Reject"))
        declineButton?.click()
        device.waitForIdle(2000)
    }

    /**
     * Navigates back.
     */
    fun navigateBack() {
        device.pressBack()
        device.waitForIdle(1000)
    }

    /**
     * Scrolls down the bookings list.
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
}
