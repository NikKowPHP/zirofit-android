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
    object Authenticated : AuthState()
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
        // Check if we have a token saved
        if (tokenManager.getToken() != null) {
            authState = AuthState.Authenticated
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
                tokenManager.saveToken(response.data!!.accessToken)
                authState = AuthState.Authenticated
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
