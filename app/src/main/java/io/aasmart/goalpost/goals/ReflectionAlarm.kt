package io.aasmart.goalpost.goals

import android.content.Context
import io.aasmart.goalpost.data.settingsDataStore
import io.aasmart.goalpost.receivers.GoalReflectionReceiver
import io.aasmart.goalpost.utils.AlarmHelper
import kotlinx.coroutines.flow.map
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

private const val DAY_MS = 24 * 60 * 60 * 1000L

suspend fun scheduleReflectionAlarm(
    context: Context
) {
    context.settingsDataStore
        .data
        .map { it.goalReflectionTimeMs }
        .collect {
            val targetTime = ZonedDateTime
                .now(ZoneId.systemDefault())
                .withSecond(0)
                .withMinute(0)
                .withHour(0)
                .plus(it, ChronoUnit.MILLIS)

            AlarmHelper.scheduleRepeatingAlarm(
                context,
                GoalReflectionReceiver::class.java,
                DAY_MS,
                targetTime.toInstant().toEpochMilli(),
                0
            )
        }
}

fun cancelReflectionAlarm(context: Context) {
    AlarmHelper.cancelAlarm(context, GoalReflectionReceiver::class.java, 0)
}
