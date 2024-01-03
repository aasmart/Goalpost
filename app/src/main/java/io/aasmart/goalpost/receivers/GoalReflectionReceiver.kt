package io.aasmart.goalpost.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import io.aasmart.goalpost.data.GoalStorage
import io.aasmart.goalpost.data.settingsDataStore
import io.aasmart.goalpost.goals.notifications.GoalReflectionNotification
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.time.Instant

class GoalReflectionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?): Unit = runBlocking {
        Log.d("Reflection Alarm Received", "Running reflection")
        context?.let {
            goalReflectionBroadcastHandler(context)
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