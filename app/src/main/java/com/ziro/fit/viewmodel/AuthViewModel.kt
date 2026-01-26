package com.ziro.fit.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import com.ziro.fit.data.local.TokenManager
import com.ziro.fit.data.remote.ZiroApi
import com.ziro.fit.data.repository.ProfileRepository
import com.ziro.fit.model.LoginRequest
import com.ziro.fit.model.RegisterRequest
import com.ziro.fit.util.ApiErrorParser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

sealed class AuthState {
    object Loading : AuthState()
    data class Authenticated(val role: String, val isOnboardingComplete: Boolean = true) : AuthState()
    object Unauthenticated : AuthState()
    data class Error(val message: String) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val api: ZiroApi,
    private val tokenManager: TokenManager,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    var authState by mutableStateOf<AuthState>(AuthState.Loading)
        private set

    init {
        setupLogoutCollection()
        checkAuthStatus()
    }
    
    private fun setupLogoutCollection() {
        viewModelScope.launch {
            tokenManager.logoutSignal.collect {
                authState = AuthState.Unauthenticated
            }
        }
    }

    private fun checkAuthStatus() {
        if (tokenManager.getToken() != null) {
            viewModelScope.launch {
                authState = AuthState.Loading
                try {
                    val userResponse = api.getMe()
                    val user = userResponse.data
                    if (user != null) {
                        val role = user.role ?: "pending"
                        authState = AuthState.Authenticated(role, isOnboardingComplete = role != "pending")
                        syncPushToken() // Sync on startup check
                    } else {
                        authState = AuthState.Unauthenticated
                        tokenManager.clearToken()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    authState = AuthState.Unauthenticated
                    tokenManager.clearToken()
                }
            }
        } else {
            authState = AuthState.Unauthenticated
        }
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            authState = AuthState.Loading
            try {
                val response = api.login(LoginRequest(email, pass))
                val loginData = response.data
                if (loginData != null) {
                    tokenManager.saveToken(loginData.accessToken)
                    val role = loginData.role
                    authState = AuthState.Authenticated(role, isOnboardingComplete = role != "pending")
                    syncPushToken() 
                } else {
                   authState = AuthState.Error(response.message ?: "Login failed: No data received") 
                }
            } catch (e: Exception) {
                e.printStackTrace()
                val apiError = ApiErrorParser.parse(e)
                authState = AuthState.Error(ApiErrorParser.getErrorMessage(apiError))
            }
        }
    }

    fun register(name: String, email: String, pass: String) {
        viewModelScope.launch {
            authState = AuthState.Loading
            try {
                val response = api.register(RegisterRequest(name, email, pass))
                if (response.success == true || response.data != null) {
                    login(email, pass)
                } else {
                    authState = AuthState.Error(response.message ?: "Registration failed")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                val apiError = ApiErrorParser.parse(e)
                authState = AuthState.Error(ApiErrorParser.getErrorMessage(apiError))
            }
        }
    }

    fun completeLocalOnboarding(role: String) {
        if (authState is AuthState.Authenticated) {
            authState = AuthState.Authenticated(role, isOnboardingComplete = true)
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

    fun logout() {
        tokenManager.clearToken()
        authState = AuthState.Unauthenticated
    }

    fun clearError() {
        if (authState is AuthState.Error) {
            authState = AuthState.Unauthenticated
        }
    }
}
