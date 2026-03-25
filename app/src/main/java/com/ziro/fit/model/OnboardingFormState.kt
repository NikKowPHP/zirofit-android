
package com.ziro.fit.model

import android.net.Uri
import com.ziro.fit.data.local.UserSessionManager
import com.ziro.fit.data.local.TokenManager
import com.ziro.fit.util.Logger


data class OnboardingFormState(
    val name: String = "",
    val location: String? = null,
    val bio: String? = null,
    val role: String = "client",
    val avatarUri: Uri? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isComplete: Boolean = false
) {
    val isValid: Boolean
        get() = name.isNotBlank()
}

class OnboardingFormStateManager(
    private val userSessionManager: UserSessionManager
) {
  
    fun rehydrate(): OnboardingFormState {
        val state = OnboardingFormState(
            name = userSessionManager.savedName ?: "",
            location = userSessionManager.savedLocation,
            bio = userSessionManager.savedBio,
            role = userSessionManager.savedRole ?: "client",
            avatarUri = userSessionManager.savedAvatarUri?.let { Uri.parse(it) }
        )
        Logger.d("onboarding_storage", "Rehydrated state from localStorage: name='${state.name}', location='${state.location}', role='${state.role}', hasAvatar=${state.avatarUri != null}")
        return state
    }

    
    fun updateName(state: OnboardingFormState, name: String): OnboardingFormState {
        Logger.d("onboarding_state", "updateName: '$name'")
        userSessionManager.savedName = name
        Logger.d("onboarding_storage", "Saved name to localStorage: '$name'")
        return state.copy(name = name)
    }

  
    fun updateLocation(state: OnboardingFormState, location: String?): OnboardingFormState {
        Logger.d("onboarding_state", "updateLocation: '$location'")
        userSessionManager.savedLocation = location
        Logger.d("onboarding_storage", "Saved location to localStorage: '$location'")
        return state.copy(location = location)
    }

  
    fun updateBio(state: OnboardingFormState, bio: String?): OnboardingFormState {
        Logger.d("onboarding_state", "updateBio: '$bio'")
        userSessionManager.savedBio = bio
        Logger.d("onboarding_storage", "Saved bio to localStorage: '$bio'")
        return state.copy(bio = bio)
    }

    
    fun updateRole(state: OnboardingFormState, role: String): OnboardingFormState {
        Logger.d("onboarding_state", "updateRole: '$role'")
        userSessionManager.savedRole = role
        Logger.d("onboarding_storage", "Saved role to localStorage: '$role'")
        return state.copy(role = role)
    }

    
    fun updateAvatar(state: OnboardingFormState, uri: Uri?): OnboardingFormState {
        Logger.d("onboarding_state", "updateAvatar: ${uri?.toString()}")
        userSessionManager.savedAvatarUri = uri?.toString()
        Logger.d("onboarding_storage", "Saved avatarUri to localStorage: ${uri?.toString()}")
        return state.copy(avatarUri = uri)
    }

   
    fun clearSession() {
        Logger.d("onboarding_storage", "Cleared session from localStorage")
        userSessionManager.clearSession()
    }
}
