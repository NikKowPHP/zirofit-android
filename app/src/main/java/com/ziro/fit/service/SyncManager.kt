package com.ziro.fit.service

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ziro.fit.data.remote.ZiroApi
import com.ziro.fit.model.*
import com.ziro.fit.util.ApiErrorParser
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val api: ZiroApi
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("sync_prefs", Context.MODE_PRIVATE)
    private val _isOnline = MutableStateFlow(true)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()
    private val gson = Gson()

    private var pendingActions = mutableListOf<SyncAction>()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        loadQueue()
        startMonitoring()
    }

    private fun startMonitoring() {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        // Check initial state
        val activeNetwork = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        _isOnline.value = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true

        connectivityManager.registerNetworkCallback(networkRequest, object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                _isOnline.value = true
                Log.d("SyncManager", "Network available. Online: true")
                processQueue()
            }

            override fun onLost(network: Network) {
                _isOnline.value = false
                Log.d("SyncManager", "Network lost. Online: false")
            }
        })
    }

    fun enqueue(type: SyncActionType, payload: Any) {
        scope.launch {
            val payloadJson = gson.toJson(payload)
            val action = SyncAction(
                id = UUID.randomUUID().toString(),
                type = type,
                payload = payloadJson,
                createdAt = System.currentTimeMillis()
            )

            pendingActions.add(action)
            saveQueue()

            if (_isOnline.value) {
                processQueue()
            }
        }
    }

    private fun saveQueue() {
        val json = gson.toJson(pendingActions)
        prefs.edit().putString("offline_sync_queue", json).apply()
    }

    private fun loadQueue() {
        val json = prefs.getString("offline_sync_queue", null)
        if (json != null) {
            val type = object : TypeToken<List<SyncAction>>() {}.type
            val actions: List<SyncAction> = gson.fromJson(json, type)
            pendingActions.addAll(actions)
        }
    }

    private fun processQueue() {
        if (!_isOnline.value || pendingActions.isEmpty()) return

        Log.d("SyncManager", "Processing queue of ${pendingActions.size} items")

        scope.launch {
            val actions = pendingActions.toList() // Copy to iterate safely
            for (action in actions) {
                val success = performAction(action)
                if (success) {
                    pendingActions.removeAll { it.id == action.id }
                    saveQueue()
                } else {
                    // Stop processing if one fails to preserve order dependency (e.g. logging a set for a deleted session)
                    Log.e("SyncManager", "Failed to process action ${action.id}. Stopping sync.")
                    break
                }
            }
        }
    }

    private suspend fun performAction(action: SyncAction): Boolean {
        return try {
            when (action.type) {
                SyncActionType.LOG_SET -> {
                    val data = gson.fromJson(action.payload, LogSetPayload::class.java)
                    val request = LogSetRequest(
                        workoutSessionId = data.workoutSessionId,
                        exerciseId = data.exerciseId,
                        reps = data.reps,
                        weight = data.weight,
                        order = data.order,
                        isCompleted = data.isCompleted,
                        logId = data.logId,
                        rpe = data.rpe
                    )
                    api.logSet(request)
                    true
                }
                SyncActionType.FINISH_WORKOUT -> {
                    val data = gson.fromJson(action.payload, FinishWorkoutPayload::class.java)
                    val request = FinishWorkoutRequest(
                        workoutSessionId = data.sessionId,
                        notes = data.notes
                    )
                    api.finishWorkout(request)
                    true
                }
            }
        } catch (e: Exception) {
            val apiError = ApiErrorParser.parse(e)
            // Self-healing: if 404 Not Found or session doesn't exist, drop the action to prevent blocking the queue.
            if (apiError.statusCode == 404 || apiError.message.contains("session_not_found") || apiError.message.contains("Session not found")) {
                Log.e("SyncManager", "Action ${action.type} failed with 404. Discarding item.")
                return true 
            }
            Log.e("SyncManager", "Error performing action: $e")
            false
        }
    }
}
