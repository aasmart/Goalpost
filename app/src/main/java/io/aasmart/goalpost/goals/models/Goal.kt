package io.aasmart.goalpost.goals.models

import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
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
    val completionDate: Long
) {
    val id = UUID.randomUUID().toString()
    val reflections: List<GoalReflection>

    init {
        val tempReflections: MutableList<GoalReflection> = mutableListOf()
        if(timePeriod.durationMs > 0) {
            var reflectionDate = ZonedDateTime
                .ofInstant(Instant.ofEpochMilli(beginDate), ZoneId.of("UTC"))
                .with(ChronoField.MILLI_OF_DAY, 0)

            while (reflectionDate.toInstant().toEpochMilli() < completionDate) {
                val ms = reflectionDate.toInstant().toEpochMilli()
                tempReflections += GoalReflection(dateTimeMillis = ms)
                reflectionDate = reflectionDate.plus(timePeriod.durationMs, ChronoUnit.MILLIS)
            }
        }

        reflections = tempReflections.toList()
    }

    fun getCurrentReflection(day: Instant): GoalReflection? {
        val ms = day.truncatedTo(ChronoUnit.DAYS).toEpochMilli()

        return reflections.filter {
            !it.isCompleted && ms >= it.dateTimeMillis
        }.maxByOrNull {
            it.dateTimeMillis
        }
    }
}
