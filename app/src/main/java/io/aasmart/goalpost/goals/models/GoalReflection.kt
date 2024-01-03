package io.aasmart.goalpost.goals.models

import kotlinx.serialization.Serializable

@Serializable
data class GoalReflection(
    val isCompleted: Boolean = false,
    val dateTimeMillis: Long,
    val madeProgress: Float? = null,
    val madeProgressReflection: String? = null,
    val couldDoBetter: Float? = null,
    val couldDoBetterReflection: String? = null,
    val stepsToImprove: String? = null,
)
