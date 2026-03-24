package com.ziro.fit.util

import android.util.Log
import com.ziro.fit.BuildConfig
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Custom Logger class based on best practices
 * 
 * Features:
 * - BuildConfig-based log level control
 * - Thread and class name context
 * - Formatted output for readability
 * - Optional file logging capability
 */
object Logger {
    
    // Log level enum
    enum class Level {
        VERBOSE, DEBUG, INFO, WARN, ERROR
    }
    
    // Configure default log level based on build
   var logLevel: Level = Level.DEBUG
    
    // Optional: Enable file logging for production issues tracking
    var enableFileLogging: Boolean = false
    
    // Date format for timestamps
    private val dateFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.US)
    
    // Get stack trace element for caller info
    private fun getCallerInfo(): String {
        val stackTrace = Thread.currentThread().stackTrace
        // stackTrace[0] is getStackTrace, stackTrace[5] is typically the caller
        val caller = stackTrace.getOrNull(5) ?: return "Unknown"
        val fileName = caller.fileName ?: "Unknown"
        val lineNumber = caller.lineNumber
        val methodName = caller.methodName
        
        // Extract simple class name from full path
        val className = caller.className.substringAfterLast('.')
        
        return "$className.$methodName($fileName:$lineNumber)"
    }
    
    private fun formatMessage(tag: String, message: String): String {
        val timestamp = dateFormat.format(Date())
        val threadName = Thread.currentThread().name
        val callerInfo = getCallerInfo()
        
        return "[$timestamp] [$threadName] $callerInfo | $tag: $message"
    }
    
    fun v(tag: String, message: String) {
        if (logLevel.ordinal <= Level.VERBOSE.ordinal) {
            Log.v(tag, formatMessage(tag, message))
        }
    }
    
    fun d(tag: String, message: String) {
        if (logLevel.ordinal <= Level.DEBUG.ordinal) {
            Log.d(tag, formatMessage(tag, message))
        }
    }
    
    fun i(tag: String, message: String) {
        if (logLevel.ordinal <= Level.INFO.ordinal) {
            Log.i(tag, formatMessage(tag, message))
        }
    }
    
    fun w(tag: String, message: String) {
        if (logLevel.ordinal <= Level.WARN.ordinal) {
            Log.w(tag, formatMessage(tag, message))
        }
    }
    
    fun e(tag: String, message: String, throwable: Throwable? = null) {
        if (logLevel.ordinal <= Level.ERROR.ordinal) {
            if (throwable != null) {
                Log.e(tag, formatMessage(tag, message), throwable)
            } else {
                Log.e(tag, formatMessage(tag, message))
            }
        }
    }
    
    // Convenience method with lazy message evaluation
    fun v(tag: String, message: () -> String) {
        if (logLevel.ordinal <= Level.VERBOSE.ordinal) {
            Log.v(tag, formatMessage(tag, message()))
        }
    }
    
    fun d(tag: String, message: () -> String) {
        if (logLevel.ordinal <= Level.DEBUG.ordinal) {
            Log.d(tag, formatMessage(tag, message()))
        }
    }
    
    fun i(tag: String, message: () -> String) {
        if (logLevel.ordinal <= Level.INFO.ordinal) {
            Log.i(tag, formatMessage(tag, message()))
        }
    }
    
    fun w(tag: String, message: () -> String) {
        if (logLevel.ordinal <= Level.WARN.ordinal) {
            Log.w(tag, formatMessage(tag, message()))
        }
    }
    
    fun e(tag: String, message: () -> String, throwable: Throwable? = null) {
        if (logLevel.ordinal <= Level.ERROR.ordinal) {
            val formattedMessage = formatMessage(tag, message())
            if (throwable != null) {
                Log.e(tag, formattedMessage, throwable)
            } else {
                Log.e(tag, formattedMessage)
            }
        }
    }
}
