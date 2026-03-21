package com.ziro.fit.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Global haptic feedback manager mirroring iOS HapticManager.
 * Provides tactile feedback for button presses, set completion, and errors.
 *
 * iOS equivalents:
 *   soft      → SOFT
 *   light     → LIGHT
 *   medium    → MEDIUM
 *   heavy     → HEAVY
 *   rigid     → RIGID
 *   success   → SUCCESS
 *   warning   → WARNING
 *   error     → ERROR
 *   selection → SELECTION
 */
@Singleton
class HapticManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val vibrator: Vibrator by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            manager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    /**
     * Short impact feedback — equivalent to iOS UIImpactFeedbackGenerator(.soft/.light/.medium/.heavy/.rigid)
     */
    fun impact(style: HapticStyle) {
        if (!vibrator.hasVibrator()) return

        val effect = when (style) {
            HapticStyle.SOFT -> VibrationEffect.createOneShot(10, 40)       // ~soft
            HapticStyle.LIGHT -> VibrationEffect.createOneShot(15, 60)       // ~light
            HapticStyle.MEDIUM -> VibrationEffect.createOneShot(20, 120)     // ~medium
            HapticStyle.HEAVY -> VibrationEffect.createOneShot(30, 200)      // ~heavy
            HapticStyle.RIGID -> VibrationEffect.createOneShot(25, 255)     // ~rigid
        }
        vibrator.vibrate(effect)
    }

    /**
     * Notification-style feedback — equivalent to iOS UINotificationFeedbackGenerator
     */
    fun notification(type: HapticNotification) {
        if (!vibrator.hasVibrator()) return

        val effect = when (type) {
            HapticNotification.SUCCESS -> VibrationEffect.createOneShot(40, 150)
            HapticNotification.WARNING -> VibrationEffect.createWaveform(longArrayOf(0, 50, 50, 50), intArrayOf(0, 180, 0, 180), -1)
            HapticNotification.ERROR -> VibrationEffect.createWaveform(longArrayOf(0, 30, 60, 30, 60, 30), intArrayOf(0, 200, 0, 200, 0, 200), -1)
        }
        vibrator.vibrate(effect)
    }

    /**
     * Selection tick — equivalent to iOS UISelectionFeedbackGenerator
     */
    fun selection() {
        if (!vibrator.hasVibrator()) return
        vibrator.vibrate(VibrationEffect.createOneShot(5, 30))
    }

    /**
     * Success haptic pattern — double pulse vibration.
     *
     * Pattern: silence(0ms) → pulse(50ms, max) → silence(100ms) → pulse(50ms, max)
     * Creates a distinct "tick-tick" feel to indicate successful completion.
     *
     * Fallback: Falls back to [notification][HapticNotification.SUCCESS] on API < 26
     *           since [VibrationEffect.createWaveform] requires API 26+.
     */
    fun success() {
        if (!vibrator.hasVibrator()) return

        // Cancel any ongoing vibration to ensure clean pattern start
        vibrator.cancel()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Timings: delay, vibrate, pause, vibrate
            // Amplitudes: silence, max intensity, silence, max intensity
            val timings = longArrayOf(0, 50, 100, 50)
            val amplitudes = intArrayOf(0, 255, 0, 255)
            val effect = VibrationEffect.createWaveform(timings, amplitudes, -1)
            vibrator.vibrate(effect)
        } else {
            // Fallback for older APIs: single longer pulse
            notification(HapticNotification.SUCCESS)
        }
    }

    /**
     * Error haptic pattern — triple heavy tap vibration.
     *
     * Pattern: silence(0ms) → heavy(200ms) → pause(100ms) → heavy(200ms) → pause(100ms) → heavy(200ms)
     * Creates a distinct "buzz-buzz-buzz" error feel to indicate failure or error.
     *
     * Fallback: Falls back to [notification][HapticNotification.ERROR] on API < 26
     *           since [VibrationEffect.createWaveform] requires API 26+.
     */
    fun error() {
        if (!vibrator.hasVibrator()) return

        // Cancel any ongoing vibration to ensure clean pattern start
        vibrator.cancel()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Timings: delay, vibrate, pause, vibrate, pause, vibrate
            // Amplitudes: silence, max intensity for each pulse
            val timings = longArrayOf(0, 200, 100, 200, 100, 200)
            val amplitudes = intArrayOf(0, 255, 0, 255, 0, 255)
            val effect = VibrationEffect.createWaveform(timings, amplitudes, -1)
            vibrator.vibrate(effect)
        } else {
            // Fallback for older APIs: existing error notification pattern
            notification(HapticNotification.ERROR)
        }
    }
}

enum class HapticStyle {
    SOFT,
    LIGHT,
    MEDIUM,
    HEAVY,
    RIGID
}

enum class HapticNotification {
    SUCCESS,
    WARNING,
    ERROR
}
