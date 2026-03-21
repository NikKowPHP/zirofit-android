package com.ziro.fit.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.ziro.fit.model.AppMode
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext context: Context,
    private val api: dagger.Lazy<com.ziro.fit.data.remote.ZiroApi>
) {
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

    private val modePrefs = context.getSharedPreferences("mode_prefs", Context.MODE_PRIVATE)

    private val _logoutSignal = MutableSharedFlow<Unit>(replay = 0)
    val logoutSignal: SharedFlow<Unit> = _logoutSignal.asSharedFlow()

    private val _activeMode = MutableStateFlow(loadActiveMode())
    val activeMode: StateFlow<AppMode> = _activeMode.asStateFlow()

    private fun slotKey(mode: AppMode, suffix: String): String {
        return if (mode == AppMode.TRAINER) {
            "trainer_$suffix"
        } else {
            "ziroMe_$suffix"
        }
    }

    fun saveToken(token: String, mode: AppMode = _activeMode.value) {
        prefs.edit().putString(slotKey(mode, "access_token"), token).apply()
    }

    fun getToken(mode: AppMode = _activeMode.value): String? {
        return prefs.getString(slotKey(mode, "access_token"), null)
    }

    fun saveRefreshToken(token: String, mode: AppMode = _activeMode.value) {
        prefs.edit().putString(slotKey(mode, "refresh_token"), token).apply()
    }

    fun getRefreshToken(mode: AppMode = _activeMode.value): String? {
        return prefs.getString(slotKey(mode, "refresh_token"), null)
    }

    fun clearToken(mode: AppMode? = null) {
        val targetMode = mode ?: _activeMode.value
        prefs.edit()
            .remove(slotKey(targetMode, "access_token"))
            .remove(slotKey(targetMode, "refresh_token"))
            .apply()
    }

    fun clearAllTokens() {
        AppMode.entries.forEach { mode ->
            prefs.edit()
                .remove(slotKey(mode, "access_token"))
                .remove(slotKey(mode, "refresh_token"))
                .apply()
        }
    }

    fun hasToken(mode: AppMode = _activeMode.value): Boolean {
        return getToken(mode) != null
    }

    fun hasAnyToken(): Boolean {
        return AppMode.entries.any { hasToken(it) }
    }

    fun setActiveMode(mode: AppMode) {
        _activeMode.value = mode
        modePrefs.edit().putString("active_mode", mode.name).apply()
    }

    private fun loadActiveMode(): AppMode {
        val saved = modePrefs.getString("active_mode", AppMode.TRAINER.name)
        return try {
            AppMode.valueOf(saved ?: AppMode.TRAINER.name)
        } catch (e: Exception) {
            AppMode.TRAINER
        }
    }

    suspend fun refreshToken(mode: AppMode = _activeMode.value): Boolean {
        val refreshToken = getRefreshToken(mode) ?: return false
        return try {
            val response = api.get().refreshAccessToken(com.ziro.fit.model.RefreshTokenRequest(refreshToken))
            val data = response.data

            if (response.success != false && data != null) {
                saveToken(data.accessToken, mode)
                data.refreshToken?.let { saveRefreshToken(it, mode) }
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    suspend fun triggerLogout(mode: AppMode? = null) {
        val targetMode = mode ?: _activeMode.value
        clearToken(targetMode)

        if (mode == null) {
            _logoutSignal.emit(Unit)
        } else {
            if (mode == _activeMode.value) {
                val other = if (mode == AppMode.TRAINER) AppMode.PERSONAL else AppMode.TRAINER
                if (hasToken(other)) {
                    setActiveMode(other)
                } else {
                    _logoutSignal.emit(Unit)
                }
            }
        }
    }

    suspend fun triggerLogoutAll() {
        clearAllTokens()
        _logoutSignal.emit(Unit)
    }
}
