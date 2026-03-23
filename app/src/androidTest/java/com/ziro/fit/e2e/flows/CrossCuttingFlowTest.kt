package com.ziro.fit.e2e.flows

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.uiautomator.By
import com.ziro.fit.MainActivity
import com.ziro.fit.e2e.base.BaseE2ETest
import com.ziro.fit.e2e.config.E2ETestConfig
import com.ziro.fit.e2e.screens.CalendarScreen
import com.ziro.fit.e2e.screens.ClientDashboardScreen
import com.ziro.fit.e2e.screens.MoreScreen
import com.ziro.fit.e2e.screens.ProfileScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CrossCuttingFlowTest : BaseE2ETest() {

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule<MainActivity>(MainActivity::class.java)

    private lateinit var calendarScreen: CalendarScreen
    private lateinit var clientDashboardScreen: ClientDashboardScreen
    private lateinit var moreScreen: MoreScreen
    private lateinit var profileScreen: ProfileScreen

    @Before
    override fun setUp() {
        super.setUp()
        calendarScreen = CalendarScreen(device)
        clientDashboardScreen = ClientDashboardScreen(device)
        moreScreen = MoreScreen(device)
        profileScreen = ProfileScreen(device)
    }

    private fun loginAsTrainer() {
        val emailField = device.findObject(By.clazz("android.widget.EditText"))
        emailField.setText(E2ETestConfig.TRAINER_EMAIL)
        device.waitForIdle(500)
        val allFields = device.findObjects(By.clazz("android.widget.EditText"))
        if (allFields.size > 1) {
            allFields[1].setText(E2ETestConfig.TRAINER_PASSWORD)
        }
        device.findObject(By.text("Sign In")).click()
        device.waitForWindowUpdate(null, 12_000)
        device.waitForIdle(2000)
    }

    private fun loginAsClient() {
        val emailField = device.findObject(By.clazz("android.widget.EditText"))
        emailField.setText(E2ETestConfig.CLIENT_EMAIL)
        device.waitForIdle(500)
        val allFields = device.findObjects(By.clazz("android.widget.EditText"))
        if (allFields.size > 1) {
            allFields[1].setText(E2ETestConfig.CLIENT_PASSWORD)
        }
        device.findObject(By.text("Sign In")).click()
        device.waitForWindowUpdate(null, 12_000)
        device.waitForIdle(2000)
    }

    private fun switchToMode(modeText: String) {
        val modeTab = device.findObject(By.text(modeText))
            ?: device.findObject(By.textContains(modeText))
        modeTab?.click()
        device.waitForIdle(3000)
    }

    private fun navigateToProfile() {
        navigateToMore()
        moreScreen.clickProfile()
        device.waitForIdle(2000)
    }

    private fun navigateToMore() {
        val moreTab = device.findObject(By.text("More"))
            ?: device.findObject(By.text("MORE"))
        moreTab?.click()
        device.waitForIdle(2000)
    }

    @Test
    fun test_mode_switch_trainer_to_personal() {
        loginAsTrainer()
        switchToMode("TRAINER")
        device.waitForIdle(2000)
        assert(calendarScreen.isVisible()) { "Should be on trainer calendar" }

        switchToMode("PERSONAL")
        device.waitForIdle(3000)

        val onClientDashboard = clientDashboardScreen.isOverviewTabVisible() ||
                device.hasObject(By.text("Overview"))
        assert(onClientDashboard) { "Should switch to client dashboard" }
        takeScreenshot("mode_switch_trainer_to_personal")
    }

    @Test
    fun test_mode_switch_personal_to_trainer() {
        loginAsClient()
        switchToMode("PERSONAL")
        device.waitForIdle(2000)

        switchToMode("TRAINER")
        device.waitForIdle(3000)

        val onTrainerCalendar = calendarScreen.isVisible() ||
                device.hasObject(By.text("Calendar"))
        assert(onTrainerCalendar) { "Should switch to trainer calendar" }
        takeScreenshot("mode_switch_personal_to_trainer")
    }

    @Test
    fun test_profile_navigation_trainer() {
        loginAsTrainer()
        switchToMode("TRAINER")
        navigateToProfile()
        moreScreen.clickProfile()
        device.waitForIdle(2000)

        assert(profileScreen.isVisible() ||
                device.hasObject(By.text("Profile")) ||
                device.hasObject(By.text("Account"))) { "Profile screen should be visible" }
        takeScreenshot("profile_navigation_trainer")
    }

    @Test
    fun test_profile_navigation_client() {
        loginAsClient()
        switchToMode("PERSONAL")
        navigateToProfile()

        assert(profileScreen.isVisible() ||
                device.hasObject(By.text("Profile")) ||
                device.hasObject(By.text("Account"))) { "Profile screen should be visible" }
        takeScreenshot("profile_navigation_client")
    }

    @Test
    fun test_profile_settings_navigation() {
        loginAsTrainer()
        switchToMode("TRAINER")
        navigateToProfile()
        moreScreen.clickProfile()
        device.waitForIdle(2000)

        val settingsOption = device.findObject(By.text("Settings"))
            ?: device.findObject(By.text("Profile Settings"))
        settingsOption?.click()
        device.waitForIdle(2000)

        val onSettings = device.hasObject(By.text("Core Info")) ||
                device.hasObject(By.text("Branding")) ||
                device.hasObject(By.text("Services"))
        assert(onSettings) { "Profile settings sub-screens should be visible" }
        takeScreenshot("profile_settings_navigation")
    }

    @Test
    fun test_logout_trainer() {
        loginAsTrainer()
        switchToMode("TRAINER")
        navigateToProfile()
        moreScreen.clickProfile()
        device.waitForIdle(2000)

        profileScreen.clickLogout()
        device.waitForIdle(3000)

        val onLogin = device.hasObject(By.text("Sign In"))
        assert(onLogin) { "Should navigate to login screen after logout" }
        takeScreenshot("logout_trainer")
    }

    @Test
    fun test_logout_client() {
        loginAsClient()
        switchToMode("PERSONAL")
        navigateToProfile()
        device.waitForIdle(1000)

        profileScreen.clickLogout()
        device.waitForIdle(3000)

        val onLogin = device.hasObject(By.text("Sign In"))
        assert(onLogin) { "Should navigate to login screen after logout" }
        takeScreenshot("logout_client")
    }

    @Test
    fun test_events_navigation_from_client() {
        loginAsClient()
        switchToMode("PERSONAL")

        val eventsTab = device.findObject(By.text("Events"))
            ?: device.findObject(By.text("Programs"))
        eventsTab?.click()
        device.waitForIdle(3000)

        val onExplore = device.hasObject(By.text("Events")) ||
                device.hasObject(By.text("Community Events")) ||
                device.hasObject(By.text("Explore"))
        assert(onExplore) { "Events/Explore screen should be visible" }
        takeScreenshot("events_navigation_client")
    }

    @Test
    fun test_events_navigation_from_trainer() {
        loginAsTrainer()
        switchToMode("TRAINER")
        navigateToMore()
        moreScreen.clickEvents()
        device.waitForIdle(2000)

        val onEvents = device.hasObject(By.text("Events")) ||
                device.hasObject(By.text("Community Events"))
        assert(onEvents) { "Events screen should be visible" }
        takeScreenshot("events_navigation_trainer")
    }

    @Test
    fun test_bottom_navigation_persistence_after_mode_switch() {
        loginAsClient()
        switchToMode("PERSONAL")

        val homeTab = device.findObject(By.text("Home"))
        homeTab?.click()
        device.waitForIdle(2000)
        assert(clientDashboardScreen.isOverviewTabVisible()) { "Should be on client dashboard" }

        switchToMode("TRAINER")
        device.waitForIdle(3000)

        val calendarTab = device.findObject(By.text("Calendar"))
            ?: device.findObject(By.text("CALENDAR"))
        calendarTab?.click()
        device.waitForIdle(2000)
        assert(calendarScreen.isVisible()) { "Should be on trainer calendar" }

        switchToMode("PERSONAL")
        device.waitForIdle(3000)

        assert(clientDashboardScreen.isOverviewTabVisible() ||
                device.hasObject(By.text("Overview"))) { "Should return to client dashboard" }
        takeScreenshot("bottom_navigation_persistence")
    }
}
