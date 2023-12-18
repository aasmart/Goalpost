package io.aasmart.goalpost.src.goals.models

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Goal(
    val title: String,
    val description: String,
    val timePeriod: GoalTimePeriod,
    /**
     * The time from epoch in milliseconds when the goal is scheduled to be completed
     */
    val completionDate: Long
) {
    val id = UUID.randomUUID().toString()
}
