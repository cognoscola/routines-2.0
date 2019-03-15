package com.gorillamoa.routines.receiver

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.gorillamoa.routines.R
import com.gorillamoa.routines.activity.OnboardActivity
import com.gorillamoa.routines.app.getTaskPRovider
import com.gorillamoa.routines.extensions.WAKE_UP_NOTIFICATION_ID


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
     *
     * Next we should consider why the notification dismissed:
     * - the user clicked on the notification, and it went into the app.
     * - the user swiped, if so, why did they swipe?
     *
     *
     */

    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("onReceive","Well... then..")

        val notificationId = intent?.extras?.getInt("com.gorillamoa.routines.notificationId")
        Log.d("onReceive","ID:$notificationId")

        when (notificationId) {

            //NOTIFICATION_TYPE_WAKEUP
            WAKE_UP_NOTIFICATION_ID -> {
                Log.d("onReceive","We have a wakeup notification dismissal")
                makeTaskNotification(context)

            }
        }
    }

    private fun makeTaskNotification(context:Context){

        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).apply {

            //TODO ENSURE 1.0+ compatibility, right now it only works on 2.0

            val taskNotificationId = 22

            /** Prepare the intent for when user decides click Open (Wear) or the notification (Phone) **/
            val mainIntent = Intent(context, OnboardActivity::class.java)
            val mainPendingIntent = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT)

            //FIXME important
//            val mainBuilder = Notification.Builder(context,Notification.)

            //Get the task info
            val taskInfo = context.getTaskPRovider().getNextTask()

            //FIXME
/*
            mainBuilder
//                    .setStyle(bigTextStyle)
                    //TODO show weather in one icon in the notification title
                    .setContentTitle(taskInfo.name)
                    .setSmallIcon(com.gorillamoa.routines.R.mipmap.ic_launcher)
                    //TODO set the auto cancel configurable by the user.
                    .setAutoCancel(true)
                    .setCategory(Notification.CATEGORY_REMINDER)
                    .setContentIntent(mainPendingIntent)
                    //TODO set dismiss intent for a NOTIFICATION_TYPE_TASK
//                    .setDeleteIntent(dismissPendingIntent)
*/

            //FIXME
//            notify(context.resources.getString(R.string.notification_tag),taskNotificationId, mainBuilder.build())
        }
    }
}