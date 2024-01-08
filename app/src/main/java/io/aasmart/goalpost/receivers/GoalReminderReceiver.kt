package io.aasmart.goalpost.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import io.aasmart.goalpost.goals.notifications.GoalEveningReminderNotification
import io.aasmart.goalpost.goals.notifications.GoalMidDayReminderNotification
import io.aasmart.goalpost.goals.notifications.GoalMorningReminderNotification
import io.aasmart.goalpost.goals.scheduleRemindersAlarm
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

enum class GoalReminderNotificationType(val stringId: String) {
    MORNING("morning"),
    MIDDAY("midday"),
    EVENING("evening"),
}

class GoalReminderReceiver : BroadcastReceiver() {
    @OptIn(DelicateCoroutinesApi::class)
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("Goal Reminder", "Reminder broadcast received")
        GlobalScope.launch {
            context?.let { scheduleRemindersAlarm(it) }
        }

        when (intent?.getStringExtra("type") ?: "") {
            GoalReminderNotificationType.MORNING.stringId ->
                context?.let { GoalMorningReminderNotification.pushNotification(it) }
            GoalReminderNotificationType.MIDDAY.stringId ->
                context?.let { GoalMidDayReminderNotification.pushNotification(it) }
            GoalReminderNotificationType.EVENING.stringId ->
                context?.let { GoalEveningReminderNotification.pushNotification(it) }
        }
    }
}