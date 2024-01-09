package io.aasmart.goalpost.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

object AlarmHelper {
    /**
     * @param context The context
     * @param broadcastReceiverClass The class of the broadcast receiver that will
     * trigger when the alarm goes off
     * @param intervalMillis The interval between each subsequent repeat of the alarm
     * @param initialTriggerMillis The time, in UTC, that the alarm should first trigger
     * @param requestCode The alarm's request code
     */
    fun <T : BroadcastReceiver> scheduleRepeatingAlarm(
        context: Context,
        broadcastReceiverClass: Class<T>,
        intervalMillis: Long,
        initialTriggerMillis: Long,
        requestCode: Int,
        extras: Map<String, String> = emptyMap()
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, broadcastReceiverClass).apply {
            extras.forEach { this.putExtra(it.key, it.value) }
        }

        // Run the repeating alarm after that
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            initialTriggerMillis,
            intervalMillis,
            PendingIntent.getBroadcast(
                context,
                requestCode,
                alarmIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
        )

        Log.d("Alarm Scheduled", "Scheduled a repeating alarm with interval " +
                "$intervalMillis and initial trigger $initialTriggerMillis"
        )
    }

    fun <T : BroadcastReceiver> scheduleInexactAlarm(
        context: Context,
        broadcastReceiverClass: Class<T>,
        type: Int,
        initialTriggerMillis: Long,
        requestCode: Int,
        extras: Map<String, String> = emptyMap()
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, broadcastReceiverClass).apply {
            extras.forEach { this.putExtra(it.key, it.value) }
        }

        alarmManager.set(
            type,
            initialTriggerMillis,
            PendingIntent.getBroadcast(
                context,
                requestCode,
                alarmIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
        )

        Log.d("Alarm Scheduled", "Scheduled an inexact alarm with " +
                "initial trigger $initialTriggerMillis"
        )
    }

    fun <T : BroadcastReceiver> scheduleExactAlarm(
        context: Context,
        broadcastReceiverClass: Class<T>,
        type: Int,
        initialTriggerMillis: Long,
        requestCode: Int,
        extras: Map<String, String> = emptyMap()
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, broadcastReceiverClass).apply {
            extras.forEach { this.putExtra(it.key, it.value) }
        }

        alarmManager.setExact(
            type,
            initialTriggerMillis,
            PendingIntent.getBroadcast(
                context,
                requestCode,
                alarmIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
        )

        Log.d("Alarm Scheduled", "Scheduled an exact alarm with " +
                "initial trigger $initialTriggerMillis"
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
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
        ))
    }
}