package com.gorillamoa.routines.core.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.gorillamoa.routines.core.extensions.notificationDismissSleepRemote
import com.gorillamoa.routines.core.extensions.notificationDismissWakeUpRemote
import com.gorillamoa.routines.core.extensions.notificationDismissTaskRemote
import com.gorillamoa.routines.core.services.DataLayerListenerService


/**
 * What action should we take when a notification is dismissed?
 * This class determines that
 */
class NotificationDismissReceiver:BroadcastReceiver() {

    @Suppress("unused")
    private val tag:String = NotificationDismissReceiver::class.java.name

    companion object {
        /**
         * If a notification is dismissed, it could be for a variety of reasons.
         * First, we need to consider what type of notification was dismissed.
         *
         * Types:
         *
         * NOTIFICATION_TYPE_WAKEUP - the user receives at beginning of each day. */
        const val TYPE_WAKE_UP = "dismiss.wakeup"
        /**
         * NOTIFICATION_TYPE_TASK - the user receives when a new task is present */
        const val TYPE_TASK = "dismiss.task"
        /**
         *
         * NOTIFICATION_TYPE_SLEEP - the user receives when its time to end the day. */
         const val TYPE_SLEEP = "dismiss.sleep"
         /**
         * Next we should consider why the notification dismissed:
         * - the user clicked on the notification, and it went into the app.
         * - the user swiped, if so, why did they swipe?
         *
         */
    }

    override fun onReceive(context: Context, intent: Intent?) {


        intent?.let {
            Log.d("$tag onReceive",intent.action)

            val tid = it.getLongExtra(com.gorillamoa.routines.core.extensions.TASK_ID,-1)

            when (intent.action) {
                TYPE_SLEEP -> {
                    context.notificationDismissSleepRemote()
                    DataLayerListenerService.endDayMirror(context)
                }
                TYPE_TASK -> {
                    //TODO this behaviour is determined by notification behaviour settings
//                    TaskScheduler.skipAndShowNext(context,tid)

                        context.notificationDismissTaskRemote()
                }

                TYPE_WAKE_UP -> {

                    //Dismiss Remotely if we're not a watch Remote Notifications
                        context.notificationDismissWakeUpRemote()

                    //TODO we automatically approve the schedule on dismissal of notification, make this optional
//                    if (context.EnableScheduler()) {
                    //TODO UNCOMMENT THIS
//                        TaskScheduler.approve(context)
//                    }

//                    DataLayerListenerService.remoteNotifyWakeUpActioned(context)

                    //TODO UNCOMMENT THIS
//                    TaskScheduler.getNextUncompletedTask(context) { task ->

                        //TODO this is bugging out
/*
                        task?.let {

                            context.notificationShowTask(
                                    task,
                                    dismissPendingIntent = context.createNotificationDeleteIntentForTask(task.id!!)
                            )
                        }
*/

//                    }
                }
                else ->{
                    Log.d("onReceive","Unknown Dimissal Type")
                }
            }
        }
    }
}