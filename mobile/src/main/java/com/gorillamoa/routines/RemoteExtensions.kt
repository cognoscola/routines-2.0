package com.gorillamoa.routines

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import com.gorillamoa.routines.MobileNotificationBehaviourReceiver.Companion.ACTION_TASK_EXPAND
import com.gorillamoa.routines.MobileNotificationBehaviourReceiver.Companion.ACTION_WAKEUP_COLLAPSE
import com.gorillamoa.routines.core.data.Task
import com.gorillamoa.routines.core.receiver.NotificationActionReceiver.Companion.ACTION_WAKE_START_DAY
import com.gorillamoa.routines.core.receiver.NotificationActionReceiver.Companion.ACTION_TASK_NEXT
import com.gorillamoa.routines.core.receiver.NotificationActionReceiver.Companion.ACTION_TASK_PREVIOUS
import com.gorillamoa.routines.core.scheduler.TaskScheduler

import android.graphics.Paint
import com.gorillamoa.routines.core.extensions.*
import com.gorillamoa.routines.core.receiver.NotificationActionReceiver.Companion.ACTION_DONE
import com.gorillamoa.routines.core.receiver.NotificationActionReceiver.Companion.ACTION_TASK_UNCOMPLETE


//TODO Set Height of remote view  to MIN of (WRAP_CONTENT, 256dp )
fun Context.getLargeWakeUpRemoteView(bigStringContent: String): RemoteViews {
    val remoteViews = RemoteViews(packageName, R.layout.remote_wakeup_large)
    remoteViews.setTextViewText(R.id.title, getHtml(getString(R.string.wake_up_large_title)))
    remoteViews.setTextViewText(R.id.bigContent, getHtml(bigStringContent))

    //Give option to collapse
    remoteViews.setImageViewResource(R.id.behaviourImage, R.drawable.ic_expand_less_black_24dp)
    remoteViews.setTextViewText(R.id.behaviourText, getString(R.string.collapse))

    setStartFunction(remoteViews)
    return remoteViews
}

fun Context.setStartFunction(remoteViews: RemoteViews) {

        remoteViews.setOnClickPendingIntent(R.id.start, createNotificationActionPendingIntentForWakeUp(ACTION_WAKE_START_DAY))
}

//TODO CONFIGURE appearance for empty tasks
fun RemoteViews.createFunction(context:Context, tasks:String?, action:String,tid:Int = -1):RemoteViews{

     try {
        tasks?.let {
            val intent = Intent(context, MobileNotificationBehaviourReceiver::class.java)
            intent.action = action
            intent.putExtra(TASK_DATA, it)
            val pIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

            if (tid != -1) {
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
}

fun RemoteViews.createCollapseFunction(context: Context,tasks: String?):RemoteViews{

    try {
        tasks?.let {
            val intent = Intent(context, MobileNotificationBehaviourReceiver::class.java)
            intent.action = ACTION_WAKEUP_COLLAPSE
            intent.putExtra(TASK_DATA, it)

            val pIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            setOnClickPendingIntent(R.id.behaviourButton, pIntent)
        }

    } catch (ignored: ClassNotFoundException) {
        Log.e("Unknown Activity Name",ignored.message)
    }
    return this
}

fun Context.getWakeupRemoteView(taskLength:Int): RemoteViews {

    val remoteViews = RemoteViews(packageName, R.layout.remote_wakeup)
    remoteViews.setTextViewText(R.id.title, getHtml(getString(R.string.wake_up_title)))
    remoteViews.setTextViewText(R.id.description, getHtml(getString(R.string.wake_up_description, taskLength )))
    remoteViews.setImageViewResource(R.id.behaviourImage, R.drawable.ic_expand_more_black_24dp)

    setStartFunction(remoteViews)
    return remoteViews
}

fun Context.getTaskRemoteView(task:Task):RemoteViews{
    val remoteViews = RemoteViews(packageName, R.layout.remote_task)
    remoteViews.setTextViewText(R.id.title, task.name)
    remoteViews.setTextViewText(R.id.description, task.description)

    setTaskCompletionStatus(task,remoteViews)

    setDirectionFunctions(task,remoteViews)
    //TODO MOVE THIS OUT OF HERE, we may want to create a notification without having this function
    remoteViews.createFunction(this, getGson().toJson(task),ACTION_TASK_EXPAND,task.id!!)

    return remoteViews
}

fun Context.setTaskCompletionStatus(task:Task, remoteView: RemoteViews){

    if(TaskScheduler.isComplete(this, task.id!!)){

        remoteView.setImageViewResource(R.id.statusImage,R.drawable.ic_check_box_black_24dp)
        remoteView.setInt(R.id.title, "setPaintFlags", Paint.STRIKE_THRU_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG)
        //we're completed so the next Intent should "Uncomplete" the task
        remoteView.setOnClickPendingIntent(R.id.statusButton,createNotificationActionPendingIntentForTask(task,ACTION_TASK_UNCOMPLETE))

    }else{

        //we aren't complete so next intent should complete the task
        remoteView.setImageViewResource(R.id.statusImage,R.drawable.ic_crop_square_black_24dp)
        remoteView.setInt(R.id.title, "setPaintFlags", Paint.ANTI_ALIAS_FLAG)
        remoteView.setOnClickPendingIntent(R.id.statusButton,createNotificationActionPendingIntentForTask(task,ACTION_DONE))
    }
}

fun Context.setDirectionFunctions(task:Task,remoteView:RemoteViews){

    remoteView.setOnClickPendingIntent(R.id.nextGroup,createNotificationActionPendingIntentForTask(task, ACTION_TASK_NEXT))
    remoteView.setOnClickPendingIntent(R.id.previousGroup,createNotificationActionPendingIntentForTask(task, ACTION_TASK_PREVIOUS))


}
