package io.aasmart.goalpost.goals.notifications

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import io.aasmart.goalpost.MainActivity
import io.aasmart.goalpost.R
import io.aasmart.goalpost.data.settingsDataStore
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

object GoalReminderNotification : GoalpostNotification() {
    override val notificationId: Int
        get() = 9843632

    @OptIn(DelicateCoroutinesApi::class)
    override fun pushNotification(context: Context) {
        val activityIntent = Intent(context, MainActivity::class.java)
        val activityPendingIntent = PendingIntent.getActivity(
            context,
            0,
            activityIntent,
            PendingIntent.FLAG_IMMUTABLE.or(PendingIntent.FLAG_UPDATE_CURRENT)
        )

        GlobalScope.launch {
            // Check if the user has incomplete reflections when sending the notification
            // If they do, change the message.
            val settings = context.settingsDataStore.data.first()
            val notificationDescription = if(settings.needsToReflect)
                    R.string.incomplete_reflections
                else
                    R.string.notification_reflect_desc

            val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(context.resources.getString(R.string.notification_reminder_title))
                .setContentText(context.resources.getString(notificationDescription))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
                .setContentIntent(activityPendingIntent)
                .setAutoCancel(true)

            showNotification(context, notificationBuilder.build())
        }
    }
}