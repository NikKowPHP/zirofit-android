package com.ziro.fit.service

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ziro.fit.data.remote.ZiroApi
import com.ziro.fit.model.DailyTarget
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dailyTargetsDataStore: DataStore<Preferences> by preferencesDataStore(name = "daily_targets")

@Singleton
class DailyTargetManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val api: ZiroApi
) {
    private val lastSyncKey = longPreferencesKey("last_sync_timestamp")
    private val cachedTargetsKey = stringPreferencesKey("cached_targets")
    private val streakKey = intPreferencesKey("current_streak")

    val cachedTargets: Flow<List<DailyTarget>> = context.dailyTargetsDataStore.data.map { prefs ->
        val targetsJson = prefs[cachedTargetsKey] ?: "[]"
        parseTargetsFromJson(targetsJson)
    }

    val currentStreak: Flow<Int> = context.dailyTargetsDataStore.data.map { prefs ->
        prefs[streakKey] ?: 0
    }

    suspend fun fetchDailyTargets(): Result<List<DailyTarget>> {
        return try {
            val response = api.getDailyTargets()
            if (response.success == true && response.data != null) {
                val targets = response.data
                cacheTargets(targets)
                Result.success(targets)
            } else {
                Result.failure(Exception("Failed to fetch targets: ${response.message ?: response.error}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createDailyTarget(type: String, goal: Int, exerciseId: String?): Result<DailyTarget> {
        return try {
            val request = com.ziro.fit.model.CreateDailyTargetRequest(type, goal, exerciseId)
            val response = api.createDailyTarget(request)
            if (response.success == true && response.data != null) {
                val newTarget = response.data
                val currentTargets = cachedTargets.first().toMutableList()
                currentTargets.add(newTarget)
                cacheTargets(currentTargets)
                Result.success(newTarget)
            } else {
                Result.failure(Exception("Failed to create target: ${response.message ?: response.error}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateTargetProgress(targetId: String, newProgress: Int): Result<DailyTarget> {
        val targets = cachedTargets.first().toMutableList()
        val index = targets.indexOfFirst { it.id == targetId }
        
        if (index != -1) {
            val updatedTarget = targets[index].copy(
                current = newProgress,
                isCompleted = newProgress >= targets[index].goal
            )
            targets[index] = updatedTarget
            cacheTargets(targets)
            return Result.success(updatedTarget)
        }
        
        return Result.failure(Exception("Target not found"))
    }

    suspend fun checkAndUpdateStreak() {
        val targets = cachedTargets.first()
        val completedToday = targets.count { it.isCompleted }
        val totalTargets = targets.size
        
        if (totalTargets > 0 && completedToday.toFloat() / totalTargets >= 0.5f) {
            val currentStreak = context.dailyTargetsDataStore.data.first()[streakKey] ?: 0
            context.dailyTargetsDataStore.edit { prefs ->
                prefs[streakKey] = currentStreak + 1
            }
        }
    }

    fun isTargetMet(target: DailyTarget): Boolean {
        return target.current >= target.goal
    }

    fun getTargetProgress(target: DailyTarget): Float {
        return if (target.goal > 0) {
            (target.current.toFloat() / target.goal).coerceIn(0f, 1f)
        } else {
            0f
        }
    }

    private suspend fun cacheTargets(targets: List<DailyTarget>) {
        val targetsJson = targetsToJson(targets)
        context.dailyTargetsDataStore.edit { prefs ->
            prefs[cachedTargetsKey] = targetsJson
            prefs[lastSyncKey] = System.currentTimeMillis()
        }
    }

    suspend fun getLastSyncTimestamp(): Long {
        return context.dailyTargetsDataStore.data.first()[lastSyncKey] ?: 0
    }

    private fun targetsToJson(targets: List<DailyTarget>): String {
        return targets.joinToString(";") { target ->
            "${target.id}|${target.type}|${target.goal}|${target.current}|${target.exerciseId ?: ""}|${target.exerciseName ?: ""}|${target.isCompleted}|${target.date}"
        }
    }

    private fun parseTargetsFromJson(json: String): List<DailyTarget> {
        if (json == "[]" || json.isBlank()) return emptyList()
        
        return try {
            json.split(";").mapNotNull { entry ->
                val parts = entry.split("|")
                if (parts.size >= 8) {
                    DailyTarget(
                        id = parts[0],
                        type = parts[1],
                        goal = parts[2].toIntOrNull() ?: 0,
                        current = parts[3].toIntOrNull() ?: 0,
                        exerciseId = parts[4].ifBlank { null },
                        exerciseName = parts[5].ifBlank { null },
                        isCompleted = parts[6].toBoolean(),
                        date = parts[7]
                    )
                } else null
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
