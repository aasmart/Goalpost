package io.aasmart.goalpost.goals.models

import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
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
        const val NAME_MIN_LENGTH = 1
        const val NAME_MAX_LENGTH = 32
        const val DESCRIPTION_MIN_LENGTH = 1

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
            return Goal(
                title = title,
                description = description,
                timePeriod = timePeriod,
                beginDate = beginDate,
                completionDate = completionDate,
                reflections = createReflectionsFromDate(
                    beginDate = beginDate,
                    timePeriod = timePeriod,
                    completionDate = completionDate
                ).toList()
            )
        }

        fun createReflectionsFromDate(
            beginDate: Long,
            timePeriod: GoalInterval,
            completionDate: Long
        ): List<GoalReflection> {
            val tempReflections: MutableList<GoalReflection> = mutableListOf()
            if(timePeriod.intervalMillis > 0) {
                var reflectionDate = ZonedDateTime
                    .ofInstant(Instant.ofEpochMilli(beginDate), ZoneId.of("UTC"))

                // Add all reflections up until the completion date
                while (reflectionDate.toInstant().toEpochMilli() < completionDate) {
                    val ms = reflectionDate.toInstant().toEpochMilli()
                    tempReflections += GoalReflection(dateTimeMillis = ms)
                    reflectionDate = reflectionDate.plus(timePeriod.intervalMillis, ChronoUnit.MILLIS)
                }

                // Add reflection for final day regardless of the reflection interval
                tempReflections.add(
                    GoalReflection(dateTimeMillis = completionDate)
                )
            }

            return tempReflections
        }
    }

    /**
     * Gets the Goal's current reflection based on the given day in local time.
     * Will also return goals that are marked as completed
     */
    fun getCurrentReflection(day: Instant): GoalReflection? {
        val currentZonedDateTime = day
            .atZone(ZoneId.systemDefault())
            .truncatedTo(ChronoUnit.DAYS)

        return reflections.filter {
            val goalZonedDateTime = Instant
                .ofEpochMilli(it.dateTimeMillis)
                .atZone(ZoneId.systemDefault())
                .truncatedTo(ChronoUnit.DAYS)

            currentZonedDateTime >= goalZonedDateTime
        }.maxByOrNull {
            it.dateTimeMillis
        }
    }
}
