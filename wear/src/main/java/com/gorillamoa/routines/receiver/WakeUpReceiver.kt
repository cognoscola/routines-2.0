package com.gorillamoa.routines.receiver

import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.PendingIntent

import android.text.Html
import android.util.Log
import com.gorillamoa.routines.R

import com.gorillamoa.routines.activity.OnboardActivity


/**
 * What to do when our app sounds the "wake up" alarm.
 * Again, this alarm is not a "wake from human sleep" alarm. Instead
 * it lets the user know, via some UI, notifications probably, that they
 * should prepare the day's tasks.
 */
class WakeUpReceiver:BroadcastReceiver(){

    val MAX_NOTIFICATION_LINE_LENGTH = 23

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("onReceive","Woken up twice...")

        (context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).apply {

            //TODO ENSURE 1.0+ compatibility, right now it only works on 2.0

            val wakeUpNotificationId = context.resources.getInteger(R.integer.wakeup_notification_id)

            /** Prepare the intent for when user decides click Open (Wear) or the notification (Phone) **/
            val mainIntent = Intent(context, OnboardActivity::class.java)
            val mainPendingIntent = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT)

            /** Prepare the intent for when user dismisses the notification **/
            val dismissIntent = Intent(context, NotificationDismissReceiver::class.java)
            dismissIntent.putExtra("com.my.app.notificationId", wakeUpNotificationId)
            val dismissPendingIntent = PendingIntent.getBroadcast(context.applicationContext, 0, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT)

            //TODO we need to have a task retriever method
            val stringBuilder = StringBuilder()
            buildLine("meditate","1hr",stringBuilder)
            buildLine("Meeting with John again twice","9min",stringBuilder)
            buildLine("Ultra super short","1reps",stringBuilder)
            buildLine("Dod","4catches",stringBuilder)
            buildLine("pick kids up from school","2p",stringBuilder)
            buildEndLine(20,stringBuilder)

            val bigTextStyle = Notification.BigTextStyle()
                    .setBigContentTitle(Html.fromHtml("Today's tasks &#128170;",Html.FROM_HTML_MODE_COMPACT))
                    .bigText(Html.fromHtml(stringBuilder.toString(),Html.FROM_HTML_SEPARATOR_LINE_BREAK_LIST))
                    .setSummaryText("+3 more")

            val mainBuilder = Notification.Builder(context,context.resources.getString(R.string.notificationchannel_one))
            mainBuilder
                    .setStyle(bigTextStyle)
                    //TODO show weather in one icon in the notification title
                    .setContentTitle(Html.fromHtml("Good morning! &#127780",Html.FROM_HTML_MODE_COMPACT))
                    .setSmallIcon(com.gorillamoa.routines.R.mipmap.ic_launcher)
                    .setContentText("See today's schedule")
                    .setAutoCancel(true)
                    .setCategory(Notification.CATEGORY_REMINDER)
                    .setContentIntent(mainPendingIntent)
                    .setDeleteIntent(dismissPendingIntent)

            notify(context.resources.getString(R.string.notification_tag),wakeUpNotificationId, mainBuilder.build())
        }
    }

    /**
     * We're going to build a line such that it only has 25 characters.
     * @param detail the showing detail, it can a time length or time specfied or task detail
     */
    private fun buildLine(task:String, detail:String,builder:java.lang.StringBuilder){

        //detail should only be 5 chars max

        //eg. Meditate
        if (task.isEmpty() or task.isBlank()) {
            return
        }

        //TODO predict relevant emoji for each task
        builder.append("&#9999;&nbsp;")
        if (task.length > MAX_NOTIFICATION_LINE_LENGTH) {

            //TODO after you cut the line, make sure the end isn't a space

            builder.append(task,0,(MAX_NOTIFICATION_LINE_LENGTH - 3 - Math.min(detail.length,5)))
            builder.append("...")

        }else{

            builder.append(task)
            for (i in 0..(MAX_NOTIFICATION_LINE_LENGTH - task.length - Math.min(detail.length,5))) {
                builder.append("&nbsp")
            }
        }

        builder.append("<br>")
    }

    private fun buildEndLine(remainingTasks:Int, builder: java.lang.StringBuilder){

        builder.append("<i>")
        if (remainingTasks < 10) {
            builder.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;")
            builder.append("+${remainingTasks} more")
            builder.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;")
        }else if (remainingTasks <100){
            builder.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;")
            builder.append("+${remainingTasks} more"      )
        }else if (remainingTasks < 1000){
            builder.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;")
            builder.append("+${remainingTasks} more"     )
        }
        builder.append("</i>")

    }

}