package io.aasmart.goalpost.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import io.aasmart.goalpost.goals.scheduleReflectionAlarm
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    @OptIn(DelicateCoroutinesApi::class)
    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent?.action != Intent.ACTION_BOOT_COMPLETED)
            return;
        if(context == null)
            return;

        Log.d("Phone Booted", "Running boot methods")

        GlobalScope.launch {
            scheduleReflectionAlarm(
                context
            )
        }
    }
}