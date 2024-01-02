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
    val timePeriod: GoalInterval,
    val beginDate: Long,
    /**
     * The time from epoch in milliseconds when the goal is scheduled to be completed
     */
    val completionDate: Long,
    val reflections: List<GoalReflection>,
    val id: String = UUID.randomUUID().toString()
) {

    companion object {
        /**
         * Creates a goal with the reflections automatically generated based on
         * the time period, the begin date, and the end date
         */
        fun createGoalWithReflections(
            title: String,
            description: String,
            timePeriod: GoalInterval,
            beginDate: Long,
            completionDate: Long,
        ): Goal {
            val tempReflections: MutableList<GoalReflection> = mutableListOf()
            if(timePeriod.intervalMillis > 0) {
                var reflectionDate = ZonedDateTime
                    .ofInstant(Instant.ofEpochMilli(beginDate), ZoneId.of("UTC"))
                    .with(ChronoField.MILLI_OF_DAY, 0)

                while (reflectionDate.toInstant().toEpochMilli() < completionDate) {
                    val ms = reflectionDate.toInstant().toEpochMilli()
                    tempReflections += GoalReflection(dateTimeMillis = ms)
                    reflectionDate = reflectionDate.plus(timePeriod.intervalMillis, ChronoUnit.MILLIS)
                }
            }

            return Goal(
                title = title,
                description = description,
                timePeriod = timePeriod,
                beginDate = beginDate,
                completionDate = completionDate,
                reflections = tempReflections.toList()
            )
        }
    }

    /**
     * Gets the Goal's current reflection based on the given day.
     * Will also return goals that are marked as completed
     */
    fun getCurrentReflection(day: Instant): GoalReflection? {
        val ms = day.truncatedTo(ChronoUnit.DAYS).toEpochMilli()

        return reflections.filter {
            ms >= it.dateTimeMillis
        }.maxByOrNull {
            it.dateTimeMillis
        }
    }
}
