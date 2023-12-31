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
        initialTriggerMillis: Long,
        requestCode: Int
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, broadcastReceiverClass)

        // Run the repeating alarm after that
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            initialTriggerMillis,
            intervalMillis,
            PendingIntent.getBroadcast(
                context,
                requestCode,
                alarmIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        )
    }

    fun <T : BroadcastReceiver> scheduleInexactAlarm(
        context: Context,
        broadcastReceiverClass: Class<T>,
        initialTriggerMillis: Long,
        requestCode: Int
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, broadcastReceiverClass)

        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            initialTriggerMillis,
            PendingIntent.getBroadcast(
                context,
                requestCode,
                alarmIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        )
    }

    fun <T : BroadcastReceiver> cancelAlarm(
        context: Context,
        broadcastReceiverClass: Class<T>,
        requestCode: Int
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, broadcastReceiverClass)

        alarmManager.cancel(PendingIntent.getBroadcast(
            context,
            requestCode,
            alarmIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        ))
    }
}