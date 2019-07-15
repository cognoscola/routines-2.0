package com.gorillamoa.routines.core.extensions

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.gorillamoa.routines.core.receiver.NotificationActionReceiver


public const val TASK_ID ="TaskId"
/**
 * creates an intent to access the NotificationActionReceiver's actions
 * @receiver Context
 * @param action String
 * @return PendingIntent
 */


fun Context.createNotificationActionPendingIntentForWakeUp(action:String, tid:Int = -1): PendingIntent {
    Log.d("notificationRoutine","createNotificationActionPendingIntentForWakeUp ACTION:$action")

    val intent = Intent(this, NotificationActionReceiver::class.java)
    intent.action = action
    intent.putExtra(TASK_ID,tid)
    return PendingIntent.getBroadcast(this, 0,intent, PendingIntent.FLAG_ONE_SHOT)
}