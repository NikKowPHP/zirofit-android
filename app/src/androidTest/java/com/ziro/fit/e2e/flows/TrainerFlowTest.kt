package com.ziro.fit.e2e.flows

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.uiautomator.By
import com.ziro.fit.MainActivity
import com.ziro.fit.e2e.base.BaseE2ETest
import com.ziro.fit.e2e.config.E2ETestConfig
import com.ziro.fit.e2e.screens.BookingsScreen
import com.ziro.fit.e2e.screens.CalendarScreen
import com.ziro.fit.e2e.screens.ClientDetailsScreen
import com.ziro.fit.e2e.screens.ClientsScreen
import com.ziro.fit.e2e.screens.CreateSessionScreen
import com.ziro.fit.e2e.screens.LiveWorkoutScreen
import com.ziro.fit.e2e.screens.MoreScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TrainerFlowTest : BaseE2ETest() {

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule<MainActivity>(MainActivity::class.java)

    private lateinit var calendarScreen: CalendarScreen
    private lateinit var clientsScreen: ClientsScreen
    private lateinit var clientDetailsScreen: ClientDetailsScreen
    private lateinit var moreScreen: MoreScreen
    private lateinit var bookingsScreen: BookingsScreen
    private lateinit var createSessionScreen: CreateSessionScreen
    private lateinit var liveWorkoutScreen: LiveWorkoutScreen

    @Before
    override fun setUp() {
        super.setUp()
        calendarScreen = CalendarScreen(device)
        clientsScreen = ClientsScreen(device)
        clientDetailsScreen = ClientDetailsScreen(device)
        moreScreen = MoreScreen(device)
        bookingsScreen = BookingsScreen(device)
        createSessionScreen = CreateSessionScreen(device)
        liveWorkoutScreen = LiveWorkoutScreen(device)
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

    private fun ensureTrainerMode() {
        val trainerTab = device.findObject(By.text("TRAINER"))
            ?: device.findObject(By.textContains("TRAINER"))
        trainerTab?.click()
        device.waitForIdle(2000)
    }

    private fun navigateToCalendar() {
        val calendarTab = device.findObject(By.text("Calendar"))
            ?: device.findObject(By.text("CALENDAR"))
            ?: device.findObject(By.text("Home"))
        calendarTab?.click()
        device.waitForIdle(2000)
    }

    private fun navigateToClients() {
        val clientsTab = device.findObject(By.text("Clients"))
            ?: device.findObject(By.text("CLIENTS"))
        clientsTab?.click()
        device.waitForIdle(2000)
    }

    private fun navigateToMore() {
        val moreTab = device.findObject(By.text("More"))
            ?: device.findObject(By.text("MORE"))
        moreTab?.click()
        device.waitForIdle(2000)
    }

    @Test
    fun test_trainer_sees_calendar_on_launch() {
        loginAsTrainer()
        ensureTrainerMode()
        navigateToCalendar()

        assert(calendarScreen.isVisible()) { "Calendar screen should be visible" }
        takeScreenshot("trainer_calendar")
    }

    @Test
    fun test_trainer_calendar_navigation() {
        loginAsTrainer()
        ensureTrainerMode()
        navigateToCalendar()

        calendarScreen.scrollToNextPeriod()
        device.waitForIdle(1000)
        calendarScreen.scrollToPreviousPeriod()
        device.waitForIdle(1000)

        assert(calendarScreen.isVisible()) { "Calendar should remain visible after navigation" }
        takeScreenshot("trainer_calendar_navigation")
    }

    @Test
    fun test_trainer_create_session_button_visible() {
        loginAsTrainer()
        ensureTrainerMode()
        navigateToCalendar()

        calendarScreen.waitForSessionsLoad()
        assert(calendarScreen.isCreateSessionButtonVisible() ||
                calendarScreen.isCreateSessionButtonVisible()) { "Create session button should be visible" }
        takeScreenshot("trainer_create_session_button")
    }

    @Test
    fun test_trainer_open_create_session_form() {
        loginAsTrainer()
        ensureTrainerMode()
        navigateToCalendar()
        calendarScreen.waitForSessionsLoad()
        calendarScreen.clickCreateSession()
        device.waitForIdle(2000)

        assert(createSessionScreen.isVisible()) { "Create session screen should open" }
        takeScreenshot("trainer_open_create_session")
    }

    @Test
    fun test_trainer_clients_list_displays() {
        loginAsTrainer()
        ensureTrainerMode()
        navigateToClients()

        assert(clientsScreen.isVisible()) { "Clients screen should be visible" }
        device.waitForIdle(2000)

        val hasContent = clientsScreen.hasClientCards() || clientsScreen.isSearchBarVisible()
        assert(hasContent) { "Clients screen should have content or search" }
        takeScreenshot("trainer_clients_list")
    }

    @Test
    fun test_trainer_view_client_details() {
        loginAsTrainer()
        ensureTrainerMode()
        navigateToClients()
        device.waitForIdle(2000)

        val clientCard = device.findObject(By.textContains("Calendar"))
            ?: device.findObject(By.textContains("Test Client"))
            ?: device.findObject(By.textContains("Client"))

        if (clientCard != null) {
            clientCard.click()
            device.waitForIdle(2000)
            assert(clientDetailsScreen.isVisible()) { "Client details should be visible" }
            takeScreenshot("trainer_client_details")
        }
    }

    @Test
    fun test_trainer_client_details_navigation() {
        loginAsTrainer()
        ensureTrainerMode()
        navigateToClients()
        device.waitForIdle(2000)

        val clientCard = device.findObject(By.textContains("Calendar"))
            ?: device.findObject(By.textContains("Test Client"))
            ?: device.findObject(By.textContains("Client"))

        if (clientCard != null) {
            clientCard.click()
            device.waitForIdle(2000)

            if (clientDetailsScreen.isVisible()) {
                clientDetailsScreen.clickSessions()
                device.waitForIdle(2000)
                clientDetailsScreen.clickPrograms()
                device.waitForIdle(2000)
            }
            takeScreenshot("trainer_client_details_nav")
        }
    }

    @Test
    fun test_trainer_navigate_to_bookings() {
        loginAsTrainer()
        ensureTrainerMode()
        navigateToMore()
        moreScreen.clickBookings()

        assert(bookingsScreen.isVisible()) { "Bookings screen should be visible" }
        takeScreenshot("trainer_bookings")
    }

    @Test
    fun test_trainer_navigate_to_assessments() {
        loginAsTrainer()
        ensureTrainerMode()
        navigateToMore()
        moreScreen.clickAssessments()

        val onAssessments = device.hasObject(By.text("Assessments")) ||
                device.hasObject(By.text("Fitness Assessments")) ||
                device.hasObject(By.text("Templates"))
        assert(onAssessments) { "Assessments screen should be visible" }
        takeScreenshot("trainer_assessments")
    }

    @Test
    fun test_trainer_navigate_to_check_ins() {
        loginAsTrainer()
        ensureTrainerMode()
        navigateToMore()
        moreScreen.clickCheckIns()

        val onCheckIns = device.hasObject(By.text("Check-Ins")) ||
                device.hasObject(By.text("Client Check-Ins"))
        assert(onCheckIns) { "Check-ins screen should be visible" }
        takeScreenshot("trainer_check_ins")
    }

    @Test
    fun test_trainer_live_workout_flow() {
        loginAsTrainer()
        ensureTrainerMode()
        navigateToCalendar()

        val startWorkout = device.findObject(By.textContains("Start"))
            ?: device.findObject(By.text("Start Workout"))
        startWorkout?.click()
        device.waitForIdle(3000)

        val onWorkout = liveWorkoutScreen.isVisible() ||
                device.hasObject(By.text("Add Exercise"))
        assert(onWorkout) { "Live workout screen should be visible" }
        takeScreenshot("trainer_live_workout")
    }

    @Test
    fun test_trainer_bottom_navigation() {
        loginAsTrainer()
        ensureTrainerMode()

        navigateToCalendar()
        assert(calendarScreen.isVisible()) { "Should be on calendar" }

        navigateToClients()
        assert(clientsScreen.isVisible()) { "Should be on clients" }

        navigateToMore()
        assert(moreScreen.isVisible()) { "Should be on more" }

        takeScreenshot("trainer_bottom_navigation")
    }
}
