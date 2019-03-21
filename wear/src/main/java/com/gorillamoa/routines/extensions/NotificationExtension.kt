package com.gorillamoa.routines.extensions

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.text.Html
import com.gorillamoa.routines.data.Task
import com.gorillamoa.routines.receiver.WakeUpReceiver

const val WAKE_UP_NOTIFICATION_ID =1
const val SLEEP_NOTIFICATION_ID =65535

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
        mainBuilder.setStyle(prepareBigTextStyle(tasks))
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
                                  dimissPendingIntent: PendingIntent? = null){

        val manager = getNotificationManager()
        getBuilder().apply {

                setContentTitle(task.name)
                setContentText("This is a sample task")
                setAutoCancel(true)
                setCategory(Notification.CATEGORY_REMINDER)
                setDeleteIntent(dimissPendingIntent)

                manager.notify(
                        NOTIFICATION_TAG,
                        task.id!!,
                        build()
                        )
        }
}


fun Context.prepareBigTextStyle(tasks:String):Notification.BigTextStyle{
        return Notification.BigTextStyle()
         .setBigContentTitle(Html.fromHtml("Today's tasks &#128170;", Html.FROM_HTML_MODE_COMPACT))
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

