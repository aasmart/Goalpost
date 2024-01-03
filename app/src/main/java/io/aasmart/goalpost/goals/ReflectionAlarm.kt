package io.aasmart.goalpost.goals

import android.app.AlarmManager
import android.content.Context
import io.aasmart.goalpost.data.settingsDataStore
import io.aasmart.goalpost.receivers.GoalReflectionReceiver
import io.aasmart.goalpost.utils.AlarmHelper
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

private const val DAY_MS = 24 * 60 * 60 * 1000L

suspend fun scheduleReflectionAlarm(
    context: Context
) {
    context.settingsDataStore
        .data
        .collect {
            val targetTime = ZonedDateTime
                .now(ZoneId.systemDefault())
                .withSecond(0)
                .withMinute(0)
                .withHour(0)
                .plus(it.goalReflectionTimeMs, ChronoUnit.MILLIS)
                .withZoneSameLocal(ZoneId.of("UTC"))

            // Schedule an exact alarm if the reflection time has passed
            // Doesn't schedule an alarm if a reflection is active
            var targetMs = targetTime.toInstant().toEpochMilli()
            if(!it.needsToReflect
                && Instant.now().toEpochMilli() > targetMs
            ) {
                if(targetMs > it.lastCompletedReflection) {
                    AlarmHelper.scheduleInexactAlarm(
                        context,
                        GoalReflectionReceiver::class.java,
                        AlarmManager.RTC_WAKEUP,
                        0,
                        1
                    )
                }
                targetMs += 24*60*60*1000 - (Instant.now().toEpochMilli() - targetMs)
            }

            AlarmHelper.scheduleRepeatingAlarm(
                context,
                GoalReflectionReceiver::class.java,
                DAY_MS,
                targetMs,
                0
            )
        }
}

fun cancelReflectionAlarm(context: Context) {
    AlarmHelper.cancelAlarm(context, GoalReflectionReceiver::class.java, 0)
}
