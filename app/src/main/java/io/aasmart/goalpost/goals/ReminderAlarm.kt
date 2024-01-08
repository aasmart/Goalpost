package io.aasmart.goalpost.goals

import android.app.AlarmManager
import android.content.Context
import io.aasmart.goalpost.data.settingsDataStore
import io.aasmart.goalpost.receivers.GoalReminderNotificationType
import io.aasmart.goalpost.receivers.GoalReminderReceiver
import io.aasmart.goalpost.utils.AlarmHelper
import io.aasmart.goalpost.utils.GoalpostUtils
import java.time.Instant

private const val requestCode = 12

suspend fun scheduleRemindersAlarm(
    context: Context
) {
    context.settingsDataStore
        .data
        .collect {
            val morning = GoalpostUtils.timeAsTodayDateTime(it.morningReminderTimeMs)
            val midday = GoalpostUtils.timeAsTodayDateTime(it.midDayReminderTimeMs)
            val evening = GoalpostUtils.timeAsTodayDateTime(it.eveningReminderTimeMs)

            val now = Instant.now()
            AlarmHelper.scheduleInexactAlarm(
                context = context,
                type = AlarmManager.RTC_WAKEUP,
                broadcastReceiverClass = GoalReminderReceiver::class.java,
                initialTriggerMillis = (
                    if(now > morning)
                        morning.plusMillis(AlarmManager.INTERVAL_DAY)
                    else
                        morning
                ).toEpochMilli(),
                requestCode = requestCode,
                extras = mapOf("type" to GoalReminderNotificationType.MORNING.stringId)
            )

            AlarmHelper.scheduleInexactAlarm(
                context = context,
                type = AlarmManager.RTC_WAKEUP,
                broadcastReceiverClass = GoalReminderReceiver::class.java,
                initialTriggerMillis = (
                        if(now > midday)
                            midday.plusMillis(AlarmManager.INTERVAL_DAY)
                        else
                            midday
                        ).toEpochMilli(),
                requestCode = requestCode + 1,
                extras = mapOf("type" to GoalReminderNotificationType.MIDDAY.stringId)
            )

            AlarmHelper.scheduleInexactAlarm(
                context = context,
                type = AlarmManager.RTC_WAKEUP,
                broadcastReceiverClass = GoalReminderReceiver::class.java,
                initialTriggerMillis = (
                        if(now > evening)
                            midday.plusMillis(AlarmManager.INTERVAL_DAY)
                        else
                            evening
                        ).toEpochMilli(),
                requestCode = requestCode + 2,
                extras = mapOf("type" to GoalReminderNotificationType.EVENING.stringId)
            )
        }
}

fun cancelRemindersAlarm(context: Context) {
    AlarmHelper.cancelAlarm(context, GoalReminderReceiver::class.java, requestCode)
    AlarmHelper.cancelAlarm(context, GoalReminderReceiver::class.java, requestCode + 1)
    AlarmHelper.cancelAlarm(context, GoalReminderReceiver::class.java, requestCode + 2)
}
