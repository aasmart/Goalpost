package io.aasmart.goalpost.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

object AlarmHelper {
    fun <T : BroadcastReceiver> scheduleRepeatingAlarm(
        context: Context,
        broadcastReceiverClass: Class<T>,
        intervalMillis: Long,
        initialTriggerMillis: Long
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, broadcastReceiverClass)

        // Trigger first alarm
        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            initialTriggerMillis,
            PendingIntent.getBroadcast(
                context,
                1,
                alarmIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        )

        // Run the repeating alarm after that
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            intervalMillis,
            intervalMillis,
            PendingIntent.getBroadcast(
                context,
                0,
                alarmIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        )
    }

    fun <T : BroadcastReceiver> cancelAlarm(
        context: Context,
        broadcastReceiverClass: Class<T>
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, broadcastReceiverClass)

        alarmManager.cancel(PendingIntent.getBroadcast(
            context,
            0,
            alarmIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        ))

        alarmManager.cancel(PendingIntent.getBroadcast(
            context,
            1,
            alarmIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        ))
    }
}