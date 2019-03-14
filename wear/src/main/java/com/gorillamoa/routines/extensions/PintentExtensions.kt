package com.gorillamoa.routines.extensions


import android.app.PendingIntent
import android.content.Context

/**
 * A place to store all the Intent and PendingIntent extensions
 *
 */


fun Context.createWakeUpPendingIntent():PendingIntent{
    return android.content.Intent(this, com.gorillamoa.routines.receiver.WakeUpReceiver::class.java).let { intent ->
        intent.action = com.gorillamoa.routines.receiver.WakeUpReceiver.ACTION_DEFAULT
        intent.putExtra(com.gorillamoa.routines.receiver.WakeUpReceiver.KEY_ALARM,true) //indicate that intent came from an alarm trigger
        PendingIntent.getBroadcast(this,
                com.gorillamoa.routines.receiver.WakeUpReceiver.WAKE_UP_INTENT_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT)
    }

}