package com.gorillamoa.routines.core.extensions

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.gorillamoa.routines.core.receiver.NotificationActionReceiver


/**
 * creates an intent to access the NotificationActionReceiver's actions
 * @receiver Context
 * @param action String
 * @return PendingIntent
 */

fun Context.createNotificationActionPendingIntentForWakeUp(action:String): PendingIntent {
    Log.d("notificationRoutine","createNotificationActionPendingIntentForWakeUp ACTION:$action")

    val intent = Intent(this, NotificationActionReceiver::class.java)
    intent.action = action
    return PendingIntent.getBroadcast(this, 0,intent, PendingIntent.FLAG_ONE_SHOT)
}