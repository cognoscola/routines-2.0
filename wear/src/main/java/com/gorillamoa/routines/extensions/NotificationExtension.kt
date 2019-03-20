package com.gorillamoa.routines.extensions

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.text.Html
import com.gorillamoa.routines.receiver.WakeUpReceiver

const val WAKE_UP_NOTIFICATION_ID =1
const val SLEEP_NOTIFICATION_ID =65535

public const val NOTIFICATION_CHANNEL_ONE  = "channel"
const val NOTIFICATION_TAG = "routines"


/** Prepare the intent for when user dismisses the notification **/
//TODO obfuscate strings later using obfuscation library
//TODO create dismiss intent
/*  val dismissIntent = Intent(context, NotificationDismissReceiver::class.java)
    dismissIntent.putExtra("com.gorillamoa.routines.notificationId",wakeUpNotificationId)
    val dismissPendingIntent = PendingIntent.getBroadcast(context.applicationContext, 22, dismissIntent, PendingIntent.FLAG_ONE_SHOT)*/


fun Context.notificationShowWakeUp(tasks:String, mainPendingIntent: PendingIntent){

        //TODO ENSURE 1.0+ compatibility, right now it only works on 2.0

        val manager = getNotificationManager()
        val mainBuilder = getBuilder()
        mainBuilder.setStyle(prepareBigTextStyle(tasks))
        mainBuilder.setContentIntent(mainPendingIntent)

        manager.notify(
                NOTIFICATION_TAG,
                WAKE_UP_NOTIFICATION_ID,
                mainBuilder.build())

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

