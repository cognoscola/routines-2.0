package com.gorillamoa.routines.extensions

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.drawable.Icon
import android.text.Html
import android.util.Log
import com.gorillamoa.routines.R
import com.gorillamoa.routines.data.Task
import com.gorillamoa.routines.receiver.TaskActionReceiver.Companion.ACTION_DONE
import com.gorillamoa.routines.receiver.TaskActionReceiver.Companion.ACTION_INTO_FUTURE
import com.gorillamoa.routines.receiver.TaskActionReceiver.Companion.ACTION_SKIP_SHORT
import com.gorillamoa.routines.receiver.TaskActionReceiver.Companion.ACTION_SKIP_TODAY
import java.util.*

const val WAKE_UP_NOTIFICATION_ID =1
const val SLEEP_NOTIFICATION_ID =65535
const val REST_NOTIFICATION_ID =65534

public const val NOTIFICATION_CHANNEL_ONE  = "channel"
const val NOTIFICATION_TAG = "routines"


/** Prepare the intent for when user dismisses the notification **/
//TODO obfuscate strings later using obfuscation library

/**
 * Show the notification to the user
 * @param tasks is the task list as a string
 * @param mainPendingIntent is the Main notification intent
 * @param dismissPendingIntent is what happens when the user dismisses
 */
fun Context.notificationShowWakeUp(tasks:String,
                                   mainPendingIntent: PendingIntent,
                                   dismissPendingIntent:PendingIntent? = null){

        //TODO ENSURE 1.0+ compatibility, right now it only works on 2.0

        val manager = getNotificationManager()
        val mainBuilder = getBuilder()
        mainBuilder.style =prepareBigTextStyle(tasks,"Today's tasks &#128170;")
        mainBuilder.setContentIntent(mainPendingIntent)

        //TODO make the dismiss action optional
        dismissPendingIntent?.let { mainBuilder.setDeleteIntent(it) }

        manager.notify(
                NOTIFICATION_TAG,
                WAKE_UP_NOTIFICATION_ID,
                mainBuilder.build())

}

fun Context.notificationShowTask(task: Task,
                                  mainPendingIntent: PendingIntent? = null,
                                  dimissPendingIntent: PendingIntent? = null) {

    val manager = getNotificationManager()
    getBuilder().apply {

        setContentTitle(task.name)
        setContentText(task.description)
        setAutoCancel(true)
        setCategory(Notification.CATEGORY_REMINDER)
        setDeleteIntent(dimissPendingIntent)

        Log.d("schedule","Showing Notification id: ${task.id}")

        addTaskAction(this@notificationShowTask,"Done      ", ACTION_DONE,task.id!!)
        addTaskAction(this@notificationShowTask,"Delay     ", ACTION_SKIP_SHORT,task.id!!)
        addTaskAction(this@notificationShowTask,"Skip Today", ACTION_SKIP_TODAY,task.id!!)

        manager.notify(
                NOTIFICATION_TAG,
                task.id!!,
                build()
        )
    }
}

fun Notification.Builder.addTaskAction(context: Context,actionText:String, action:String,tid:Int){

    addAction(Notification.Action.Builder(
            Icon.createWithResource(context ,R.mipmap.ic_launcher),
            actionText,
            context.createNotificationActionPendingIntent(tid,action)
    ).build())
}

fun Context.notificationShowRest(){
    val manager = getNotificationManager()

    getBuilder().apply {

        setContentTitle(Html.fromHtml("Rest!", Html.FROM_HTML_MODE_COMPACT))
//                setContentTitle(Html.fromHtml("All done! &#127769", Html.FROM_HTML_MODE_COMPACT))

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()

        setContentText("Time: ${calendar.get(Calendar.HOUR)}:${calendar.get(Calendar.MINUTE)}")

        setAutoCancel(true)
        setCategory(Notification.CATEGORY_REMINDER)

        manager.notify(
                NOTIFICATION_TAG,
                REST_NOTIFICATION_ID,
                build()
        )
    }

}



fun Context.notificationShowSleep(){

        val manager = getNotificationManager()
        getBuilder().apply {

                //TODO launch with alarm OR with task completion

                //TODO change text depending on above condition
                setContentTitle(Html.fromHtml("All done! &#127881", Html.FROM_HTML_MODE_COMPACT))
//                setContentTitle(Html.fromHtml("All done! &#127769", Html.FROM_HTML_MODE_COMPACT))
                //TODO change text depending on above conditions
                setContentText("See Today's Accomplishments ")

                //TODO MOVE THIS ELSEWHERE
                val completed = getCompletedTaskList()
                val uncompleted = getDayTaskList()
                val total = completed.size + uncompleted.size

                style =prepareBigTextStyle("$completed/$total Tasks Completed!","Results:")
                setAutoCancel(true)
                setCategory(Notification.CATEGORY_SOCIAL)
//                setDeleteIntent(dimissPendingIntent)

                manager.notify(
                        NOTIFICATION_TAG,
                        SLEEP_NOTIFICATION_ID,
                        build()
                )
        }


}




fun Context.prepareBigTextStyle(tasks:String,title:String):Notification.BigTextStyle{
        return Notification.BigTextStyle()
         .setBigContentTitle(Html.fromHtml(title, Html.FROM_HTML_MODE_COMPACT))
         .bigText(Html.fromHtml(tasks, Html.FROM_HTML_SEPARATOR_LINE_BREAK_LIST))
}

fun Context.getBuilder():Notification.Builder{
        return Notification.Builder(this,NOTIFICATION_CHANNEL_ONE)

                //TODO show weather in one icon in the notification title
                .setContentTitle(Html.fromHtml("Good morning! &#127780", Html.FROM_HTML_MODE_COMPACT))
                .setSmallIcon(com.gorillamoa.routines.R.mipmap.ic_launcher)
                .setContentText("See today's schedule")
                .setAutoCancel(true)
                .setCategory(Notification.CATEGORY_REMINDER)
//                                .setDeleteIntent(dismissPendingIntent)
}

/**
 * retrieve the notifcation manager.
 */
fun Context.getNotificationManager():NotificationManager{
    return getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
}

