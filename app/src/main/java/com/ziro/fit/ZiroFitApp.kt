package com.ziro.fit

import android.app.Application
import com.ziro.fit.util.HapticManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class ZiroFitApp : Application() {
    @Inject
    lateinit var hapticManager: HapticManager

    companion object {
        var globalHapticManager: HapticManager? = null
            private set
    }

    override fun onCreate() {
        super.onCreate()
        globalHapticManager = hapticManager
    }
}
