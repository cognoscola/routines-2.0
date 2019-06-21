package com.gorillamoa.routines.core.extensions

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.gorillamoa.routines.core.data.Task

import com.gorillamoa.routines.core.receiver.AlarmReceiver
import com.gorillamoa.routines.core.receiver.AlarmReceiver.Companion.EVENT_WAKEUP
import com.gorillamoa.routines.core.receiver.AlarmReceiver.Companion.ACTION_SLEEP
import com.gorillamoa.routines.core.receiver.AlarmReceiver.Companion.SLEEP_INTENT_CODE
import com.gorillamoa.routines.core.receiver.AlarmReceiver.Companion.WAKE_UP_INTENT_CODE
import com.gorillamoa.routines.core.receiver.NotificationDismissReceiver
import com.gorillamoa.routines.core.receiver.NotificationActionReceiver


/**
 * A place to store all the Intent and PendingIntent extensions
 */

public const val TASK_ID ="TaskId"
public const val TASK_DATA = "taskDataString"

/**
 * The user is coming from a notification. This notification is a wake-up attempt from
 * the onboard process.
 */
const val ACTION_TEST_WAKE_UP="N0"

//TODO COMMENT THIS PAGE


/**
 * Creates a PendingIntent for the AlarmReceiver
 */
fun Context.createWakeUpAlarmPendingIntent():PendingIntent{

    return createAlarmPendingIntent(createAlarmIntent()
            .apply { action = EVENT_WAKEUP }, WAKE_UP_INTENT_CODE)
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
fun Context.createNotificationMainIntentForOnboarding(startingActivityName: String):PendingIntent?{

    //TODO ACCEPT STRING INTENT AND DON"T RETURN NULL

    return try {
        val c = Class.forName(startingActivityName)
        val intent = Intent(this, c)
        intent.action = ACTION_TEST_WAKE_UP
        PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
    } catch (ignored: ClassNotFoundException) {
        Log.e("Unknown Activity Name",ignored.message)
        null
    }
}


fun Context.createWakeUpRecieverIntent():Intent{
    return Intent(this, AlarmReceiver::class.java)
}


/**
 * create aa wake up's notificiation Main Intent.
 * @param startingActivityName is the name of the activity to start.
 */
fun Context.createNotificationMainIntentForWakeup(startingActivityName:String):PendingIntent?{

    //TODO we'll show a common activity, but bring different fragments depending on the device
    return try {
        val c = Class.forName(startingActivityName)
        val intent = Intent(this, c)
        PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    } catch (ignored: ClassNotFoundException) {
        Log.e("Unknown Activity Name",ignored.message)
        null
    }
}



/****************************************************************
 * Functions for WAKE UP Notifications
 *****************************************************************/
fun Context.createNotificationActionPendingIntentForWakeUp(action:String):PendingIntent{
    Log.d("notificationRoutine","createNotificationActionPendingIntentForWakeUp ACTION:$action")

    val intent = Intent(this,NotificationActionReceiver::class.java)
    intent.action = action
    return PendingIntent.getBroadcast(this, 0,intent,PendingIntent.FLAG_ONE_SHOT)
}

/**
 * The user dismisses a Wake Up Intent. System needs to take an action
 * For now We'll just launch the first task.
 */
fun Context.createNotificationDeleteIntentForWakeUp():PendingIntent{
    Log.d("notificationRoutine","createNotificationDeleteIntentForWakeUp")

    val dismissIntent = Intent(this, NotificationDismissReceiver::class.java)
    //signal the receiver that it came from a wake up notification
    dismissIntent.action = NotificationDismissReceiver.TYPE_WAKE_UP
    //we're passing in the tid
    return PendingIntent.getBroadcast(this,0, dismissIntent,PendingIntent.FLAG_ONE_SHOT)

}

/****************************************************************
 * Functions for TASK  notifications
 *****************************************************************/


/**
 * create a notification action which will mark the displayed task as done
 * @param tid is the task id of the task currently being displayed
 */
fun Context.createNotificationActionPendingIntentForTask(task: Task?, action:String):PendingIntent{
    Log.d("notificationRoutine","createNotificationActionPendingIntentForTask")

    val intent = Intent(this,NotificationActionReceiver::class.java)
    intent.action = action
    if (task != null) {
        intent.putExtra(TASK_ID,task.id?:-1)
        intent.putExtra(TASK_DATA, getGson().toJson(task))
    }

    return PendingIntent.getBroadcast(this, task?.id?:-1,intent,PendingIntent.FLAG_ONE_SHOT)
}

/**
 * The user dismisses a Task notification.
 * @param tid is the id of the task being dismissed
 */
fun Context.createNotificationDeleteIntentForTask(task:Task):PendingIntent{
    Log.d("notificationRoutine","createNotificationDeleteIntentForTask ${task.id}")
    val dismissIntent = Intent(this.applicationContext, NotificationDismissReceiver::class.java)
    dismissIntent.action = NotificationDismissReceiver.TYPE_TASK
    dismissIntent.putExtra(TASK_ID,task.id)
    return PendingIntent.getBroadcast(this.applicationContext,task.id?:0, dismissIntent,PendingIntent.FLAG_ONE_SHOT)

}