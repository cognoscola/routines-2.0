package com.gorillamoa.routines.core.extensions

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.gorillamoa.routines.core.receiver.AlarmReceiver
import com.gorillamoa.routines.core.receiver.AlarmReceiver.Companion.ACTION_SLEEP
import com.gorillamoa.routines.core.receiver.AlarmReceiver.Companion.EVENT_WAKEUP
import com.gorillamoa.routines.core.receiver.AlarmReceiver.Companion.SLEEP_INTENT_CODE
import com.gorillamoa.routines.core.receiver.AlarmReceiver.Companion.WAKE_UP_INTENT_CODE
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

fun Context.createAlarmPendingIntent(intent:Intent, code:Int):PendingIntent{
    return PendingIntent.getBroadcast(this,
            code,
            intent,
//            0) //We don't care about extras right now
            PendingIntent.FLAG_UPDATE_CURRENT) //change if we are carrying extras
}

fun Context.createWakeUpAlarmPendingIntent():PendingIntent{

    return createAlarmPendingIntent(createAlarmIntent()
            .apply { action = EVENT_WAKEUP }, WAKE_UP_INTENT_CODE)
}


fun Context.createSleepAlarmPendingIntent():PendingIntent{
    return createAlarmPendingIntent(createAlarmIntent()
            .apply { action = ACTION_SLEEP }, SLEEP_INTENT_CODE)
}

/**
 * Create an alarm intent for AlarmReceiverClass
 * @receiver Context application context
 * @return Intent
 */
fun Context.createAlarmIntent():Intent{
    return Intent(this, AlarmReceiver::class.java).apply {
        addFlags(Intent.FLAG_RECEIVER_FOREGROUND) //to give forground priority
            putExtra(AlarmReceiver.KEY_ALARM,true)
    }
}

