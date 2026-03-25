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
import com.ziro.fit.data.local.UserSessionManager
import com.ziro.fit.model.OnboardingFormState
import com.ziro.fit.model.OnboardingFormStateManager
import com.ziro.fit.util.Logger

data class OnboardingUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
    val selectedRole: String = "client"
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val api: ZiroApi,
    private val userSessionManager: UserSessionManager,
    
    @ApplicationContext private val context: android.content.Context
) : ViewModel() {
  private val formStateManager = OnboardingFormStateManager(userSessionManager)
    val initialName: String get() = userSessionManager.savedName ?: ""
    val initialLocation: String? get() = userSessionManager.savedLocation
    val initialBio: String? get() = userSessionManager.savedBio

   private val _uiState = MutableStateFlow(formStateManager.rehydrate())
    val uiState: StateFlow<OnboardingFormState> = _uiState.asStateFlow()


    var avatarUri by mutableStateOf<Uri?>(null)
        private set

    fun setAvatar(uri: Uri?) {
        avatarUri = uri
    }

    fun updateName(name: String) {
        _uiState.update { formStateManager.updateName(it, name) }
    }

    fun updateLocation(location: String?) {
        _uiState.update { formStateManager.updateLocation(it, location) }

    }

    fun updateBio(bio: String?) {
        _uiState.update { formStateManager.updateBio(it, bio) }

    }

    fun updateRole(role: String) {
        _uiState.update { formStateManager.updateRole(it, role) }
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
        _uiState.update { it.copy(role = role) }
    }

    fun completeOnboarding(name: String, location: String?, bio: String?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val mediaTypeText = "text/plain".toMediaTypeOrNull()

                val roleBody = _uiState.value.role.toRequestBody(mediaTypeText)
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

                Logger.d("onboarding_api", "Sending to server - role: '${_uiState.value.role}', name: '$name', location: '$location', bio: '${bio?.take(50)}...', hasAvatar: ${avatarPart != null}")

                val response = api.completeOnboarding(
                    role = roleBody,
                    name = nameBody,
                    location = locationBody,
                    bio = bioBody,
                    avatar = avatarPart
                )

                Logger.d("onboarding_api", "Response received - success: ${response.success}, hasData: ${response.data != null}, message: '${response.message}', error: '${response.error}'")

                response.data?.let { user ->
                    Logger.d("onboarding_api", "User data from server - id: '${user.id}', name: '${user.name}', role: '${user.role}', email: '${user.email}'")
                }

                if (response.success == true || response.data != null) {
                    Logger.d("onboarding_storage", "Onboarding successful - clearing local session")
                    userSessionManager.clearSession()
                    formStateManager.clearSession()
                    _uiState.update { it.copy(isLoading = false) }
                } else {
                    Logger.e("onboarding_api", "Onboarding failed: ${response.message ?: response.error}")
                    _uiState.update { it.copy(isLoading = false, error = response.message ?: "Onboarding failed") }
                }

            } catch (e: Exception) {
                Logger.e("onboarding_api", "Onboarding exception: ${e.message}", e)
                val apiError = ApiErrorParser.parse(e)
                _uiState.update { it.copy(isLoading = false, error = ApiErrorParser.getErrorMessage(apiError)) }
            }
        }
    }
}
