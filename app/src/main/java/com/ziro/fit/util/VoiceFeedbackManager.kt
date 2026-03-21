package com.ziro.fit.util

import android.content.Context
import android.os.Build
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Voice feedback manager using Android TextToSpeech (TTS) engine.
 *
 * Provides voice confirmation for workout logging and general status messages.
 * Mirrors iOS VoiceFeedbackManager functionality using Android's TTS.
 *
 * Features:
 * - Singleton instance with Hilt dependency injection
 * - Lazy initialization with proper lifecycle management
 * - Thread-safe operations (queues speech on main thread)
 * - Graceful handling of TTS initialization failures
 * - Configurable speech rate (0.9f for natural pace)
 * - US English locale for consistent pronunciation
 *
 * Usage:
 * ```
 * @Inject lateinit var voiceFeedbackManager: VoiceFeedbackManager
 *
 * // Speak workout confirmation
 * voiceFeedbackManager.speakConfirmation("Bench Press", 5, 50.0)
 * // Output: "Logged 5 reps of Bench Press at 50 kilos"
 *
 * // Speak without weight
 * voiceFeedbackManager.speakConfirmation("Squat", 10, null)
 * // Output: "Logged 10 reps of Squat"
 *
 * // General status message
 * voiceFeedbackManager.speakStatus("Workout complete!")
 * ```
 */
@Singleton
class VoiceFeedbackManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // Coroutine scope for main thread operations
    private val mainScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // Lazy initialization of TextToSpeech with proper error handling
    private val textToSpeech: TextToSpeech? by lazy {
        try {
            TextToSpeech(context) { status ->
                onTtsInitStatus(status)
            }
        } catch (e: Exception) {
            // TTS not available or initialization failed
            null
        }
    }

    // Flag to track TTS initialization success
    @Volatile
    private var isTtsReady: Boolean = false

    // Queue for pending speech when TTS is not yet ready
    private val speechQueue = mutableListOf<Pair<String, Boolean>>()
    private val queueLock = Any()

    init {
        // Configure TTS settings once initialized
        // Actual configuration happens in onTtsInitStatus callback
    }

    /**
     * Callback for TextToSpeech initialization.
     * Sets up language and speech rate when TTS is ready.
     *
     * @param status TextToSpeech.SUCCESS or TextToSpeech.ERROR
     */
    private fun onTtsInitStatus(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // Set language to US English
            val result = textToSpeech?.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Language not available, TTS unusable
                isTtsReady = false
                return
            }

            // Set speech rate: 0.9f (slightly slower than normal for clarity)
            textToSpeech?.setSpeechRate(0.9f)

            isTtsReady = true

            // Process any queued speech
            processSpeechQueue()
        } else {
            // Initialization failed
            isTtsReady = false
        }
    }

    /**
     * Speaks a workout confirmation message.
     *
     * Format: "Logged {reps} reps of {exercise}" or
     *         "Logged {reps} reps of {exercise} at {weight} kilos"
     *
     * @param exercise Name of the exercise (e.g., "Bench Press")
     * @param reps Number of repetitions
     * @param weight Weight in kilograms (optional, if null weight is omitted)
     *
     * Example:
     * ```
     * speakConfirmation("Bench Press", 5, 50.0)
     * // Says: "Logged 5 reps of Bench Press at 50 kilos"
     *
     * speakConfirmation("Squat", 10, null)
     * // Says: "Logged 10 reps of Squat"
     * ```
     */
    fun speakConfirmation(exercise: String?, reps: Int, weight: Double?) {
        if (exercise.isNullOrBlank()) {
            // No exercise specified, cannot form proper message
            return
        }

        val message = buildString {
            append("Logged $reps reps of $exercise")
            if (weight != null) {
                append(" at ${weight}kilos")
            }
        }

        speakStatus(message)
    }

    /**
     * Speaks a general status message.
     *
     * @param message The text to speak
     *
     * Example:
     * ```
     * speakStatus("Workout complete!")
     * speakStatus("Get ready for next set")
     * ```
     */
    fun speakStatus(message: String) {
        if (message.isBlank()) return

        mainScope.launch {
            synchronized(queueLock) {
                // Queue the message with isConfirmation flag (false for general status)
                speechQueue.add(message to false)
            }

            // Try to speak if TTS is ready
            if (isTtsReady) {
                processSpeechQueue()
            }
        }
    }

    /**
     * Processes the speech queue, speaking the next message if TTS is ready.
     * Called after TTS initialization and after each utterance completes.
     */
    private fun processSpeechQueue() {
        if (!isTtsReady) return

        val nextMessage: String?
        synchronized(queueLock) {
            if (speechQueue.isNotEmpty()) {
                nextMessage = speechQueue.removeAt(0).first
            } else {
                nextMessage = null
            }
        }

        nextMessage?.let { message ->
            // Set utterance progress listener to know when speech completes
            textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    // Speech started
                }

                override fun onDone(utteranceId: String?) {
                    // Speech completed, process next in queue
                    mainScope.launch {
                        processSpeechQueue()
                    }
                }

                override fun onError(utteranceId: String?) {
                    // Error occurred, try next message
                    mainScope.launch {
                        processSpeechQueue()
                    }
                }

                // API 26+ override
                override fun onError(utteranceId: String?, errorCode: Int) {
                    onError(utteranceId)
                }
            })

            // Speak with unique utterance ID
            val utteranceId = "UTTERANCE_${System.currentTimeMillis()}"
            textToSpeech?.speak(message, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        }
    }

    /**
     * Shuts down the TextToSpeech engine and releases resources.
     *
     * Should be called when the application is terminating or when voice feedback is no longer needed.
     * After calling shutdown(), this manager becomes unusable.
     *
     * Example:
     * ```
     * // In Application.onTerminate() or Activity.onDestroy()
     * voiceFeedbackManager.shutdown()
     * ```
     */
    fun shutdown() {
        mainScope.launch {
            synchronized(queueLock) {
                speechQueue.clear()
            }
            textToSpeech?.stop()
            textToSpeech?.shutdown()
            isTtsReady = false
        }
    }

    /**
     * Checks if the TTS engine is available and initialized.
     *
     * @return true if TTS is ready to speak, false otherwise
     */
    fun isAvailable(): Boolean = isTtsReady
}
