package com.ziro.fit.model

data class DaySchedule(
    val day: String,
    val isOpen: Boolean,
    val startTime: String?,
    val endTime: String?
)

data class WorkingHours(
    val days: List<DaySchedule>
)
