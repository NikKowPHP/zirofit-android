package com.ziro.fit.e2e.flows

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import com.ziro.fit.MainActivity
import com.ziro.fit.e2e.base.BaseE2ETest
import com.ziro.fit.e2e.config.E2ETestConfig
import com.ziro.fit.e2e.screens.CheckInScreen
import com.ziro.fit.e2e.screens.ClientDashboardScreen
import com.ziro.fit.e2e.screens.ExploreScreen
import com.ziro.fit.e2e.screens.LiveWorkoutScreen
import com.ziro.fit.e2e.screens.ProfileScreen
import com.ziro.fit.e2e.screens.WorkoutsScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ClientFlowTest : BaseE2ETest() {

    private val config = E2ETestConfig

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule<MainActivity>(MainActivity::class.java)

    private lateinit var dashboardScreen: ClientDashboardScreen
    private lateinit var workoutsScreen: WorkoutsScreen
    private lateinit var liveWorkoutScreen: LiveWorkoutScreen
    private lateinit var checkInScreen: CheckInScreen
    private lateinit var profileScreen: ProfileScreen
    private lateinit var exploreScreen: ExploreScreen

    @Before
    override fun setUp() {
        super.setUp()

        dashboardScreen = ClientDashboardScreen(device)
        workoutsScreen = WorkoutsScreen(device)
        liveWorkoutScreen = LiveWorkoutScreen(device)
        checkInScreen = CheckInScreen(device)
        profileScreen = ProfileScreen(device)
        exploreScreen = ExploreScreen(device)
    }

    private fun loginAsClient() {
        device.findObject(By.text("Sign In")).click()
        device.waitForIdle(1000)

        val emailField = device.findObject(By.clazz("android.widget.EditText"))
        emailField.setText(config.CLIENT_EMAIL)

        device.waitForIdle(500)

        val allFields = device.findObjects(By.clazz("android.widget.EditText"))
        if (allFields.size > 1) {
            allFields[1].setText(config.CLIENT_PASSWORD)
        }

        device.findObject(By.text("Sign In")).click()
        device.waitForWindowUpdate(null, 10000)
    }

    private fun ensureClientMode() {
        val personalTab = device.findObject(By.text("PERSONAL"))
        if (personalTab != null) {
            personalTab.click()
            device.waitForIdle(1000)
        }
    }

    private fun navigateToDashboard() {
        val homeTab = device.findObject(By.text("Home"))
        if (homeTab != null) {
            homeTab.click()
            device.waitForIdle(2000)
        }
    }

    private fun navigateToWorkouts() {
        val calendarTab = device.findObject(By.text("Calendar"))
        if (calendarTab != null) {
            calendarTab.click()
            device.waitForIdle(2000)
        }
    }

    private fun navigateToCheckIns() {
        navigateToDashboard()
        dashboardScreen.scrollDown()
        dashboardScreen.clickViewCheckIns()
        device.waitForIdle(2000)
    }

    private fun navigateToProfile() {
        val moreTab = device.findObject(By.text("More"))
        if (moreTab != null) {
            moreTab.click()
            device.waitForIdle(2000)
        }
    }

    private fun navigateToExplore() {
        val programsTab = device.findObject(By.text("Programs"))
        if (programsTab != null) {
            programsTab.click()
            device.waitForIdle(2000)
        }
    }

    private fun navigateToLiveWorkout() {
        navigateToWorkouts()
        workoutsScreen.clickStartEmptyWorkout()
        device.waitForIdle(3000)
    }

    @Test
    fun test_client_dashboard_displays() {
        loginAsClient()
        ensureClientMode()
        navigateToDashboard()

        assert(dashboardScreen.isVisible()) { "Dashboard should be visible" }
        assert(dashboardScreen.areTabsVisible()) { "Dashboard tabs should be visible" }
        assert(dashboardScreen.isEventsSectionVisible()) { "Events section should be visible" }
        assert(dashboardScreen.isAICoachVisible()) { "AI Coach section should be visible" }
        assert(dashboardScreen.isCheckInSectionVisible()) { "Check-In section should be visible" }

        takeScreenshot("dashboard_display")
    }

    @Test
    fun test_client_dashboard_tabs() {
        loginAsClient()
        ensureClientMode()
        navigateToDashboard()

        dashboardScreen.clickHistoryTab()
        assert(dashboardScreen.isHistoryTabVisible()) { "History tab should be selected" }

        dashboardScreen.clickStatsTab()
        assert(dashboardScreen.isStatsTabVisible()) { "Stats tab should be selected" }

        dashboardScreen.clickOverviewTab()
        assert(dashboardScreen.isOverviewTabVisible()) { "Overview tab should be selected" }

        takeScreenshot("dashboard_tabs")
    }

    @Test
    fun test_start_workout_from_dashboard() {
        loginAsClient()
        ensureClientMode()
        navigateToDashboard()

        dashboardScreen.scrollDown()

        val startButton = device.findObject(By.textContains("Start "))
        if (startButton != null) {
            startButton.click()
            device.waitForIdle(3000)
        }

        val inWorkout = liveWorkoutScreen.isVisible()
        val inWorkouts = workoutsScreen.isVisible()

        assert(inWorkout || inWorkouts) { "Should navigate to workouts or live workout" }

        takeScreenshot("start_workout_from_dashboard")
    }

    @Test
    fun test_workouts_screen() {
        loginAsClient()
        ensureClientMode()
        navigateToWorkouts()

        assert(workoutsScreen.isVisible()) { "Workouts screen should be visible" }
        assert(workoutsScreen.isQuickStartVisible()) { "Quick Start section should be visible" }
        assert(workoutsScreen.isProgramsSectionVisible()) { "Programs section should be visible" }
        assert(workoutsScreen.isTemplatesSectionVisible()) { "Templates section should be visible" }

        takeScreenshot("workouts_screen")
    }

    @Test
    fun test_start_live_workout() {
        loginAsClient()
        ensureClientMode()
        navigateToWorkouts()

        workoutsScreen.clickStartEmptyWorkout()

        assert(liveWorkoutScreen.isVisible()) { "Live workout screen should be visible" }

        takeScreenshot("start_live_workout")
    }

    @Test
    fun test_live_workout_add_exercise() {
        loginAsClient()
        ensureClientMode()
        navigateToLiveWorkout()

        assert(liveWorkoutScreen.isVisible()) { "Live workout screen should be visible" }

        liveWorkoutScreen.clickAddExercise()

        assert(liveWorkoutScreen.isExerciseBrowserVisible()) { "Exercise browser should be visible" }

        liveWorkoutScreen.searchExercise("Squat")
        liveWorkoutScreen.selectExerciseFromBrowser("Squat")
        device.waitForIdle(2000)

        assert(liveWorkoutScreen.isExerciseVisible("Squat")) { "Exercise should appear in workout" }

        takeScreenshot("live_workout_add_exercise")
    }

    @Test
    fun test_live_workout_log_set() {
        loginAsClient()
        ensureClientMode()
        navigateToLiveWorkout()

        liveWorkoutScreen.clickAddExercise()
        device.waitForIdle(1000)

        if (liveWorkoutScreen.isExerciseBrowserVisible()) {
            liveWorkoutScreen.searchExercise("Bench Press")
            device.waitForIdle(1000)
            liveWorkoutScreen.selectExerciseFromBrowser("Bench")
            device.waitForIdle(2000)
        }

        assert(liveWorkoutScreen.isExerciseVisible("Bench")) { "Exercise should be visible" }

        val weightField = device.findObject(By.textContains("Weight"))
        if (weightField != null) {
            weightField.click()
            device.waitForIdle(1000)
        }

        takeScreenshot("live_workout_log_set")
    }

    @Test
    fun test_finish_workout() {
        loginAsClient()
        ensureClientMode()
        navigateToLiveWorkout()

        assert(liveWorkoutScreen.isVisible()) { "Live workout screen should be visible" }

        if (liveWorkoutScreen.isCancelButtonVisible()) {
            liveWorkoutScreen.clickCancel()
            device.waitForIdle(1000)
        }

        if (liveWorkoutScreen.isCancelDialogVisible()) {
            liveWorkoutScreen.confirmCancel()
        }

        device.waitForIdle(2000)
        val backOnWorkouts = workoutsScreen.isVisible()
        val backOnDashboard = dashboardScreen.isVisible()

        assert(backOnWorkouts || backOnDashboard) { "Should navigate back after canceling workout" }

        takeScreenshot("finish_workout")
    }

    @Test
    fun test_navigate_to_checkins() {
        loginAsClient()
        ensureClientMode()
        navigateToCheckIns()

        assert(checkInScreen.isListVisible()) { "Check-ins list should be visible" }
        assert(checkInScreen.isCheckInNowVisible()) { "Check-In button should be visible" }

        takeScreenshot("navigate_to_checkins")
    }

    @Test
    fun test_submit_checkin() {
        loginAsClient()
        ensureClientMode()
        navigateToCheckIns()

        checkInScreen.clickCheckInNow()

        assert(checkInScreen.isSubmissionVisible()) { "Check-in submission form should be visible" }

        checkInScreen.enterWeight("75")
        checkInScreen.enterSleep("8")
        checkInScreen.setEnergy(7)
        checkInScreen.setStress(4)
        checkInScreen.enterNotes("Feeling good this week!")

        checkInScreen.clickSubmit()

        device.waitForIdle(3000)
        assert(checkInScreen.isSubmissionSuccessful()) { "Should return to check-ins list after submission" }

        takeScreenshot("submit_checkin")
    }

    @Test
    fun test_profile_screen() {
        loginAsClient()
        ensureClientMode()
        navigateToProfile()

        assert(profileScreen.isVisible()) { "Profile screen should be visible" }

        profileScreen.waitForLoad()
        assert(profileScreen.hasUserInfo()) { "User info should be loaded" }
        assert(profileScreen.isLogoutButtonVisible()) { "Logout button should be visible" }

        takeScreenshot("profile_screen")
    }

    @Test
    fun test_navigate_to_explore() {
        loginAsClient()
        ensureClientMode()
        navigateToExplore()

        assert(exploreScreen.isVisible()) { "Explore screen should be visible" }

        exploreScreen.waitForContentLoad()
        val hasContent = exploreScreen.hasTrainerCards() || exploreScreen.hasEventCards()
        assert(hasContent) { "Explore screen should have content" }

        takeScreenshot("navigate_to_explore")
    }
}
