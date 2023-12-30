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

object SetGoalsNotification : GoalpostNotification() {
    private const val ACTION_SNOOZE = "SNOOZE"
    private const val ACTION_SET_GOAL = "SET_GOAL"

    class SetGoalsNotificationBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.println(Log.ERROR, "ee", "HELLO WORLD")
            if(intent?.action == ACTION_SNOOZE)
                context?.let { snooze(it) }
            else if(intent?.action == ACTION_SET_GOAL)
                setGoal()
        }

        private fun snooze(context: Context) {
            LolNotification.pushNotification(context)
        }

        private fun setGoal() {

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

        val snoozeIntent = Intent(context, SetGoalsNotificationBroadcastReceiver::class.java).apply {
            action = ACTION_SNOOZE
            putExtra("snooze", 0)
        }
        val snoozePendingIntent: PendingIntent =
            PendingIntent.getBroadcast(context, 0, snoozeIntent, PendingIntent.FLAG_IMMUTABLE)

        val setGoalBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("IMPORTANT!")
            .setContentText("You've got LIGMA!!!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setFullScreenIntent(fullscreenPendingIntent, true)
            .addAction(R.drawable.ic_launcher_background, "What's Ligma?", snoozePendingIntent)

        showNotification(context, setGoalBuilder.build(), Random.nextInt())
    }

}

object LolNotification : GoalpostNotification() {
    override fun pushNotification(context: Context) {
        val setGoalBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Lol get got")
            .setContentText("Ligma balls")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.drawable.ic_launcher_background)

        showNotification(context, setGoalBuilder.build(), Random.nextInt())
    }

}