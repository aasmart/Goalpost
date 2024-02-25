package io.aasmart.goalpost.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import io.aasmart.goalpost.goals.notifications.GoalReminderNotification
import io.aasmart.goalpost.goals.scheduleRemindersAlarm
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class GoalReminderReceiver : BroadcastReceiver() {
    @OptIn(DelicateCoroutinesApi::class)
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("Goal Reminder", "Reminder broadcast received")
        GlobalScope.launch {
            context?.let { scheduleRemindersAlarm(it) }
        }
        context?.let { GoalReminderNotification.pushNotification(it) }
    }
}