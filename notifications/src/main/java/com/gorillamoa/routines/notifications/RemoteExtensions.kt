
package com.gorillamoa.routines.notifications

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
/*
import com.gorillamoa.routines.MobileNotificationBehaviourReceiver.Companion.ACTION_TASK_EXPAND
import com.gorillamoa.routines.MobileNotificationBehaviourReceiver.Companion.ACTION_WAKEUP_COLLAPSE
import com.gorillamoa.routines.core.data.Task
import com.gorillamoa.routines.core.receiver.NotificationActionReceiver.Companion.ACTION_WAKE_START_DAY
import com.gorillamoa.routines.core.receiver.NotificationActionReceiver.Companion.ACTION_TASK_NEXT
import com.gorillamoa.routines.core.receiver.NotificationActionReceiver.Companion.ACTION_TASK_PREVIOUS
import com.gorillamoa.routines.core.scheduler.TaskScheduler

*/
import android.graphics.Paint
/*import com.gorillamoa.routines.core.constants.DataLayerConstant.Companion.KEY_TASK_DATA
import com.gorillamoa.routines.core.extensions.*
import com.gorillamoa.routines.core.receiver.NotificationActionReceiver.Companion.ACTION_DONE
import com.gorillamoa.routines.core.receiver.NotificationActionReceiver.Companion.ACTION_TASK_UNCOMPLETE*/
import com.gorillamoa.routines.notifications.impl.getHtml

/**
 * Creates a remote view specifically designed (dimension wise) for a large sized notification.
 * @receiver Context
 * @param bigStringContent String
 * @return RemoteViews
 */
fun Context.createLargeWakeUpRemoteView(bigStringContent: String): RemoteViews {
    val remoteViews = RemoteViews(packageName, R.layout.remote_wakeup_large)
    remoteViews.setTextViewText(R.id.title, getHtml(getString(R.string.wake_up_large_title)))
    remoteViews.setTextViewText(R.id.bigContent, getHtml(bigStringContent))

    //Give option to collapse
    remoteViews.setImageViewResource(R.id.behaviourImage, R.drawable.ic_expand_less_black_24dp)
    remoteViews.setTextViewText(R.id.behaviourText, getString(R.string.collapse))

    return remoteViews
}

/**
 * Creates a Remote View. This View is intented for a regular-sized notification. So don't assign it
 * to a large content view
 * @receiver Context
 * @param taskLength Int
 * @return RemoteViews
 */
fun Context.createWakeUpRemoteView(taskLength:Int): RemoteViews {

    val remoteViews = RemoteViews(packageName, R.layout.remote_wakeup)
    remoteViews.setTextViewText(R.id.title, getHtml(getString(R.string.wake_up_title)))
    remoteViews.setTextViewText(R.id.description, getHtml(getString(R.string.wake_up_description, taskLength )))
    remoteViews.setImageViewResource(R.id.behaviourImage, R.drawable.ic_expand_more_black_24dp)

    return remoteViews
}


/**
 * Assigns the specified pending intent to the start function
 * @receiver RemoteViews
 * @param startPendingIntent PendingIntent
 */
fun RemoteViews.setIntentToStartButton(startPendingIntent:PendingIntent) {
    setOnClickPendingIntent(R.id.start, startPendingIntent)
}

/************************************************************************************
 * OLD FUNCTIONS
 *********************************************************************************/
//TODO deprecate this
//TODO Set Height of remote view  to MIN of (WRAP_CONTENT, 256dp )
fun Context.getLargeWakeUpRemoteView(bigStringContent: String): RemoteViews {
    val remoteViews = RemoteViews(packageName, R.layout.remote_wakeup_large)
    remoteViews.setTextViewText(R.id.title, getHtml(getString(R.string.wake_up_large_title)))
    remoteViews.setTextViewText(R.id.bigContent, getHtml(bigStringContent))

    //Give option to collapse
    remoteViews.setImageViewResource(R.id.behaviourImage, R.drawable.ic_expand_less_black_24dp)
    remoteViews.setTextViewText(R.id.behaviourText, getString(R.string.collapse))

    return remoteViews
}

fun Context.getLargeTaskRemoteView(bigTaskContent:String):RemoteViews{

    val remoteViews = RemoteViews(packageName, R.layout.remote_task_large)
    remoteViews.setTextViewText(R.id.bigContent, getHtml(bigTaskContent))
    return remoteViews

}


//TODO CONFIGURE appearance for empty tasks
/*fun RemoteViews.createFunction(context:Context, tasks:String?, action:String,tid:Long = -1):RemoteViews{

     try {
        tasks?.let {
            val intent = Intent(context, MobileNotificationBehaviourReceiver::class.java)
            intent.action = action
            intent.putExtra(KEY_TASK_DATA, it)
            val pIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

            if (tid != -1L) {
                intent.putExtra(TASK_ID, tid)
                setOnClickPendingIntent(R.id.contentGroup, pIntent)
            }else{
                setOnClickPendingIntent(R.id.notificationContainer, pIntent)
            }
        }

    } catch (ignored: ClassNotFoundException) {
        Log.e("Unknown Activity Name",ignored.message)
    }
    return this
}*/

/*fun RemoteViews.createCollapseFunction(context: Context,tasks: String?):RemoteViews{

    try {
        tasks?.let {
            val intent = Intent(context, MobileNotificationBehaviourReceiver::class.java)
            intent.action = ACTION_WAKEUP_COLLAPSE
            intent.putExtra(KEY_TASK_DATA, it)

            val pIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            setOnClickPendingIntent(R.id.behaviourButton, pIntent)
        }

    } catch (ignored: ClassNotFoundException) {
        Log.e("Unknown Activity Name",ignored.message)
    }
    return this
}*/




fun Context.getTaskRemoteView(task:String):RemoteViews{

    val remoteViews = RemoteViews(packageName, R.layout.remote_task)
/*    remoteViews.setTextViewText(R.id.title, task.name)
    remoteViews.setTextViewText(R.id.description, task.description)

    setTaskCompletionStatus(task,remoteViews)

    setDirectionFunctions(task,remoteViews)
    //TODO MOVE THIS OUT OF HERE, we may want to create a notification without having this function
    remoteViews.createFunction(this, getGson().toJson(task),ACTION_TASK_EXPAND,task.id!!)*/

    return remoteViews
}

/*
fun Context.setTaskCompletionStatus(task:Task, remoteView: RemoteViews){

    if(TaskScheduler.isComplete(this, task.id!!)){

        remoteView.setImageViewResource(R.id.statusImage,R.drawable.ic_check_box_black_24dp)
        remoteView.setInt(R.id.title, "setPaintFlags", Paint.STRIKE_THRU_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG)
        //we're completed so the next Intent should "Uncomplete" the task
        remoteView.setOnClickPendingIntent(R.id.statusButton,createNotificationActionPendingIntentForTask(task,null,ACTION_TASK_UNCOMPLETE))

    }else{

        //we aren't complete so next intent should complete the task
        remoteView.setImageViewResource(R.id.statusImage,R.drawable.ic_crop_square_black_24dp)
        remoteView.setInt(R.id.title, "setPaintFlags", Paint.ANTI_ALIAS_FLAG)
        remoteView.setOnClickPendingIntent(R.id.statusButton,createNotificationActionPendingIntentForTask(task,null,ACTION_DONE))
    }
}*/

/*
fun Context.setDirectionFunctions(task:Task,remoteView:RemoteViews){


    remoteView.setOnClickPendingIntent(R.id.nextGroup,createNotificationActionPendingIntentForTask(task, null,ACTION_TASK_NEXT))
    remoteView.setOnClickPendingIntent(R.id.previousGroup,createNotificationActionPendingIntentForTask(task,null, ACTION_TASK_PREVIOUS))
}
*/

fun Context.getSmallSleepView():RemoteViews{
    val remoteViews = RemoteViews(packageName,R.layout.remote_sleep)
//    remoteViews.setTextViewText(R.id.title, getHtml(getString(R.string.sleep_title)))
//    remoteViews.setTextViewText(R.id.percentScore, TaskScheduler.getScoreString(this@getSmallSleepView))
//    remoteViews.setTextViewText(R.id.points, TaskScheduler.getPoints(this@getSmallSleepView).toString())
//    remoteViews.setTextViewText(R.id.description, getString(R.string.details))

    return remoteViews
}

fun Context.getLargeSleepView():RemoteViews{
    return RemoteViews(packageName,0)
}

