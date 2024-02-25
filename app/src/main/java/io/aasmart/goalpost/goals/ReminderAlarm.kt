package io.aasmart.goalpost.goals

import android.app.AlarmManager
import android.content.Context
import io.aasmart.goalpost.data.settingsDataStore
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
            it.reminderNotifTimesList.forEachIndexed { i, time ->
                if(i >= it.reminderEnabledCount || !it.reminderEnabledList[i])
                    return@forEachIndexed
                val reminderTime = GoalpostUtils.timeAsTodayDateTime(time)
                val now = Instant.now()
                AlarmHelper.scheduleInexactAlarm(
                    context = context,
                    type = AlarmManager.RTC_WAKEUP,
                    broadcastReceiverClass = GoalReminderReceiver::class.java,
                    initialTriggerMillis = (
                            if(now > reminderTime)
                                reminderTime.plusMillis(AlarmManager.INTERVAL_DAY)
                            else
                                reminderTime
                            ).toEpochMilli(),
                    requestCode = requestCode
                )
            }
        }
}

fun cancelRemindersAlarm(context: Context) {
    AlarmHelper.cancelAlarm(context, GoalReminderReceiver::class.java, requestCode)
}
