package com.gorillamoa.routines.core.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

import com.gorillamoa.routines.core.extensions.*
import com.gorillamoa.routines.core.scheduler.TaskScheduler
import com.gorillamoa.routines.core.services.DataLayerListenerService

class NotificationActionReceiver:BroadcastReceiver(){
    @Suppress("unused")
    private val tag:String = NotificationActionReceiver::class.java.name

    companion object {

        const val ACTION_SKIP_SHORT = "task.short"
        const val ACTION_SKIP_TODAY = "task.skiptoday"
        const val ACTION_INTO_FUTURE = "task.skiplong"

        const val ACTION_TASK_NEXT = "task.next"
        const val ACTION_TASK_PREVIOUS = "task.previous"
        const val ACTION_TASK_UNCOMPLETE = "task.uncomplete"
        const val ACTION_DONE = "task.done"

        const val ACTION_WAKE_START_DAY = "wake.startday"
        const val ACTION_START_MODIFY = "wakeup.modify"

        const val ACTION_EDIT_ORDER = "edit.sort"
        const val ACTION_EDIT_SCHEDULE = "edit.schedule"

    }

    override fun onReceive(context: Context, intent: Intent?) {

        intent?.let {

            val currentTid = intent.getLongExtra(TASK_ID,-1)

            Log.d("$tag onReceive","${intent.action}")

            when (intent.action) {
                ACTION_DONE -> {
                    //mark the task as done.
                    //TODO DONE FUNCTION
                    if (TaskScheduler.completeTaskMirror(context, currentTid)) {
                        val task = context.getTaskFromString(intent.getStringExtra(TASK_DATA))

                        //TODO SHOW Sleep once we complete the last task
                        if (TaskScheduler.isDayComplete(context)) {

                            context.notificationShowSleepMirror()

                        }else{
                            //we'll just show the same TASK if we're not done the day
                            context.notificationShowTaskMirror(task)
                        }
                        //TODO MAKE THIS OPTIONAL
//                        TaskScheduler.showNext(context)
                    }else{
                        Log.e("onReceive","Something wont wrong Completeing task! UH OH")
                    }
                }

                ACTION_TASK_UNCOMPLETE ->{

                    if (TaskScheduler.uncompleteTaskMirror(context, currentTid)) {
                        val task = context.getTaskFromString(intent.getStringExtra(TASK_DATA))
                        context.notificationShowTaskMirror(task)

                    }else{

                    }
                }

                ACTION_SKIP_SHORT ->{

/*
                    //Dismiss the notification
                    //TODO stubborn check
                    context.getNotificationManager().cancel(NOTIFICATION_TAG,currentTid)

                    Log.d("schedule:SKIP","receiver got id ${currentTid}")
                    TaskScheduler.skipAndShowNext(context,currentTid)

                    if (TaskScheduler.scheduleNTasksForward(context, currentTid, 2)) {
                        TaskScheduler.showNext(context)
                    }else{
                        Log.d("onReceive","ACTION_SKIP_SHORT")
                    }
*/
                }

                ACTION_SKIP_TODAY ->{

                    if (TaskScheduler.scheduleForNextAvailableDay(context, currentTid)) {
                        TaskScheduler.showNext(context)
                    }else{
                        Log.d("onReceive","ACTION_SKIP_TODAY")
                    }
                }
                ACTION_WAKE_START_DAY-> {

                    context.apply {
                        TaskScheduler.approveMirror(context)
                        TaskScheduler.getNextUncompletedTask(this@apply) { task,history ->
                            task?.let {
                                notificationShowTaskMirror(task,history)
                            }
                        }
                    }
                }

                ACTION_START_MODIFY ->{

                }

                ACTION_TASK_NEXT -> {

                    TaskScheduler.getNextOrderedTask(context, currentTid) { task, history ->

/*
                        task?.let {
                            //First dismiss both locally and remotely,
                            //Don't need this anymore, we
                            context.notificationDismissTaskMirror(currentTid)

                            //now lets show a new task on both screens
                            context.notificationShowTaskMirror(task)
                        }
*/
                    }
                }

                ACTION_TASK_PREVIOUS ->{

                    TaskScheduler.getPreviousOrderedTask(context, currentTid) { task, history->

/*
                        task?.let {
                            //First dismiss both locally and remotely
                            context.notificationDismissTaskMirror(currentTid)

                            //now lets show a new task on both screens
                            context.notificationShowTaskMirror(task)

                        }
*/
                    }
                }

                /**
                 * Expand the currently showing notification
                 */
                else -> {
                    Log.d("onReceive","Unknown Action on Task ${intent.getIntExtra(com.gorillamoa.routines.core.extensions.TASK_ID,-1)}")
                }
            }
        }
    }


}



