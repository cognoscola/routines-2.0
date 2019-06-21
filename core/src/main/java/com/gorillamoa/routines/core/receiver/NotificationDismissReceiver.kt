package com.gorillamoa.routines.core.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.gorillamoa.routines.core.extensions.notificationDismissWakeUpRemote
import com.gorillamoa.routines.core.extensions.notificationDissmissWakeUp

import com.gorillamoa.routines.core.scheduler.TaskScheduler


/**
 * What action should we take when a notification is dismissed?
 * This class determines that
 */
class NotificationDismissReceiver:BroadcastReceiver() {

    companion object {
        /**
         * If a notification is dismissed, it could be for a variety of reasons.
         * First, we need to consider what type of notification was dismissed.
         *
         * Types:
         *
         * NOTIFICATION_TYPE_WAKEUP - the user receives at beginning of each day. */
        const val TYPE_WAKE_UP = "wakeup"
        /**
         * NOTIFICATION_TYPE_TASK - the user receives when a new task is present */
        const val TYPE_TASK = "task"
        /**
         *
         * NOTIFICATION_TYPE_SLEEP - the user receives when its time to end the day. */
         const val TYPE_SLEEP = "sleep"
         /**
         * Next we should consider why the notification dismissed:
         * - the user clicked on the notification, and it went into the app.
         * - the user swiped, if so, why did they swipe?
         *
         */
    }

    override fun onReceive(context: Context, intent: Intent?) {

        intent?.let {

            val tid = it.getIntExtra(com.gorillamoa.routines.core.extensions.TASK_ID,-1)

            when (intent.action) {
                TYPE_SLEEP -> {
                    Log.d("onReceive","Sleep Dismissal")
                }
                TYPE_TASK -> {
                    Log.d("schedule:Dismiss","Task Dismissal")
                    //we dismissed a task. We'll fetch a future task, while pushing the task back

                    TaskScheduler.skipAndShowNext(context,tid)
                }

                TYPE_WAKE_UP -> {
                    Log.d("onReceive","Wake Up Dismissal")

                    //Dismiss Any Remote Notifications
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