package io.aasmart.goalpost.goals.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import io.aasmart.goalpost.MainActivity
import io.aasmart.goalpost.R
import io.aasmart.goalpost.receivers.GoalReflectionReceiver
import io.aasmart.goalpost.utils.AlarmHelper

object GoalReflectionNotification : GoalpostNotification() {
    private const val ACTION_SNOOZE = "SNOOZE"

    class GoalReflectionReminderNotification : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if(intent?.action == ACTION_SNOOZE)
                context?.let { snooze(it) }
        }

        private fun snooze(context: Context) {
            AlarmHelper.scheduleInexactAlarm(
                context,
                GoalReflectionReceiver::class.java,
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                2
            )

            GoalReflectionNotification.cancelNotification(context, notificationId)
        }
    }

    override val notificationId: Int
        get() = 0

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
        val snoozePendingIntent: PendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            snoozeIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val setGoalBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(context.resources.getString(R.string.notification_reflect_title))
            .setContentText(context.resources.getString(R.string.notification_reflect_desc))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
            .setFullScreenIntent(fullscreenPendingIntent, true)
            .addAction(
                R.mipmap.ic_launcher_round,
                context.resources.getString(R.string.snooze),
                snoozePendingIntent
            )

        showNotification(context, setGoalBuilder.build())
    }

}