package com.gorillamoa.routines.extensions

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.gorillamoa.routines.activity.OnboardActivity
import com.gorillamoa.routines.receiver.NotificationDismissReceiver
import com.gorillamoa.routines.receiver.WakeUpReceiver

/**
 * A place to store all the Intent and PendingIntent extensions
 */

public const val TASK_ID ="TaskId"


/**
 * Creates a PendingIntent for the WakeUpReceiver
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
    return Intent(this, WakeUpReceiver::class.java)
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
 * @param tid is the task id of the first task
 */
fun Context.createNotificationDeleteIntentForWakeUp(tid:Int):PendingIntent{
    val dismissIntent = Intent(this, NotificationDismissReceiver::class.java)
    //signal the receiver that it came from a wake up notification
    dismissIntent.action = NotificationDismissReceiver.TYPE_WAKE_UP
    //we're passing in the tid
    dismissIntent.putExtra(TASK_ID,tid)
    return PendingIntent.getBroadcast(this,0, dismissIntent,PendingIntent.FLAG_ONE_SHOT)

}