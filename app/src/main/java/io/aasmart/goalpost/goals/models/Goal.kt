package io.aasmart.goalpost.goals.models

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Goal(
    val title: String,
    val description: String,
    val timePeriod: GoalTimePeriod,
    val beginDate: Long,
    /**
     * The time from epoch in milliseconds when the goal is scheduled to be completed
     */
    val completionDate: Long,
    val reflections: Map<Long, GoalReflection>
) {
    val id = UUID.randomUUID().toString()
}
