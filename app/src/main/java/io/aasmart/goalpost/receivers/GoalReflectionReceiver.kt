package io.aasmart.goalpost.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import io.aasmart.goalpost.data.GoalStorage
import io.aasmart.goalpost.data.settingsDataStore
import io.aasmart.goalpost.goals.models.GoalReflection
import io.aasmart.goalpost.goals.notifications.SetGoalsNotification
import kotlinx.coroutines.flow.onEach
import java.time.Instant

class GoalReflectionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("Reflection Alarm Received", "Running reflection")
        context?.let {
            goalReflectionBroadcastHandler(context)
        }
    }
}

private fun goalReflectionBroadcastHandler(context: Context) = GoalStorage
    .getInstance(context)
    .getGoals()
    .onEach { goals ->
        val now = Instant.now()

        val reflectionGoals = goals.filter { goal ->
            true
        }

        // Add missing reflection to target goals
        reflectionGoals.forEach { goal ->
            GoalStorage.getInstance(context).setGoal(
                goal.apply { this.reflections.plus(
                    GoalReflection(
                        dateTimeMillis = now.toEpochMilli()
                    )
                ) }
            )
        }

        if(reflectionGoals.isNotEmpty()) {
            SetGoalsNotification.GoalReflectionReminderNotification()
            context.settingsDataStore.updateData {
                return@updateData it.toBuilder().setNeedsToReflect(true).build()
            }
            Log.d("Reflection Notification", "Sent reflection notification")
        }
    }