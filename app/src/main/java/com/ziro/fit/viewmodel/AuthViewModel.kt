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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

sealed class AuthState {
    object Loading : AuthState()
    data class Authenticated(val role: String) : AuthState()
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
                        authState = AuthState.Authenticated(user.role ?: "trainer")
                        syncPushToken() // Sync on startup check
                    } else {
                        // Token might be valid but can't get user? Treat as error or unauth
                        authState = AuthState.Unauthenticated
                        tokenManager.clearToken()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    // If we can't get user details (e.g. 401), assume unauthenticated
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
                // Save the token from the response
                val loginData = response.data
                if (loginData != null) {
                    tokenManager.saveToken(loginData.accessToken)
                    authState = AuthState.Authenticated(loginData.role)
                    syncPushToken() // Sync on explicit login
                } else {
                   authState = AuthState.Error("Login failed: No data received") 
                }
            } catch (e: Exception) {
                e.printStackTrace()
                authState = AuthState.Error("Login failed: ${e.message}")
            }
        }
    }

    private fun syncPushToken() {
        viewModelScope.launch {
            try {
                // Check if Firebase is initialized and get token
                val token = FirebaseMessaging.getInstance().token.await() // suspending function
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
}
