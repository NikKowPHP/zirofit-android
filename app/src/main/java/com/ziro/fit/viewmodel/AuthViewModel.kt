package com.ziro.fit.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import com.ziro.fit.auth.GoogleAuthManager
import com.ziro.fit.data.local.TokenManager
import com.ziro.fit.data.remote.ZiroApi
import com.ziro.fit.data.repository.CalendarRepository
import com.ziro.fit.data.repository.ClientDashboardRepository
import com.ziro.fit.data.repository.ExerciseRepository
import com.ziro.fit.data.repository.ProfileRepository
import com.ziro.fit.model.AppMode
import com.ziro.fit.model.LoginRequest
import com.ziro.fit.model.RegisterRequest
import com.ziro.fit.model.SignOutRequest
import com.ziro.fit.model.ForgotPasswordRequest
import com.ziro.fit.model.UpdatePasswordRequest
import com.ziro.fit.util.ApiErrorParser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import javax.inject.Inject

sealed class AuthState {
    object Loading : AuthState()
    data class Authenticated(
        val role: String,
        val userId: String,
        val isOnboardingComplete: Boolean = true
    ) : AuthState()
    object Unauthenticated : AuthState()
    data class Error(val message: String) : AuthState()
    data class EmailConfirmationRequired(val email: String) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val api: ZiroApi,
    private val tokenManager: TokenManager,
    private val googleAuthManager: GoogleAuthManager,
    private val profileRepository: ProfileRepository,
    private val dashboardRepository: ClientDashboardRepository,
    private val calendarRepository: CalendarRepository,
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    var authState by mutableStateOf<AuthState>(AuthState.Loading)
        private set

    var uiLoading by mutableStateOf(false)
        private set
    var uiError by mutableStateOf<String?>(null)
        private set

    var activeMode by mutableStateOf(AppMode.TRAINER)
        private set

    var isTrainerAuthenticated by mutableStateOf(false)
        private set
    var isPersonalAuthenticated by mutableStateOf(false)
        private set

    private var trainerUserId: String? = null
    private var personalUserId: String? = null

    val isAnyAuthenticated: Boolean
        get() = isTrainerAuthenticated || isPersonalAuthenticated

    init {
        activeMode = tokenManager.activeMode.value
        tokenManager.activeMode.onEach { mode ->
            activeMode = mode
        }.launchIn(viewModelScope)

        setupLogoutCollection()
        checkAuthStatus()
    }

    private fun setupLogoutCollection() {
        viewModelScope.launch {
            tokenManager.logoutSignal.collect {
                isTrainerAuthenticated = false
                isPersonalAuthenticated = false
                authState = AuthState.Unauthenticated
            }
        }
    }

    private fun checkAuthStatus() {
        viewModelScope.launch {
            authState = AuthState.Loading

            val currentMode = tokenManager.activeMode.value
            val token = tokenManager.getToken(currentMode)

            if (token != null) {
                try {
                    val userResponse = api.getMe()
                    val user = userResponse.data
                    if (user != null) {
                        val role = user.role ?: "pending"
                        authState = AuthState.Authenticated(role, user.id, isOnboardingComplete = role != "pending")
                        markModeAuthenticated(currentMode, user.id)
                        syncPushToken()
                        triggerPrefetch(currentMode)
                    } else {
                        handleUnauthenticated()
                    }
                } catch (e: Exception) {
                    if (tokenManager.hasAnyToken()) {
                        handleUnauthenticated()
                    } else {
                        authState = AuthState.Unauthenticated
                    }
                }
            } else {
                val otherMode = if (currentMode == AppMode.TRAINER) AppMode.PERSONAL else AppMode.TRAINER
                if (tokenManager.hasToken(otherMode)) {
                    tokenManager.setActiveMode(otherMode)
                    checkAuthStatus()
                } else {
                    authState = AuthState.Unauthenticated
                }
            }
        }
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            uiLoading = true
            uiError = null
            try {
                val response = api.login(LoginRequest(email, pass))
                val loginData = response.data
                if (loginData != null) {
                    val currentMode = tokenManager.activeMode.value
                    tokenManager.saveToken(loginData.accessToken, currentMode)
                    loginData.refreshToken?.let { tokenManager.saveRefreshToken(it, currentMode) }

                    val detectedMode = detectModeFromRole(loginData.role)
                    if (detectedMode != currentMode) {
                        val oldToken = tokenManager.getToken(currentMode)
                        tokenManager.clearToken(currentMode)
                        tokenManager.setActiveMode(detectedMode)
                        tokenManager.saveToken(loginData.accessToken, detectedMode)
                        loginData.refreshToken?.let { tokenManager.saveRefreshToken(it, detectedMode) }
                    }

                    val role = loginData.role
                    val userId = loginData.user.id
                    authState = AuthState.Authenticated(role, userId, isOnboardingComplete = role != "pending")
                    markModeAuthenticated(detectedMode, userId)
                    syncPushToken()
                    triggerPrefetch(detectedMode)
                } else {
                    uiError = response.message ?: "Login failed: No data received"
                }
            } catch (e: Exception) {
                val apiError = ApiErrorParser.parse(e)
                uiError = ApiErrorParser.getErrorMessage(apiError)
            } finally {
                uiLoading = false
            }
        }
    }

    fun handleGoogleAuthResult(accessToken: String, refreshToken: String?, userId: String, role: String) {
        viewModelScope.launch {
            uiLoading = true
            uiError = null
            try {
                val currentMode = tokenManager.activeMode.value
                tokenManager.saveToken(accessToken, currentMode)
                refreshToken?.let { tokenManager.saveRefreshToken(it, currentMode) }

                val detectedMode = detectModeFromRole(role)
                if (detectedMode != currentMode) {
                    tokenManager.clearToken(currentMode)
                    tokenManager.setActiveMode(detectedMode)
                    tokenManager.saveToken(accessToken, detectedMode)
                    refreshToken?.let { tokenManager.saveRefreshToken(it, detectedMode) }
                }

                authState = AuthState.Authenticated(role, userId, isOnboardingComplete = role != "pending")
                markModeAuthenticated(detectedMode, userId)
                syncPushToken()
                triggerPrefetch(detectedMode)
            } finally {
                uiLoading = false
            }
        }
    }

    fun handleAppleAuthResult(idToken: String, authCode: String, email: String?, userId: String, role: String) {
        viewModelScope.launch {
            uiLoading = true
            uiError = null
            try {
                val currentMode = tokenManager.activeMode.value
                tokenManager.saveToken(idToken, currentMode)

                val detectedMode = detectModeFromRole(role)
                if (detectedMode != currentMode) {
                    tokenManager.clearToken(currentMode)
                    tokenManager.setActiveMode(detectedMode)
                    tokenManager.saveToken(idToken, detectedMode)
                }

                authState = AuthState.Authenticated(role, userId, isOnboardingComplete = role != "pending")
                markModeAuthenticated(detectedMode, userId)
                syncPushToken()
                triggerPrefetch(detectedMode)
            } finally {
                uiLoading = false
            }
        }
    }

    fun register(name: String, email: String, pass: String) {
        viewModelScope.launch {
            uiLoading = true
            uiError = null
            try {
                val deepLinkRedirect = "zirofitapp://login?verified=true"
                val response = api.register(RegisterRequest(name, email, pass, redirect = deepLinkRedirect))
                if (response.success == true || response.data != null) {
                    val data = response.data
                    if (data?.confirmationRequired == true) {
                        uiLoading = false
                        authState = AuthState.EmailConfirmationRequired(email)
                    } else {
                        login(email, pass)
                    }
                } else {
                    uiError = response.message ?: "Registration failed"
                    uiLoading = false
                }
            } catch (e: Exception) {
                val apiError = ApiErrorParser.parse(e)
                uiError = ApiErrorParser.getErrorMessage(apiError)
                uiLoading = false
            }
        }
    }

    fun setMode(mode: AppMode) {
        if (activeMode == mode) return

        viewModelScope.launch {
            val previousMode = activeMode
            tokenManager.setActiveMode(mode)
            activeMode = mode

            val alreadyAuth = tokenManager.hasToken(mode)
            if (alreadyAuth) {
                markModeAuthenticated(mode, getUserIdForMode(mode) ?: "")
                authState = AuthState.Authenticated(
                    role = getRoleForMode(mode),
                    userId = getUserIdForMode(mode) ?: "",
                    isOnboardingComplete = true
                )
                triggerPrefetch(mode)
            } else {
                markModeUnauthenticated(mode)
                val currentState = authState
                if (currentState is AuthState.Authenticated && currentState.userId == getUserIdForMode(previousMode)) {
                    authState = AuthState.Unauthenticated
                }
                checkAuthStatus()
            }
        }
    }

    private fun markModeAuthenticated(mode: AppMode, userId: String) {
        when (mode) {
            AppMode.TRAINER -> {
                isTrainerAuthenticated = true
                trainerUserId = userId
            }
            AppMode.PERSONAL -> {
                isPersonalAuthenticated = true
                personalUserId = userId
            }
        }
    }

    private fun markModeUnauthenticated(mode: AppMode) {
        when (mode) {
            AppMode.TRAINER -> {
                isTrainerAuthenticated = false
                trainerUserId = null
            }
            AppMode.PERSONAL -> {
                isPersonalAuthenticated = false
                personalUserId = null
            }
        }
    }

    private fun getUserIdForMode(mode: AppMode): String? {
        return when (mode) {
            AppMode.TRAINER -> trainerUserId
            AppMode.PERSONAL -> personalUserId
        }
    }

    private fun getRoleForMode(mode: AppMode): String {
        return when (mode) {
            AppMode.TRAINER -> "trainer"
            AppMode.PERSONAL -> "client"
        }
    }

    private fun detectModeFromRole(role: String): AppMode {
        val r = role.lowercase()
        return if (r.contains("trainer") || r.contains("coach") ||
                   r.contains("instructor") || r.contains("admin") ||
                   r.contains("staff") || r.contains("owner")) {
            AppMode.TRAINER
        } else {
            AppMode.PERSONAL
        }
    }

    private fun triggerPrefetch(mode: AppMode) {
        viewModelScope.launch {
            when (mode) {
                AppMode.TRAINER -> {
                    launch { calendarRepository.getEvents(LocalDate.now()) }
                    launch { calendarRepository.getCalendarSummary(LocalDate.now()) }
                    launch { exerciseRepository.getExercises(null, 1) }
                }
                AppMode.PERSONAL -> {
                    launch { dashboardRepository.getClientDashboard() }
                    launch { dashboardRepository.getWorkoutHistory(20, null) }
                    launch { dashboardRepository.getActiveProgramProgress() }
                    launch { exerciseRepository.getExercises(null, 1) }
                }
            }
        }
    }

    fun completeLocalOnboarding(role: String) {
        val currentState = authState
        if (currentState is AuthState.Authenticated) {
            authState = AuthState.Authenticated(role, currentState.userId, isOnboardingComplete = true)
        }
    }

    private fun syncPushToken() {
        viewModelScope.launch {
            try {
                val token = FirebaseMessaging.getInstance().token.await()
                profileRepository.registerPushToken(token)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun logout(mode: AppMode? = null) {
        viewModelScope.launch {
            val targetMode = mode ?: activeMode

            val token = tokenManager.getToken(targetMode)
            if (token != null) {
                try {
                    api.signOut(SignOutRequest(token))
                } catch (_: Exception) { }
            }

            tokenManager.clearToken(targetMode)
            markModeUnauthenticated(targetMode)

            val otherMode = if (targetMode == AppMode.TRAINER) AppMode.PERSONAL else AppMode.TRAINER
            if (tokenManager.hasToken(otherMode)) {
                tokenManager.setActiveMode(otherMode)
                activeMode = otherMode
                val otherUserId = getUserIdForMode(otherMode)
                val otherAuth = isTrainerAuthenticated || isPersonalAuthenticated
                authState = AuthState.Authenticated(
                    role = getRoleForMode(otherMode),
                    userId = otherUserId ?: "",
                    isOnboardingComplete = true
                )
            } else {
                authState = AuthState.Unauthenticated
            }
        }
    }

    fun logoutAll() {
        viewModelScope.launch {
            val currentToken = tokenManager.getToken(activeMode)
            if (currentToken != null) {
                try {
                    api.signOut(SignOutRequest(currentToken))
                } catch (_: Exception) { }
            }
            tokenManager.triggerLogoutAll()
        }
    }

    private fun handleUnauthenticated() {
        tokenManager.clearToken()
        authState = AuthState.Unauthenticated
        isTrainerAuthenticated = false
        isPersonalAuthenticated = false
    }

    fun forgotPassword(email: String) {
        viewModelScope.launch {
            uiLoading = true
            uiError = null
            try {
                val response = api.forgotPassword(ForgotPasswordRequest(email))
                if (response.success != false && response.data != null) {
                    uiLoading = false
                } else {
                    uiError = response.message ?: "Failed to send reset email"
                    uiLoading = false
                }
            } catch (e: Exception) {
                val apiError = ApiErrorParser.parse(e)
                uiError = ApiErrorParser.getErrorMessage(apiError)
                uiLoading = false
            }
        }
    }

    fun updatePassword(password: String) {
        viewModelScope.launch {
            uiLoading = true
            uiError = null
            try {
                val response = api.updatePassword(UpdatePasswordRequest(password))
                if (response.success != false && response.data != null) {
                    uiLoading = false
                } else {
                    uiError = response.message ?: "Failed to update password"
                    uiLoading = false
                }
            } catch (e: Exception) {
                val apiError = ApiErrorParser.parse(e)
                uiError = ApiErrorParser.getErrorMessage(apiError)
                uiLoading = false
            }
        }
    }

    fun clearError() {
        uiError = null
    }

    fun resetToUnauthenticated() {
        authState = AuthState.Unauthenticated
    }

    fun handleAppleAuthResult(idToken: String, authorizationCode: String, fullName: String?) {
        viewModelScope.launch {
            uiLoading = true
            uiError = null
            try {
                val response = api.appleAuth(
                    com.ziro.fit.model.AppleAuthRequest(
                        idToken = idToken,
                        authorizationCode = authorizationCode,
                        fullName = fullName
                    )
                )
                val loginData = response.data
                if (loginData != null) {
                    val currentMode = tokenManager.activeMode.value
                    tokenManager.saveToken(loginData.accessToken, currentMode)
                    loginData.refreshToken?.let { tokenManager.saveRefreshToken(it, currentMode) }

                    val detectedMode = detectModeFromRole(loginData.role)
                    if (detectedMode != currentMode) {
                        tokenManager.clearToken(currentMode)
                        tokenManager.setActiveMode(detectedMode)
                        tokenManager.saveToken(loginData.accessToken, detectedMode)
                        loginData.refreshToken?.let { tokenManager.saveRefreshToken(it, detectedMode) }
                    }

                    val role = loginData.role
                    val userId = loginData.user.id
                    authState = AuthState.Authenticated(role, userId, isOnboardingComplete = role != "pending")
                    markModeAuthenticated(detectedMode, userId)
                    syncPushToken()
                    triggerPrefetch(detectedMode)
                } else {
                    uiError = response.message ?: "Apple authentication failed"
                    uiLoading = false
                }
            } catch (e: Exception) {
                val apiError = ApiErrorParser.parse(e)
                uiError = ApiErrorParser.getErrorMessage(apiError)
                uiLoading = false
            }
        }
    }
}
