package com.ziro.fit.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziro.fit.data.local.TokenManager
import com.ziro.fit.data.remote.ZiroApi
import com.ziro.fit.model.LoginRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
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
    private val tokenManager: TokenManager
) : ViewModel() {

    var authState by mutableStateOf<AuthState>(AuthState.Loading)
        private set

    init {
        checkAuthStatus()
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
                } else {
                   authState = AuthState.Error("Login failed: No data received") 
                }
            } catch (e: Exception) {
                e.printStackTrace()
                authState = AuthState.Error("Login failed: ${e.message}")
            }
        }
    }

    fun logout() {
        tokenManager.clearToken()
        authState = AuthState.Unauthenticated
    }
}
