package io.aasmart.goalpost.goals.models

import kotlinx.serialization.Serializable

@Serializable
data class GoalReflection(
    val isCompleted: Boolean = false,
    val dateTimeMillis: Long,
    val progression: String = "",
    val mistakes: String = "",
    val ideasToImprove: String = "",
    val workedTowardsGoal: Int = 0,
)
