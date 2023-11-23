package io.aasmart.goalpost.src.goals.models

data class Goal(
    val title: String,
    val description: String,
    val timePeriod: GoalTimePeriod
)
