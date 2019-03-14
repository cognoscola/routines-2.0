package com.gorillamoa.routines.receiver

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.PendingIntent

import android.text.Html
import android.util.Log
import com.gorillamoa.routines.R

import com.gorillamoa.routines.activity.OnboardActivity
import com.gorillamoa.routines.extensions.getNotificationManager


/**
 * What to do when our app sounds the "wake up" alarm.
 * Again, this alarm is not a "wake from human sleep" alarm. Instead
 * it lets the user know, via some UI, notifications probably, that they
 * should prepare the day's tasks.
 *
 * There are two types of "Wake up" Alarms:
 * ACTION_ONBOARD - fires once when we are onboarding the user
 * ACTION_WAKEUP - fires every morning at the specified time
 *
 */
class WakeUpReceiver:BroadcastReceiver(){

    companion object {

        const val MAX_NOTIFICATION_LINE_LENGTH = 23

        /**
         * When this receiver has an intent with a type ACTION_ONBOARD
         * it means that it should execute in a manner in line with on-boarding
         * the user. That is, generate a notification with the user's first task
         */
        const val ACTION_ONBOARD = "W0"

        /**
         * When the receiver has an intent with a type ACTION_DEFAULT, it
         * means that the receiver should process the intent normally.
         * I.e. schedule tasks as normal
         */
        const val ACTION_DEFAULT  = "W1"

        const val KEY_ALARM = "A"

        const val WAKE_UP_INTENT_CODE = 1


    }

    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("onReceive","Woken up...")
        
        intent?.let {

            if (intent.hasExtra(KEY_ALARM)) {
                Log.d("onReceive","By Alarm")
            }
            
            when (it.action) {

                ACTION_ONBOARD ->{

                    Log.d("onReceive","ACTION_ONBOARD!")
                    getNotificationManager(context).apply {
                        //TODO ENSURE 1.0+ compatibility, right now it only works on 2.0

                        val wakeUpNotificationId = context.resources.getInteger(R.integer.wakeup_notification_id)

                        /** Prepare the intent for when user decides click Open (Wear) or the notification (Phone) **/
                        val mainIntent = Intent(context, OnboardActivity::class.java)
                        mainIntent.action = OnboardActivity.ACTION_TEST_WAKE_UP
                        val mainPendingIntent = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_ONE_SHOT)

                        /** Prepare the intent for when user dismisses the notification **/
                     /*   val dismissIntent = Intent(context, NotificationDismissReceiver::class.java)
                        //TODO obfuscate strings later using obfuscation library
                        dismissIntent.putExtra("com.gorillamoa.routines.notificationId",wakeUpNotificationId)
                        val dismissPendingIntent = PendingIntent.getBroadcast(context.applicationContext, 22, dismissIntent, PendingIntent.FLAG_ONE_SHOT)*/

                        //TODO we need to have a task retriever method
                        val stringBuilder = StringBuilder()
                        buildLine("Drink Water","1cup",stringBuilder)
//                        buildEndLine(20,stringBuilder)

                        val bigTextStyle = Notification.BigTextStyle()
                                .setBigContentTitle(Html.fromHtml("Today's tasks &#128170;",Html.FROM_HTML_MODE_COMPACT))
                                .bigText(Html.fromHtml(stringBuilder.toString(),Html.FROM_HTML_SEPARATOR_LINE_BREAK_LIST))

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
//                                .setDeleteIntent(dismissPendingIntent)


                        notify(context.resources.getString(R.string.notification_tag),wakeUpNotificationId, mainBuilder.build())

                    }
                }

                ACTION_DEFAULT ->{
                    
                    Log.d("onReceive","ACTION_DEFAULT")
                    getNotificationManager(context).apply {

                        //TODO ENSURE 1.0+ compatibility, right now it only works on 2.0

                        val wakeUpNotificationId = context.resources.getInteger(R.integer.wakeup_notification_id)

                        /** Prepare the intent for when user decides click Open (Wear) or the notification (Phone) **/
                        val mainIntent = Intent(context, OnboardActivity::class.java)
                        val mainPendingIntent = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT)

                        /** Prepare the intent for when user dismisses the notification **/
                    /*    val dismissIntent = Intent(context, NotificationDismissReceiver::class.java)
                        //TODO obfuscate strings later using obfuscation library
                        dismissIntent.putExtra("com.gorillamoa.routines.notificationId",wakeUpNotificationId)
                        val dismissPendingIntent = PendingIntent.getBroadcast(context.applicationContext, 22, dismissIntent, PendingIntent.FLAG_ONE_SHOT)*/

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
//                                .setDeleteIntent(dismissPendingIntent)

                        notify(context.resources.getString(R.string.notification_tag),wakeUpNotificationId, mainBuilder.build())
                    }

                }
                else ->{

                    throw Exception("This wake up alarm did not receive instructions!")
                    //TODO create a notification that something went wrong
                }
            }
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