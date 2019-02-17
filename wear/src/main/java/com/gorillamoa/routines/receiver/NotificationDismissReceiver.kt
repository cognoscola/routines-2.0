package com.gorillamoa.routines.receiver

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.text.Html
import android.util.Log
import com.gorillamoa.routines.R
import com.gorillamoa.routines.activity.OnboardActivity


/**
 * What action should we take when a notification is dismissed?
 * This class determines that
 */
class NotificationDismissReceiver:BroadcastReceiver() {

    /**
     * If a notification is dismissed, it could be for a variety of reasons.
     * First, we need to consider what type of notification was dismissed.
     *
     * Types:
     *
     * NOTIFICATION_TYPE_WAKEUP - the user receives at beginning of each day.
     * NOTIFICATION_TYPE_TASK - the user receives when a new task is present
     * NOTIFICATION_TYPE_SLEEP - the user receives when its time to end the day.
     */

    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("onReceive","Well... then..")

        val notificationId = intent?.extras?.getInt("com.gorillamoa.routines.notificationId")
        Log.d("onReceive","ID:${notificationId}")

        when (notificationId) {

            //NOTIFICATION_TYPE_WAKEUP
            context.resources.getInteger(R.integer.wakeup_notification_id) -> {
                Log.d("onReceive","We have a wakeup dismissal")
                makeTaskNotification(context)

            }
        }
    }

    fun makeTaskNotification(context:Context){

        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).apply {

            //TODO ENSURE 1.0+ compatibility, right now it only works on 2.0

            val taskNotificationId = 22

            /** Prepare the intent for when user decides click Open (Wear) or the notification (Phone) **/
            val mainIntent = Intent(context, OnboardActivity::class.java)
            val mainPendingIntent = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT)

            /** Prepare the intent for when user dismisses the notification **/
//            val dismissIntent = Intent(context, NotificationDismissReceiver::class.java)
            //TODO obfuscate strings later using obfuscation library
//            dismissIntent.putExtra("com.gorillamoa.routines.notificationId",wakeUpNotificationId)
//            val dismissPendingIntent = PendingIntent.getBroadcast(context.applicationContext, 22, dismissIntent, PendingIntent.FLAG_ONE_SHOT)

            //TODO we need to have a task retriever method
//            val stringBuilder = StringBuilder()
//            buildLine("meditate","1hr",stringBuilder)
//            buildLine("Meeting with John again twice","9min",stringBuilder)
//            buildLine("Ultra super short","1reps",stringBuilder)
//            buildLine("Dod","4catches",stringBuilder)
//            buildLine("pick kids up from school","2p",stringBuilder)
//            buildEndLine(20,stringBuilder)

//            val bigTextStyle = Notification.BigTextStyle()
//                    .setBigContentTitle(Html.fromHtml("Today's tasks &#128170;", Html.FROM_HTML_MODE_COMPACT))
//                    .bigText(Html.fromHtml(stringBuilder.toString(), Html.FROM_HTML_SEPARATOR_LINE_BREAK_LIST))
//                    .setSummaryText("+3 more")

            val mainBuilder = Notification.Builder(context,context.resources.getString(R.string.notificationchannel_one))
            mainBuilder
//                    .setStyle(bigTextStyle)
                    //TODO show weather in one icon in the notification title
                    .setContentTitle(Html.fromHtml("Meditate", Html.FROM_HTML_MODE_COMPACT))
                    .setSmallIcon(com.gorillamoa.routines.R.mipmap.ic_launcher)
                    .setContentText("60 min lets see how long we can make the text for this notification, its going to" +
                            "to be pretty long . no doubt about it. Gona take a whole hour to read this " +
                            "notification, but its for testing purposes. soo whatever. it works notification, but its for testing purposes. soo whatever. it works" +
                            "notification, but its for testing purposes. soo whatever. it worksnotification, but its for testing purposes. soo whatever. it works" +
                            "notification, but its for testing purposes. soo whatever. it worksnotification, but its for testing purposes. soo whatever. it works" +
                            "notification, but its for testing purposes. soo whatever. it works" +
                            "notification, but its for testing purposes. soo whatever. it works" +
                            "notification, but its for testing purposes. soo whatever. it works" +
                            "notification, but its for testing purposes. soo whatever. it works" +
                            "notification, but its for testing purposes. soo whatever. it works" +
                            "notification, but its for testing purposes. soo whatever. it worksnotification, but its for testing purposes. soo whatever. it works" +
                            "")
                    .setAutoCancel(true)
                    .setCategory(Notification.CATEGORY_REMINDER)
                    .setContentIntent(mainPendingIntent)
                    //TODO set dismiss intent for a NOTIFICATION_TYPE_TASK
//                    .setDeleteIntent(dismissPendingIntent)

            notify(context.resources.getString(R.string.notification_tag),taskNotificationId, mainBuilder.build())
        }
    }
}