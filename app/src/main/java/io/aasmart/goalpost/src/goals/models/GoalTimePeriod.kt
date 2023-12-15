package io.aasmart.goalpost.src.goals.models

import kotlinx.serialization.Serializable

@Serializable
data class GoalTimePeriod(
    val name: String,
    val durationMs: Long
)
