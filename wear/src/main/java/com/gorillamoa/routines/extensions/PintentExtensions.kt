package com.gorillamoa.routines.extensions

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.gorillamoa.routines.activity.OnboardActivity
import com.gorillamoa.routines.receiver.NotificationDismissReceiver
import com.gorillamoa.routines.receiver.AlarmReceiver
import com.gorillamoa.routines.receiver.AlarmReceiver.Companion.ACTION_DEFAULT
import com.gorillamoa.routines.receiver.AlarmReceiver.Companion.ACTION_SLEEP
import com.gorillamoa.routines.receiver.AlarmReceiver.Companion.SLEEP_INTENT_CODE
import com.gorillamoa.routines.receiver.AlarmReceiver.Companion.WAKE_UP_INTENT_CODE

/**
 * A place to store all the Intent and PendingIntent extensions
 */

public const val TASK_ID ="TaskId"

//TODO COMMENT THIS PAGE

/**
 * Creates a PendingIntent for the AlarmReceiver
 */
fun Context.createWakeUpAlarmPendingIntent():PendingIntent{

    return createAlarmPendingIntent(createAlarmIntent()
            .apply { action = ACTION_DEFAULT }, WAKE_UP_INTENT_CODE)
}

fun Context.createSleepAlarmPendingIntent():PendingIntent{

    return createAlarmPendingIntent(createAlarmIntent()
            .apply { action = ACTION_SLEEP }, SLEEP_INTENT_CODE)
}

fun Context.createAlarmPendingIntent(intent:Intent, code:Int):PendingIntent{
    return PendingIntent.getBroadcast(this,
            code,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT)
}


fun Context.createAlarmIntent():Intent{
    return Intent(this, AlarmReceiver::class.java).apply {
        putExtra(AlarmReceiver.KEY_ALARM,true)
    }
}





/**
 * creates the notification's main intent (when the notification is clicked)
 * such that we are directed to the onboard
 */
fun Context.createNotificationMainIntentForOnboarding():PendingIntent{
    val mainIntent = Intent(this, OnboardActivity::class.java)
    mainIntent.action = OnboardActivity.ACTION_TEST_WAKE_UP
    return PendingIntent.getActivity(this, 0, mainIntent, PendingIntent.FLAG_ONE_SHOT)
}


fun Context.createWakeUpRecieverIntent():Intent{
    return Intent(this, AlarmReceiver::class.java)
}

/**
 * create the notification's main intent
 * when user clicks on a wake up notification
 */
//TODO redirect to another activity
fun Context.createNotificationMainIntentForWakeUp():PendingIntent{
    val mainIntent = Intent(this, OnboardActivity::class.java)
    return PendingIntent.getActivity(this, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT)
}

/**
 * The user dismisses a Wake Up Intent. System needs to take an action
 * For now We'll just launch the first task.
 */
fun Context.createNotificationDeleteIntentForWakeUp():PendingIntent{
    val dismissIntent = Intent(this, NotificationDismissReceiver::class.java)
    //signal the receiver that it came from a wake up notification
    dismissIntent.action = NotificationDismissReceiver.TYPE_WAKE_UP
    //we're passing in the tid
    return PendingIntent.getBroadcast(this,0, dismissIntent,PendingIntent.FLAG_ONE_SHOT)

}

/**
 * The user dismisses a Task notification.
 * @param tid is the id of the task being dismissed
 */
fun Context.createNotificationDeleteIntentForTask(tid: Int):PendingIntent{
    val dismissIntent = Intent(this, NotificationDismissReceiver::class.java)
    dismissIntent.action = NotificationDismissReceiver.TYPE_TASK
    dismissIntent.putExtra(TASK_ID,tid)
    return PendingIntent.getBroadcast(this,tid, dismissIntent,PendingIntent.FLAG_ONE_SHOT)

}
