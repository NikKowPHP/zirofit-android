package com.ziro.fit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ziro.fit.data.remote.ZiroApi
import com.ziro.fit.util.ApiErrorParser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

data class OnboardingUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
    val selectedRole: String = "client"
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val api: ZiroApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun selectRole(role: String) {
        _uiState.update { it.copy(selectedRole = role) }
    }

    fun completeOnboarding(name: String, location: String?, bio: String?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val textMediaType = "text/plain".toMediaTypeOrNull()
                val roleBody = _uiState.value.selectedRole.toRequestBody(textMediaType)
                val nameBody = name.toRequestBody(textMediaType)
                val locationBody = location?.toRequestBody(textMediaType)
                val bioBody = bio?.toRequestBody(textMediaType)
                
                // Avatar upload not fully implemented in UI yet, passing null
                val avatarPart: MultipartBody.Part? = null

                val response = api.completeOnboarding(
                    role = roleBody,
                    name = nameBody,
                    location = locationBody,
                    bio = bioBody,
                    avatar = avatarPart
                )

                if (response.success == true || response.data != null) {
                    _uiState.update { it.copy(isLoading = false, success = true) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = response.message ?: "Onboarding failed") }
                }

            } catch (e: Exception) {
                val apiError = ApiErrorParser.parse(e)
                _uiState.update { it.copy(isLoading = false, error = ApiErrorParser.getErrorMessage(apiError)) }
            }
        }
    }
}
