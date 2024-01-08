package io.aasmart.goalpost.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import io.aasmart.goalpost.data.GoalStorage
import io.aasmart.goalpost.data.settingsDataStore
import io.aasmart.goalpost.goals.notifications.GoalReflectionNotification
import io.aasmart.goalpost.goals.scheduleReflectionAlarm
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Instant

class GoalReflectionReceiver : BroadcastReceiver() {
    @OptIn(DelicateCoroutinesApi::class)
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("Reflection Alarm Received", "Running reflection")

        GlobalScope.launch {
            context?.let {
                goalReflectionBroadcastHandler(context)
                scheduleReflectionAlarm(context)
            }
        }
    }
}

private suspend fun goalReflectionBroadcastHandler(context: Context) {
    val goals = GoalStorage.getInstance(context)
        .getGoals()
        .cancellable()
        .first()

    val now = Instant.now()

    val reflectionGoals = goals.filter {
        val reflection = it.getCurrentReflection(now)
        return@filter reflection?.isCompleted == false
    }

    if(reflectionGoals.isNotEmpty()) {
        GoalReflectionNotification.pushNotification(context)
        // Indicate that the user needs to update their goals
        context.settingsDataStore.updateData {
            return@updateData it.toBuilder().setNeedsToReflect(true).build()
        }
        Log.d("Reflection Notification", "Sent reflection notification")
    }
}