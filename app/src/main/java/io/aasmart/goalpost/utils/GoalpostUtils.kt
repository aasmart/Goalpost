package io.aasmart.goalpost.utils

import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit

object GoalpostUtils {
    const val DAY_MS = 24 * 60 * 60 * 1000L

    fun timeAsTodayDateTime(time: Long): Instant =
        Instant.now()
            .atZone(ZoneId.systemDefault())
            .with(ChronoField.MILLI_OF_DAY, 0)
            .plus(time, ChronoUnit.MILLIS)
            .toInstant()
}