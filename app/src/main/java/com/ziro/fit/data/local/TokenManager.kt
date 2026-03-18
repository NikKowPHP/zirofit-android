package com.ziro.fit.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext context: Context,
    private val api: dagger.Lazy<com.ziro.fit.data.remote.ZiroApi>
) {
    private val _logoutSignal = MutableSharedFlow<Unit>(replay = 0)
    val logoutSignal: SharedFlow<Unit> = _logoutSignal.asSharedFlow()
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        "secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveToken(token: String) {
        prefs.edit().putString("access_token", token).apply()
    }

    fun getToken(): String? {
        return prefs.getString("access_token", null)
    }

    fun saveRefreshToken(token: String) {
        prefs.edit().putString("refresh_token", token).apply()
    }

    fun getRefreshToken(): String? {
        return prefs.getString("refresh_token", null)
    }

    fun clearToken() {
        prefs.edit().remove("access_token").apply()
        prefs.edit().remove("refresh_token").apply()
    }

    suspend fun refreshToken(): Boolean {
        val refreshToken = getRefreshToken() ?: return false
        return try {
            // Revalidate with server using the refresh token
            val response = api.get().refreshToken(com.ziro.fit.model.RefreshTokenRequest(refreshToken))
            val data = response.data
            
            if (response.success != false && data != null) {
                saveToken(data.accessToken)
                saveRefreshToken(data.refreshToken)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            // If the refresh token itself is expired or invalid, the server returns 401/400
            false
        }
    }

    suspend fun triggerLogout() {
        clearToken()
        _logoutSignal.emit(Unit)
    }
}
