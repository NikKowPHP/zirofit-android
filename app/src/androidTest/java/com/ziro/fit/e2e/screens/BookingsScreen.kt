package com.ziro.fit.e2e.screens

import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until

class BookingsScreen(private val device: UiDevice) {

    companion object {
        private const val TIMEOUT_MS = 5_000L
    }

    fun isVisible(): Boolean {
        return device.wait(Until.hasObject(By.text("Bookings")), TIMEOUT_MS) ||
                device.wait(Until.hasObject(By.text("My Bookings")), TIMEOUT_MS) ||
                device.wait(Until.hasObject(By.text("Manage Bookings")), TIMEOUT_MS)
    }

    fun hasBookingCards(): Boolean {
        return device.hasObject(By.text("Confirmed")) ||
                device.hasObject(By.text("Pending")) ||
                device.hasObject(By.text("Declined"))
    }

    fun isPendingRequestsSectionVisible(): Boolean {
        return device.hasObject(By.text("Pending Requests")) ||
                device.hasObject(By.textContains("Pending Requests"))
    }

    fun isConfirmedSectionVisible(): Boolean {
        return device.hasObject(By.text("Confirmed"))
    }

    fun isDeclinedSectionVisible(): Boolean {
        return device.hasObject(By.text("Declined")) ||
                device.hasObject(By.text("Declined/Cancelled"))
    }

    fun isCreateBookingButtonVisible(): Boolean {
        return device.hasObject(By.text("Create Booking")) ||
                device.hasObject(By.text("New Booking"))
    }

    fun clickCreateBooking() {
        val createButton = device.findObject(By.text("Create Booking"))
            ?: device.findObject(By.text("New Booking"))
        createButton?.click()
        device.waitForIdle(2000)
    }

    fun clickBooking(bookingText: String) {
        val bookingCard = device.findObject(By.textContains(bookingText))
        bookingCard?.click()
        device.waitForIdle(2000)
    }

    fun clickApproveButton() {
        val approveButton = device.findObject(By.text("Approve"))
            ?: device.findObject(By.textContains("Approve"))
        approveButton?.click()
        device.waitForIdle(2000)
    }

    fun clickDeclineButton() {
        val declineButton = device.findObject(By.text("Decline"))
            ?: device.findObject(By.textContains("Decline"))
        declineButton?.click()
        device.waitForIdle(2000)
    }

    fun confirmBooking() {
        val confirmButton = device.findObject(By.text("Confirm"))
            ?: device.findObject(By.text("Accept"))
        confirmButton?.click()
        device.waitForIdle(2000)
    }

    fun declineBooking() {
        val declineButton = device.findObject(By.text("Decline"))
            ?: device.findObject(By.text("Reject"))
        declineButton?.click()
        device.waitForIdle(2000)
    }

    fun hasDataSharingBadge(): Boolean {
        return device.hasObject(By.text("Data sharing enabled")) ||
                device.hasObject(By.textContains("Data sharing"))
    }

    fun hasApprovalDialog(): Boolean {
        return device.hasObject(By.text("Approve Booking")) ||
                device.hasObject(By.text("Enable client data sharing?"))
    }

    fun clickApproveWithDataSharing() {
        val button = device.findObject(By.text("Approve with Data Sharing"))
            ?: device.findObject(By.textContains("Approve with Data"))
        button?.click()
        device.waitForIdle(2000)
    }

    fun clickApproveOnly() {
        val button = device.findObject(By.text("Approve Only"))
            ?: device.findObject(By.textContains("Approve Only"))
        button?.click()
        device.waitForIdle(2000)
    }

    fun clickCancelDialog() {
        val button = device.findObject(By.text("Cancel"))
            ?: device.findObject(By.textContains("Cancel"))
        button?.click()
        device.waitForIdle(1000)
    }

    fun dismissDialog() {
        device.pressBack()
        device.waitForIdle(1000)
    }

    fun navigateBack() {
        device.pressBack()
        device.waitForIdle(1000)
    }

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

    fun scrollUp() {
        device.swipe(
            device.displayWidth / 2,
            device.displayHeight / 4,
            device.displayWidth / 2,
            device.displayHeight * 3 / 4,
            50
        )
        device.waitForIdle(1000)
    }

    fun hasPendingBookings(): Boolean {
        val pendingSection = device.findObject(By.text("Pending Requests"))
            ?: device.findObject(By.textContains("Pending Requests"))
        return pendingSection != null
    }

    fun hasConfirmedBookings(): Boolean {
        val confirmedSection = device.findObject(By.text("Confirmed"))
        return confirmedSection != null
    }
}
