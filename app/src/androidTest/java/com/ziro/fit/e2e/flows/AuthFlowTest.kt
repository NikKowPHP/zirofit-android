package com.ziro.fit.e2e.flows

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.ziro.fit.MainActivity
import com.ziro.fit.e2e.base.BaseE2ETest
import com.ziro.fit.e2e.config.E2ETestConfig
import com.ziro.fit.e2e.helpers.AuthHelper
import com.ziro.fit.e2e.screens.EmailConfirmationScreen
import com.ziro.fit.e2e.screens.LoginScreen
import com.ziro.fit.e2e.screens.RegisterScreen
import com.ziro.fit.e2e.screens.RoleSelectionScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AuthFlowTest : BaseE2ETest() {

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule<MainActivity>(MainActivity::class.java)

    private lateinit var loginScreen: LoginScreen
    private lateinit var registerScreen: RegisterScreen
    private lateinit var emailConfirmationScreen: EmailConfirmationScreen
    private lateinit var roleSelectionScreen: RoleSelectionScreen
    private lateinit var authHelper: AuthHelper

    @Before
    override fun setUp() {
        super.setUp()
        loginScreen = LoginScreen(device)
        registerScreen = RegisterScreen(device)
        emailConfirmationScreen = EmailConfirmationScreen(device)
        roleSelectionScreen = RoleSelectionScreen(device)
        authHelper = AuthHelper(device)
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
        device.waitForWindowUpdate(null, 10_000)
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
        device.waitForWindowUpdate(null, 10_000)
    }

    private fun switchToTrainerMode() {
        val trainerTab = device.findObject(By.text("TRAINER"))
            ?: device.findObject(By.text("Trainer"))
            ?: device.findObject(By.textContains("TRAINER"))
        trainerTab?.click()
        device.waitForIdle(2000)
    }

    private fun switchToPersonalMode() {
        val personalTab = device.findObject(By.text("PERSONAL"))
            ?: device.findObject(By.text("Personal"))
            ?: device.findObject(By.textContains("PERSONAL"))
        personalTab?.click()
        device.waitForIdle(2000)
    }

    @Test
    fun test_login_screen_displays() {
        assert(loginScreen.waitForScreen()) { "Login screen should be visible" }
        assert(loginScreen.isWelcomeBackTextVisible()) { "Welcome back text should be visible" }
        assert(loginScreen.isLoginButtonVisible()) { "Sign In button should be visible" }
        assert(loginScreen.isRegisterLinkVisible()) { "Register link should be visible" }
        takeScreenshot("login_screen_displays")
    }

    @Test
    fun test_login_with_valid_trainer_credentials() {
        loginAsTrainer()

        val onDashboard = device.wait(Until.hasObject(By.text("Calendar")), 15_000) ||
                device.wait(Until.hasObject(By.text("Overview")), 5_000)
        assert(onDashboard) { "Should navigate to dashboard or calendar after login" }
        takeScreenshot("login_trainer_success")
    }

    @Test
    fun test_login_with_valid_client_credentials() {
        loginAsClient()

        val onDashboard = device.wait(Until.hasObject(By.text("Overview")), 15_000) ||
                device.wait(Until.hasObject(By.text("Calendar")), 5_000)
        assert(onDashboard) { "Should navigate to client dashboard after login" }
        takeScreenshot("login_client_success")
    }

    @Test
    fun test_login_with_invalid_credentials() {
        val emailField = device.findObject(By.clazz("android.widget.EditText"))
        emailField.setText("invalid@email.com")
        device.waitForIdle(500)
        val allFields = device.findObjects(By.clazz("android.widget.EditText"))
        if (allFields.size > 1) {
            allFields[1].setText("wrongpassword")
        }
        device.findObject(By.text("Sign In")).click()
        device.waitForIdle(3000)

        assert(loginScreen.isLoginButtonVisible()) { "Should stay on login screen on failure" }
        takeScreenshot("login_invalid_credentials")
    }

    @Test
    fun test_navigate_to_register() {
        loginScreen.clickRegisterLink()
        device.waitForIdle(2000)

        assert(registerScreen.waitForScreen()) { "Register screen should be visible" }
        assert(registerScreen.isJoinZiroFitTextVisible()) { "Join ZIRO.FIT text should be visible" }
        assert(registerScreen.isCreateAccountButtonVisible()) { "Create Account button should be visible" }
        takeScreenshot("navigate_to_register")
    }

    @Test
    fun test_navigate_back_to_login_from_register() {
        loginScreen.clickRegisterLink()
        device.waitForIdle(2000)
        registerScreen.clickLoginLink()
        device.waitForIdle(2000)

        assert(loginScreen.waitForScreen()) { "Should navigate back to login screen" }
        takeScreenshot("navigate_back_to_login")
    }

    @Test
    fun test_register_validation_empty_fields() {
        loginScreen.clickRegisterLink()
        device.waitForIdle(2000)
        registerScreen.clickCreateAccount()
        device.waitForIdle(2000)

        assert(registerScreen.isCreateAccountButtonVisible()) { "Should stay on register screen" }
        takeScreenshot("register_validation_empty")
    }

    @Test
    fun test_role_selection_as_trainer() {
        loginAsTrainer()

        val onRoleSelection = device.wait(Until.hasObject(By.text("Choose Your Role")), 5_000)
        if (onRoleSelection) {
            roleSelectionScreen.selectTrainerRole()
            assert(roleSelectionScreen.waitForOnboardingComplete()) { "Should complete onboarding" }
        } else {
            switchToTrainerMode()
        }

        val onCalendar = device.hasObject(By.text("Calendar")) ||
                device.hasObject(By.text("Today"))
        assert(onCalendar) { "Should see trainer dashboard (Calendar)" }
        takeScreenshot("role_selection_trainer")
    }

    @Test
    fun test_role_selection_as_client() {
        loginAsClient()

        val onRoleSelection = device.wait(Until.hasObject(By.text("Choose Your Role")), 5_000)
        if (onRoleSelection) {
            roleSelectionScreen.selectClientRole()
            assert(roleSelectionScreen.waitForOnboardingComplete()) { "Should complete onboarding" }
        } else {
            switchToPersonalMode()
        }

        val onDashboard = device.hasObject(By.text("Overview")) ||
                device.hasObject(By.text("Home"))
        assert(onDashboard) { "Should see client dashboard" }
        takeScreenshot("role_selection_client")
    }

    @Test
    fun test_email_confirmation_screen_elements() {
        loginScreen.clickRegisterLink()
        device.waitForIdle(2000)
        registerScreen.enterName("Test User")
        registerScreen.enterEmail("test@example.com")
        registerScreen.enterPassword("TestPass123!")
        registerScreen.enterConfirmPassword("TestPass123!")
        registerScreen.clickCreateAccount()
        device.waitForIdle(5000)

        val onConfirmation = emailConfirmationScreen.waitForScreen(10_000)
        if (onConfirmation) {
            assert(emailConfirmationScreen.isCheckYourEmailTextVisible() ||
                    emailConfirmationScreen.isWeSentVerificationTextVisible()) { "Confirmation message should be visible" }
            takeScreenshot("email_confirmation_screen")
        }
    }

    @Test
    fun test_back_to_login_from_email_confirmation() {
        loginScreen.clickRegisterLink()
        device.waitForIdle(2000)
        registerScreen.enterName("Test User")
        registerScreen.enterEmail("test@example.com")
        registerScreen.enterPassword("TestPass123!")
        registerScreen.enterConfirmPassword("TestPass123!")
        registerScreen.clickCreateAccount()
        device.waitForIdle(5000)

        val onConfirmation = emailConfirmationScreen.waitForScreen(10_000)
        if (onConfirmation) {
            emailConfirmationScreen.clickBackToLogin()
            device.waitForIdle(2000)
            assert(loginScreen.waitForScreen()) { "Should navigate back to login" }
            takeScreenshot("email_confirmation_back_to_login")
        }
    }

    @Test
    fun test_google_sign_in_button_visible() {
        assert(loginScreen.isGoogleButtonVisible()) { "Google sign in button should be visible" }
        takeScreenshot("google_sign_in_visible")
    }
}
