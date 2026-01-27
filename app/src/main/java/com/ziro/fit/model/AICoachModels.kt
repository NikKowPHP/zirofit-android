package com.ziro.fit.model

import com.google.gson.annotations.SerializedName

data class RefineGoalRequest(
    @SerializedName("user_input")
    val userInput: String
)

data class RefineGoalResponse(
    @SerializedName("suggestions")
    val suggestions: List<GoalSuggestion>
)

data class GoalSuggestion(
    @SerializedName("title")
    val title: String,
    
    @SerializedName("description")
    val description: String,
    
    @SerializedName("focus")
    val focus: String,

    @SerializedName("requiredMetrics")
    val requiredMetrics: List<String> = emptyList()
)

data class GenerateProgramFromGoalRequest(
    @SerializedName("clientId")
    val clientId: String,
    
    @SerializedName("selectedGoal")
    val selectedGoal: String,

    @SerializedName("focus")
    val focus: String,
    
    @SerializedName("metrics")
    val metrics: Map<String, Any>
)

data class GenerateProgramFromGoalResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("program_id")
    val programId: String
)
