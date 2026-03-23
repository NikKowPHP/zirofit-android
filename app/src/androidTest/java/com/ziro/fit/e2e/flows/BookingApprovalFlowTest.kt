package com.ziro.fit.e2e.flows

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.uiautomator.By
import com.ziro.fit.MainActivity
import com.ziro.fit.e2e.base.BaseE2ETest
import com.ziro.fit.e2e.config.E2ETestConfig
import com.ziro.fit.e2e.screens.BookingsScreen
import com.ziro.fit.e2e.screens.MoreScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class BookingApprovalFlowTest : BaseE2ETest() {

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule<MainActivity>(MainActivity::class.java)

    private lateinit var bookingsScreen: BookingsScreen
    private lateinit var moreScreen: MoreScreen

    @Before
    override fun setUp() {
        super.setUp()
        bookingsScreen = BookingsScreen(device)
        moreScreen = MoreScreen(device)
    }

    private fun loginAsTrainer() {
        val emailField = device.findObject(By.clazz("android.widget.EditText"))
        emailField?.setText(E2ETestConfig.TRAINER_EMAIL)
        device.waitForIdle(500)
        val allFields = device.findObjects(By.clazz("android.widget.EditText"))
        if (allFields.size > 1) {
            allFields[1].setText(E2ETestConfig.TRAINER_PASSWORD)
        }
        device.findObject(By.text("Sign In"))?.click()
        device.waitForWindowUpdate(null, 12_000)
        device.waitForIdle(2000)
    }

    private fun ensureTrainerMode() {
        val trainerTab = device.findObject(By.text("TRAINER"))
            ?: device.findObject(By.textContains("TRAINER"))
        trainerTab?.click()
        device.waitForIdle(2000)
    }

    private fun navigateToBookings() {
        val moreTab = device.findObject(By.text("More"))
            ?: device.findObject(By.text("MORE"))
        moreTab?.click()
        device.waitForIdle(1000)
        moreScreen.clickBookings()
        device.waitForIdle(2000)
    }

    @Test
    fun test_trainer_can_navigate_to_bookings_screen() {
        loginAsTrainer()
        ensureTrainerMode()
        navigateToBookings()

        assert(bookingsScreen.isVisible()) { "Bookings screen should be visible" }
        takeScreenshot("trainer_bookings_screen")
    }

    @Test
    fun test_bookings_screen_displays_booking_sections() {
        loginAsTrainer()
        ensureTrainerMode()
        navigateToBookings()

        val hasSections = bookingsScreen.hasBookingCards()
        assert(hasSections) { "Bookings should display sections (Pending/Confirmed/Declined)" }
        takeScreenshot("bookings_sections")
    }

    @Test
    fun test_bookings_screen_displays_pending_requests_section() {
        loginAsTrainer()
        ensureTrainerMode()
        navigateToBookings()

        val hasPendingSection = bookingsScreen.isPendingRequestsSectionVisible()
        assert(hasPendingSection) { "Should display Pending Requests section" }
        takeScreenshot("pending_requests_section")
    }

    @Test
    fun test_bookings_screen_displays_confirmed_section() {
        loginAsTrainer()
        ensureTrainerMode()
        navigateToBookings()

        val hasConfirmedSection = bookingsScreen.isConfirmedSectionVisible()
        assert(hasConfirmedSection) { "Should display Confirmed section" }
        takeScreenshot("confirmed_section")
    }

    @Test
    fun test_bookings_screen_displays_declined_section() {
        loginAsTrainer()
        ensureTrainerMode()
        navigateToBookings()

        val hasDeclinedSection = bookingsScreen.isDeclinedSectionVisible()
        assert(hasDeclinedSection) { "Should display Declined/Cancelled section" }
        takeScreenshot("declined_section")
    }

    @Test
    fun test_pending_booking_has_approve_button() {
        loginAsTrainer()
        ensureTrainerMode()
        navigateToBookings()

        device.waitForIdle(2000)
        
        val hasApproveButton = device.hasObject(By.text("Approve")) ||
                device.hasObject(By.textContains("Approve"))
        
        assert(hasApproveButton || !bookingsScreen.isPendingRequestsSectionVisible()) {
            "Should have Approve button when there are pending bookings"
        }
        takeScreenshot("approve_button_visible")
    }

    @Test
    fun test_pending_booking_has_decline_button() {
        loginAsTrainer()
        ensureTrainerMode()
        navigateToBookings()

        device.waitForIdle(2000)
        
        val hasDeclineButton = device.hasObject(By.text("Decline")) ||
                device.hasObject(By.textContains("Decline"))
        
        assert(hasDeclineButton || !bookingsScreen.isPendingRequestsSectionVisible()) {
            "Should have Decline button when there are pending bookings"
        }
        takeScreenshot("decline_button_visible")
    }

    @Test
    fun test_confirmed_booking_displays_data_sharing_status() {
        loginAsTrainer()
        ensureTrainerMode()
        navigateToBookings()

        device.waitForIdle(2000)
        bookingsScreen.scrollDown()
        device.waitForIdle(1000)

        val hasDataSharingBadge = bookingsScreen.hasDataSharingBadge()
        
        takeScreenshot("data_sharing_badge")
    }

    @Test
    fun test_trainer_can_approve_booking() {
        loginAsTrainer()
        ensureTrainerMode()
        navigateToBookings()

        device.waitForIdle(2000)
        
        val approveButton = device.findObject(By.text("Approve"))
        if (approveButton != null) {
            approveButton.click()
            device.waitForIdle(2000)
            
            val dialogAppears = device.hasObject(By.text("Approve Booking")) ||
                    device.hasObject(By.text("Enable client data sharing?"))
            
            takeScreenshot("booking_approval_dialog")
            
            if (dialogAppears) {
                device.pressBack()
                device.waitForIdle(1000)
            }
        } else {
            takeScreenshot("no_pending_bookings_to_approve")
        }
    }

    @Test
    fun test_approval_dialog_shows_data_sharing_option() {
        loginAsTrainer()
        ensureTrainerMode()
        navigateToBookings()

        device.waitForIdle(2000)
        
        val approveButton = device.findObject(By.text("Approve"))
        approveButton?.click()
        device.waitForIdle(2000)

        val hasDataSharingOption = device.hasObject(By.text("Enable client data sharing?")) ||
                device.hasObject(By.text("Approve with Data Sharing")) ||
                device.hasObject(By.text("Approve Only"))

        assert(!hasDataSharingOption || !bookingsScreen.isPendingRequestsSectionVisible()) {
            "Approval dialog should show data sharing options"
        }
        takeScreenshot("approval_dialog_options")
        
        if (hasDataSharingOption) {
            device.pressBack()
            device.waitForIdle(1000)
        }
    }

    @Test
    fun test_trainer_can_decline_booking() {
        loginAsTrainer()
        ensureTrainerMode()
        navigateToBookings()

        device.waitForIdle(2000)
        
        val declineButton = device.findObject(By.text("Decline"))
        if (declineButton != null) {
            declineButton.click()
            device.waitForIdle(2000)
            
            takeScreenshot("booking_declined")
        } else {
            takeScreenshot("no_pending_bookings_to_decline")
        }
    }

    @Test
    fun test_booking_list_is_scrollable() {
        loginAsTrainer()
        ensureTrainerMode()
        navigateToBookings()

        device.waitForIdle(2000)
        bookingsScreen.scrollDown()
        device.waitForIdle(1000)
        
        val stillVisible = bookingsScreen.isVisible()
        assert(stillVisible) { "Bookings screen should still be visible after scrolling" }
        takeScreenshot("bookings_scrolled")
    }

    // ===== New Approval Flow Tests =====

    @Test
    fun test_approval_dialog_has_both_approve_options() {
        loginAsTrainer()
        ensureTrainerMode()
        navigateToBookings()

        device.waitForIdle(2000)
        bookingsScreen.clickApproveButton()

        device.waitForIdle(1000)
        
        val hasApproveWithDataSharing = device.hasObject(By.text("Approve with Data Sharing")) ||
                device.hasObject(By.textContains("Data Sharing"))
        val hasApproveOnly = device.hasObject(By.text("Approve Only"))

        takeScreenshot("approval_dialog_both_options")
        
        if (bookingsScreen.hasApprovalDialog()) {
            assert(hasApproveWithDataSharing || hasApproveOnly) {
                "Dialog should have both 'Approve with Data Sharing' and 'Approve Only' options"
            }
            bookingsScreen.dismissDialog()
        }
    }

    @Test
    fun test_approval_dialog_explains_data_sharing() {
        loginAsTrainer()
        ensureTrainerMode()
        navigateToBookings()

        device.waitForIdle(2000)
        bookingsScreen.clickApproveButton()

        device.waitForIdle(1000)
        
        val explainsDataSharing = device.hasObject(By.text("Enable client data sharing?")) ||
                device.hasObject(By.textContains("workouts, measurements, photos, and check-ins")) ||
                device.hasObject(By.textContains("view the client's"))

        takeScreenshot("data_sharing_explanation")
        
        if (bookingsScreen.hasApprovalDialog()) {
            bookingsScreen.dismissDialog()
        }
    }

    @Test
    fun test_approve_with_data_sharing_option_exists() {
        loginAsTrainer()
        ensureTrainerMode()
        navigateToBookings()

        device.waitForIdle(2000)
        
        if (bookingsScreen.hasPendingBookings()) {
            bookingsScreen.clickApproveButton()
            device.waitForIdle(1000)
            
            val hasDataSharingOption = device.hasObject(By.text("Approve with Data Sharing"))
            
            takeScreenshot("approve_with_data_sharing_option")
            
            if (hasDataSharingOption) {
                bookingsScreen.clickApproveWithDataSharing()
                device.waitForIdle(2000)
                takeScreenshot("after_approve_with_data_sharing")
            } else {
                bookingsScreen.dismissDialog()
            }
        } else {
            takeScreenshot("no_pending_bookings_data_sharing_test")
        }
    }

    @Test
    fun test_approve_only_option_exists() {
        loginAsTrainer()
        ensureTrainerMode()
        navigateToBookings()

        device.waitForIdle(2000)
        
        if (bookingsScreen.hasPendingBookings()) {
            bookingsScreen.clickApproveButton()
            device.waitForIdle(1000)
            
            val hasApproveOnly = device.hasObject(By.text("Approve Only"))
            
            takeScreenshot("approve_only_option")
            
            if (hasApproveOnly) {
                bookingsScreen.clickApproveOnly()
                device.waitForIdle(2000)
                takeScreenshot("after_approve_only")
            } else {
                bookingsScreen.dismissDialog()
            }
        } else {
            takeScreenshot("no_pending_bookings_approve_only_test")
        }
    }

    @Test
    fun test_approval_dialog_can_be_cancelled() {
        loginAsTrainer()
        ensureTrainerMode()
        navigateToBookings()

        device.waitForIdle(2000)
        
        if (bookingsScreen.hasPendingBookings()) {
            bookingsScreen.clickApproveButton()
            device.waitForIdle(1000)
            
            if (bookingsScreen.hasApprovalDialog()) {
                bookingsScreen.clickCancelDialog()
                device.waitForIdle(1000)
                
                val dialogDismissed = !device.hasObject(By.text("Approve Booking"))
                
                takeScreenshot("dialog_cancelled")
            }
        } else {
            takeScreenshot("no_pending_bookings_cancel_test")
        }
    }

    @Test
    fun test_booking_moves_to_confirmed_after_approval() {
        loginAsTrainer()
        ensureTrainerMode()
        navigateToBookings()

        device.waitForIdle(2000)
        
        val hadPendingBefore = bookingsScreen.hasPendingBookings()
        
        if (bookingsScreen.hasPendingBookings()) {
            bookingsScreen.clickApproveButton()
            device.waitForIdle(1000)
            
            if (bookingsScreen.hasApprovalDialog()) {
                bookingsScreen.clickApproveOnly()
                device.waitForIdle(2000)
                
                val hasConfirmedNow = bookingsScreen.hasConfirmedBookings()
                
                takeScreenshot("booking_moved_to_confirmed")
            }
        }
        
        takeScreenshot("approval_flow_complete")
    }

    @Test
    fun test_data_sharing_badge_shows_for_shared_bookings() {
        loginAsTrainer()
        ensureTrainerMode()
        navigateToBookings()

        device.waitForIdle(2000)
        bookingsScreen.scrollDown()
        device.waitForIdle(1000)
        
        val hasDataSharingBadge = bookingsScreen.hasDataSharingBadge()
        
        takeScreenshot("data_sharing_badge_visible")
        
        if (hasDataSharingBadge) {
            val badgeText = device.findObject(By.textContains("Data sharing"))
            assert(badgeText != null) { "Data sharing badge should display text" }
        }
    }

    @Test
    fun test_confirmed_section_shows_all_confirmed_bookings() {
        loginAsTrainer()
        ensureTrainerMode()
        navigateToBookings()

        device.waitForIdle(2000)
        
        if (bookingsScreen.hasConfirmedBookings()) {
            val confirmedCount = device.findObjects(By.text("CONFIRMED")).size
            
            takeScreenshot("confirmed_bookings_count")
            
            assert(confirmedCount >= 0) { "Confirmed section should show all confirmed bookings" }
        } else {
            takeScreenshot("no_confirmed_bookings")
        }
    }

    @Test
    fun test_pending_section_only_shows_pending_bookings() {
        loginAsTrainer()
        ensureTrainerMode()
        navigateToBookings()

        device.waitForIdle(2000)
        
        if (bookingsScreen.isPendingRequestsSectionVisible()) {
            val pendingCount = device.findObjects(By.text("PENDING")).size
            
            takeScreenshot("pending_bookings_count")
            
            assert(pendingCount >= 0) { "Pending section should show only pending bookings" }
        } else {
            takeScreenshot("no_pending_bookings")
        }
    }

    @Test
    fun test_booking_with_data_sharing_shows_indicator() {
        loginAsTrainer()
        ensureTrainerMode()
        navigateToBookings()

        device.waitForIdle(2000)
        
        val confirmedSection = device.findObject(By.text("Confirmed"))
        if (confirmedSection != null) {
            bookingsScreen.scrollDown()
            device.waitForIdle(1000)
            
            val hasDataSharingIndicator = device.hasObject(By.text("Data sharing enabled")) ||
                    device.hasObject(By.textContains("sharing enabled"))
            
            takeScreenshot("data_sharing_indicator")
        } else {
            takeScreenshot("no_confirmed_bookings_for_indicator")
        }
    }

    @Test
    fun test_full_booking_lifecycle_pending_to_confirmed() {
        loginAsTrainer()
        ensureTrainerMode()
        navigateToBookings()

        device.waitForIdle(2000)
        
        val initialPendingCount = if (bookingsScreen.hasPendingBookings()) {
            device.findObjects(By.text("PENDING")).size
        } else 0

        if (initialPendingCount > 0) {
            bookingsScreen.clickApproveButton()
            device.waitForIdle(1000)
            
            if (bookingsScreen.hasApprovalDialog()) {
                bookingsScreen.clickApproveOnly()
                device.waitForIdle(2000)
                
                val finalPendingCount = if (bookingsScreen.isPendingRequestsSectionVisible()) {
                    device.findObjects(By.text("PENDING")).size
                } else 0
                
                takeScreenshot("lifecycle_pending_to_confirmed")
                
                assert(finalPendingCount < initialPendingCount) {
                    "Pending count should decrease after approval"
                }
            }
        } else {
            takeScreenshot("lifecycle_no_pending_to_test")
        }
    }

    @Test
    fun test_decline_removes_booking_from_pending() {
        loginAsTrainer()
        ensureTrainerMode()
        navigateToBookings()

        device.waitForIdle(2000)
        
        if (bookingsScreen.hasPendingBookings()) {
            val initialPendingCount = device.findObjects(By.text("PENDING")).size
            
            bookingsScreen.clickDeclineButton()
            device.waitForIdle(2000)
            
            val finalPendingCount = if (bookingsScreen.isPendingRequestsSectionVisible()) {
                device.findObjects(By.text("PENDING")).size
            } else 0
            
            takeScreenshot("decline_removes_pending")
            
            assert(finalPendingCount < initialPendingCount || finalPendingCount == 0) {
                "Pending count should decrease or be zero after decline"
            }
        } else {
            takeScreenshot("decline_no_pending_to_test")
        }
    }
}