package io.aasmart.goalpost.goals.notifications

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import io.aasmart.goalpost.MainActivity
import io.aasmart.goalpost.R

object GoalMidDayReminderNotification : GoalpostNotification() {
    override val notificationId: Int
        get() = 13453221

    override fun pushNotification(context: Context) {
        val activityIntent = Intent(context, MainActivity::class.java)
        val activityPendingIntent = PendingIntent.getActivity(
            context,
            0,
            activityIntent,
            PendingIntent.FLAG_IMMUTABLE.or(PendingIntent.FLAG_UPDATE_CURRENT)
        )

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(context.resources.getString(R.string.notification_reminder_title))
            .setContentText(context.resources.getString(R.string.notification_reminder_midday_desc))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
            .setContentIntent(activityPendingIntent)

        showNotification(context, notificationBuilder.build())
    }
}