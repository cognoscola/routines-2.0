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

    return try {
        val c = Class.forName(startingActivityName)
        val intent = Intent(this, c)
        PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    } catch (ignored: ClassNotFoundException) {
        Log.e("Unknown Activity Name",ignored.message)
        null
    }
}




/**
 * create the notification's main intent
 * when user clicks on a wake up notification
 */
//TODO create Task view Activity in Mobile
fun Context.createNotificationMainIntentForWakeUp():PendingIntent?{

    return try {
//        val c = Class.forName("com.gorillamoa.routines.activity.TaskViewActivity")
//        val intent = Intent(this, c)
//        PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        null
    } catch (ignored: ClassNotFoundException) {
        Log.e("Unknown Activity Name",ignored.message)
        null
    }
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

/**
 * create a notification action which will mark the displayed task as done
 * @param tid is the task id of the task currently being displayed
 */
fun Context.createNotificationActionPendingIntent(task: Task?, action:String):PendingIntent{

    val doneIntent = Intent(this, NotificationActionReceiver::class.java)
    doneIntent.action = action
    if (task != null) {
        doneIntent.putExtra(TASK_ID,task.id?:-1)
        doneIntent.putExtra(TASK_DATA, getGson().toJson(task))
    }
    Log.d("ActionPendingIntent","ACTION:$action")

    return PendingIntent.getBroadcast(this, task?.id?:-1,doneIntent,PendingIntent.FLAG_ONE_SHOT)

}
