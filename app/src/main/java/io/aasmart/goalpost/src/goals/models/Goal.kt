package io.aasmart.goalpost.src.goals.models

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Goal(
    val title: String,
    val description: String,
    val timePeriod: GoalTimePeriod
) {
    val id = UUID.randomUUID().toString()
}
