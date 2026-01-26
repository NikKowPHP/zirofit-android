package com.ziro.fit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.ziro.fit.data.remote.ZiroApi
import com.ziro.fit.util.ApiErrorParser
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import java.io.File

data class OnboardingUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
    val selectedRole: String = "client"
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val api: ZiroApi,
    @ApplicationContext private val context: android.content.Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    var avatarUri by mutableStateOf<Uri?>(null)
        private set

    fun setAvatar(uri: Uri?) {
        avatarUri = uri
    }

    private fun uriToFile(uri: Uri): File? {
        return try {
            val contentResolver = context.contentResolver
            val mimeType = contentResolver.getType(uri)
            val extension = if (mimeType != null) {
                android.webkit.MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) ?: "jpg"
            } else {
                "jpg"
            }

            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val tempFile = File.createTempFile("avatar", ".$extension", context.cacheDir)
            tempFile.outputStream().use { output ->
                inputStream.copyTo(output)
            }
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

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
                val mediaTypeText = "text/plain".toMediaTypeOrNull()

                val roleBody = _uiState.value.selectedRole.toRequestBody(mediaTypeText)
                val nameBody = name.toRequestBody(mediaTypeText)
                val locationBody = location?.toRequestBody(mediaTypeText)
                val bioBody = bio?.toRequestBody(mediaTypeText)

                var avatarPart: MultipartBody.Part? = null
                avatarUri?.let { uri ->
                    val file = uriToFile(uri)
                    if (file != null) {
                        val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
                        val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
                        avatarPart = MultipartBody.Part.createFormData("avatar", file.name, requestFile)
                    }
                }

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
