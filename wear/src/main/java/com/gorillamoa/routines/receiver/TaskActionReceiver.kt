package com.gorillamoa.routines.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.gorillamoa.routines.extensions.TASK_ID
import com.gorillamoa.routines.extensions.createNotificationDeleteIntentForTask
import com.gorillamoa.routines.extensions.notificationShowSleep
import com.gorillamoa.routines.extensions.notificationShowTask
import com.gorillamoa.routines.scheduler.TaskScheduler

class TaskActionReceiver:BroadcastReceiver(){
    companion object {

        //clean comment
        const val ACTION_DONE = "task.done"

        const val ACTION_SKIP_TODAY = "task.skiptoday"

        const val ACTION_INTO_FUTURE = "task.future"
    }

    override fun onReceive(context: Context, intent: Intent?) {

        intent?.let {

            val tid = intent.getIntExtra(TASK_ID,-1)

            when (intent.action) {
                ACTION_DONE -> {
                    Log.d("onReceive","ACTION DONE")
                    //mark the task as done.
                    TaskScheduler.completeTask(context,tid)
                    TaskScheduler.getNextTask(context){ task ->

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
                else -> {
                    Log.d("onReceive","Unknown Action on Task ${intent.getIntExtra(TASK_ID,-1)}")
                }
            }
        }
    }
}