package io.aasmart.goalpost.goals.notifications

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import io.aasmart.goalpost.MainActivity
import io.aasmart.goalpost.R
import kotlin.random.Random

object GoalReflectionNotification : GoalpostNotification() {
    private const val ACTION_SNOOZE = "SNOOZE"
    private const val ACTION_SET_GOAL = "SET_GOAL"

    class GoalReflectionReminderNotification : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.println(Log.ERROR, "ee", "HELLO WORLD")
            if(intent?.action == ACTION_SNOOZE)
                context?.let { snooze(it) }
            else if(intent?.action == ACTION_SET_GOAL)
                TODO()
        }

        private fun snooze(context: Context) {

        }
    }

    override fun pushNotification(context: Context) {
        val fullscreenIntent = Intent(context, MainActivity::class.java)
        val fullscreenPendingIntent = PendingIntent.getActivity(
            context,
            0,
            fullscreenIntent,
            PendingIntent.FLAG_IMMUTABLE.or(PendingIntent.FLAG_UPDATE_CURRENT)
        )

        val snoozeIntent = Intent(context, GoalReflectionReminderNotification::class.java).apply {
            action = ACTION_SNOOZE
            putExtra("snooze", 0)
        }
        val snoozePendingIntent: PendingIntent =
            PendingIntent.getBroadcast(context, 0, snoozeIntent, PendingIntent.FLAG_IMMUTABLE)

        val setGoalBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Time to Reflect on your Goals!")
            .setContentText("Reflecting on your goals is an important step in reaching them!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setFullScreenIntent(fullscreenPendingIntent, true)
            .addAction(R.drawable.ic_launcher_background, "Snooze", snoozePendingIntent)

        showNotification(context, setGoalBuilder.build(), Random.nextInt())
    }

}