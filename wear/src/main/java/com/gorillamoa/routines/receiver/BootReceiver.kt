package com.gorillamoa.routines.receiver

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.gorillamoa.routines.extensions.alarmEnableSleep
import com.gorillamoa.routines.extensions.alarmEnableWakeUp
import com.gorillamoa.routines.extensions.isSleepAlarmSet
import com.gorillamoa.routines.extensions.isWakeAlarmSet

class SimpleBootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            // Set the alarm here.
            context.apply {
                if (isSleepAlarmSet()) {
                    alarmEnableSleep()
                }

                if (isWakeAlarmSet()) {
                    alarmEnableWakeUp()
                }
            }
        }
    }

    companion object {

        fun enableBootReceiver(context: Context){
            val receiver = ComponentName(context, SimpleBootReceiver::class.java)

            context.packageManager.setComponentEnabledSetting(
                    receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP
            )
        }

        fun disableBootReciver(context: Context){
            val receiver = ComponentName(context, SimpleBootReceiver::class.java)

            context.packageManager.setComponentEnabledSetting(
                    receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP
            )
        }
    }
}