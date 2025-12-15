package com.ziro.fit.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziro.fit.data.remote.ZiroApi
import com.ziro.fit.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val api: ZiroApi
) : ViewModel() {

    var user by mutableStateOf<User?>(null)
        private set
    var isLoading by mutableStateOf(false)
        private set

    // Automatically fetch user when ViewModel is created
    init {
        fetchUserProfile()
    }

    fun fetchUserProfile() {
        viewModelScope.launch {
            isLoading = true
            try {
                // This call uses the Interceptor to attach the token
                val response = api.getMe() 
                user = response.data
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }
}
