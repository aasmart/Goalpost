package io.aasmart.goalpost.goals

import android.app.AlarmManager
import android.content.Context
import io.aasmart.goalpost.data.settingsDataStore
import io.aasmart.goalpost.receivers.GoalReflectionReceiver
import io.aasmart.goalpost.utils.AlarmHelper
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit

private const val DAY_MS = 24 * 60 * 60 * 1000L
private const val requestCode = 10

suspend fun scheduleReflectionAlarm(
    context: Context
) {
    context.settingsDataStore
        .data
        .collect {
            val targetTime = ZonedDateTime
                .now(ZoneId.systemDefault())
                .with(ChronoField.MILLI_OF_DAY, 0)
                .plus(it.goalReflectionTimeMs, ChronoUnit.MILLIS)
            val targetMs = targetTime.toInstant().toEpochMilli()

            if(Instant.now().toEpochMilli() < targetMs) {
                AlarmHelper.scheduleInexactAlarm(
                    context,
                    GoalReflectionReceiver::class.java,
                    initialTriggerMillis = targetMs,
                    type = AlarmManager.RTC_WAKEUP,
                    requestCode = requestCode
                )
            } else {
                AlarmHelper.scheduleInexactAlarm(
                    context,
                    GoalReflectionReceiver::class.java,
                    initialTriggerMillis = targetMs + DAY_MS,
                    type = AlarmManager.RTC_WAKEUP,
                    requestCode = requestCode
                )
            }
        }
}

fun cancelReflectionAlarm(context: Context) {
    AlarmHelper.cancelAlarm(context, GoalReflectionReceiver::class.java, requestCode + 1)
    AlarmHelper.cancelAlarm(context, GoalReflectionReceiver::class.java, requestCode)
}
