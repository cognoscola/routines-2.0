package com.gorillamoa.routines.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.gorillamoa.routines.extensions.*
import com.gorillamoa.routines.scheduler.TaskScheduler


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

            val tid = it.getIntExtra(TASK_ID,-1)

            when (intent.action) {
                TYPE_SLEEP -> {
                    Log.d("onReceive","Sleep Dismissal")
                }
                TYPE_TASK -> {
                    Log.d("onReceive","Task Dismissal")
                    //we dismissed a task. We'll fetch a future task, while pushing the task back

                    //TODO come up with algorithm to determine how far into future to schedule ahead, for now just 2
                    TaskScheduler.scheduleNTasksForward(context,tid,2)
                    TaskScheduler.getNextTask(context,tid){ task ->

                        task?.let {
                            context.notificationShowTask(
                                    it,
                                    dimissPendingIntent = context.createNotificationDeleteIntentForTask(task.id!!)
                            )

                            //first time using this notation, so just to clarify. Since task was null the
                            //commands on the right side of the elvis (?:) notation was executed

                        } ?: run{
                            context.notificationShowSleep()
                            TaskScheduler.endDay(context)
                        }
                    }
                }
                TYPE_WAKE_UP -> {
                    Log.d("onReceive","Wake Up Dismissal")

                    //TODO we automatically approve the schedule on dismissal of notification, make this optional
                    if (context.isReadyToApprove()) {
                        TaskScheduler.approve(context)
                    }

                    TaskScheduler.getNextTask(context,-1){ task ->
                        task?.let {
                            context.notificationShowTask(
                                    it,
                                    dimissPendingIntent = context.createNotificationDeleteIntentForTask(task.id!!)
                            )
                        }
                    }
                }
                else ->{
                    Log.d("onReceive","Unknown Dimissal Type")
                }
            }
        }
    }
}