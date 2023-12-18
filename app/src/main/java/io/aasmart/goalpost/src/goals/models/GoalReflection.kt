package io.aasmart.goalpost.src.goals.models

import kotlinx.serialization.Serializable

@Serializable
data class GoalReflection(
    val dateTimeMillis: Long,
    val progression: String,
    val mistakes: String,
    val ideasToImprove: String
)
