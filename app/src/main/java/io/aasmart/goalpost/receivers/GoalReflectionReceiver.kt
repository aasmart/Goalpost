package io.aasmart.goalpost.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import io.aasmart.goalpost.data.GoalStorage
import io.aasmart.goalpost.data.settingsDataStore
import io.aasmart.goalpost.goals.notifications.GoalReflectionNotification
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.Instant

class GoalReflectionReceiver : BroadcastReceiver() {
    @OptIn(DelicateCoroutinesApi::class)
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("Reflection Alarm Received", "Running reflection")
        context?.let {
            GlobalScope.launch {
                goalReflectionBroadcastHandler(context)
            }
        }
    }
}

private suspend fun goalReflectionBroadcastHandler(context: Context) = GoalStorage
    .getInstance(context)
    .getGoals()
    .collect { goals ->
        val now = Instant.now()

        val reflectionGoals = goals.filter {
            it.getCurrentReflection(now) != null
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