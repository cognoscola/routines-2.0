package com.gorillamoa.routines.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.gorillamoa.routines.R


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

        when (notificationId?:0) {

            //NOTIFICATION_TYPE_WAKEUP
            context.resources.getInteger(R.integer.wakeup_notification_id) -> {
                Toast.makeText(context,"First task",Toast.LENGTH_SHORT).show()

            }
        }
    }
}