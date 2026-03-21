package com.ziro.fit.service

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ziro.fit.model.AnalyticsWidget
import com.ziro.fit.model.AnalyticsWidgetType
import com.ziro.fit.model.FitnessGoal
import com.ziro.fit.model.FitnessGoalType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.widgetStateDataStore: DataStore<Preferences> by preferencesDataStore(name = "widget_state")

@Singleton
class WidgetStateManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val widgetsKey = stringPreferencesKey("analytics_widgets")
    private val fitnessGoalKey = stringPreferencesKey("fitness_goal")

    val widgets: Flow<List<AnalyticsWidget>> = context.widgetStateDataStore.data.map { prefs ->
        val widgetsJson = prefs[widgetsKey]
        if (widgetsJson.isNullOrBlank()) {
            getDefaultWidgets()
        } else {
            parseWidgetsFromJson(widgetsJson)
        }
    }

    val fitnessGoal: Flow<FitnessGoal?> = context.widgetStateDataStore.data.map { prefs ->
        val goalJson = prefs[fitnessGoalKey]
        if (goalJson.isNullOrBlank()) {
            null
        } else {
            parseGoalFromJson(goalJson)
        }
    }

    suspend fun saveWidgets(widgets: List<AnalyticsWidget>) {
        context.widgetStateDataStore.edit { prefs ->
            prefs[widgetsKey] = widgetsToJson(widgets)
        }
    }

    suspend fun saveFitnessGoal(goal: FitnessGoal) {
        context.widgetStateDataStore.edit { prefs ->
            prefs[fitnessGoalKey] = goalToJson(goal)
        }
    }

    suspend fun clearFitnessGoal() {
        context.widgetStateDataStore.edit { prefs ->
            prefs.remove(fitnessGoalKey)
        }
    }

    private fun getDefaultWidgets(): List<AnalyticsWidget> = listOf(
        AnalyticsWidget("1", AnalyticsWidgetType.WORKOUTS_PER_WEEK, true, 0),
        AnalyticsWidget("2", AnalyticsWidgetType.CONSISTENCY, true, 1),
        AnalyticsWidget("3", AnalyticsWidgetType.VOLUME_PROGRESSION, true, 2),
        AnalyticsWidget("4", AnalyticsWidgetType.MUSCLE_FOCUS, true, 3),
        AnalyticsWidget("5", AnalyticsWidgetType.PRS, true, 4),
        AnalyticsWidget("6", AnalyticsWidgetType.HEAT_MAP, true, 5),
        AnalyticsWidget("7", AnalyticsWidgetType.INSIGHTS, true, 6)
    )

    private fun widgetsToJson(widgets: List<AnalyticsWidget>): String {
        return widgets.joinToString(";") { w ->
            "${w.id}|${w.type.name}|${w.isVisible}|${w.order}"
        }
    }

    private fun parseWidgetsFromJson(json: String): List<AnalyticsWidget> {
        if (json.isBlank()) return getDefaultWidgets()
        return try {
            json.split(";").mapNotNull { entry ->
                val parts = entry.split("|")
                if (parts.size >= 4) {
                    val type = try {
                        AnalyticsWidgetType.valueOf(parts[1])
                    } catch (e: Exception) {
                        return@mapNotNull null
                    }
                    AnalyticsWidget(
                        id = parts[0],
                        type = type,
                        isVisible = parts[2].toBoolean(),
                        order = parts[3].toIntOrNull() ?: 0
                    )
                } else null
            }
        } catch (e: Exception) {
            getDefaultWidgets()
        }
    }

    private fun goalToJson(goal: FitnessGoal): String {
        return "${goal.id}|${goal.title}|${goal.targetValue}|${goal.currentValue}|${goal.unit}|${goal.type.name}"
    }

    private fun parseGoalFromJson(json: String): FitnessGoal? {
        return try {
            val parts = json.split("|")
            if (parts.size >= 6) {
                FitnessGoal(
                    id = parts[0],
                    title = parts[1],
                    targetValue = parts[2].toDoubleOrNull() ?: 0.0,
                    currentValue = parts[3].toDoubleOrNull() ?: 0.0,
                    unit = parts[4],
                    type = try {
                        FitnessGoalType.valueOf(parts[5])
                    } catch (e: Exception) {
                        FitnessGoalType.WEIGHT
                    }
                )
            } else null
        } catch (e: Exception) {
            null
        }
    }
}


