package com.ziro.fit.model

enum class FitnessGoalType {
    WEIGHT,
    WORKOUTS,
    VOLUME,
    PR
}

data class FitnessGoal(
    val id: String,
    val title: String,
    val targetValue: Double,
    val currentValue: Double,
    val unit: String,
    val type: FitnessGoalType
)
