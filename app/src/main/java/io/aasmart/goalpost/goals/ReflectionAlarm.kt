package io.aasmart.goalpost.goals

import android.app.AlarmManager
import android.content.Context
import io.aasmart.goalpost.data.settingsDataStore
import io.aasmart.goalpost.receivers.GoalReflectionReceiver
import io.aasmart.goalpost.utils.AlarmHelper
import io.aasmart.goalpost.utils.GoalpostUtils
import io.aasmart.goalpost.utils.GoalpostUtils.DAY_MS
import java.time.Instant

private const val requestCode = 10

suspend fun scheduleReflectionAlarm(
    context: Context
) {
    context.settingsDataStore
        .data
        .collect {
            val reflectionInstant = GoalpostUtils.timeAsTodayDateTime(it.goalReflectionTimeMs)
            val targetMs = reflectionInstant.toEpochMilli()

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
