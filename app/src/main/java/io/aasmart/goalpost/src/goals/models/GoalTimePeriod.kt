package io.aasmart.goalpost.src.goals.models

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class GoalTimePeriod(
    val name: String,
    val durationMs: Long
) {
    val id = UUID.randomUUID().toString()
}
