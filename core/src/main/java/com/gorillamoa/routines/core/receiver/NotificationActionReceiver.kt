package com.gorillamoa.routines.core.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.util.Log
import android.widget.Toast

import com.gorillamoa.routines.core.extensions.*
import com.gorillamoa.routines.core.scheduler.TaskScheduler

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

            val currentTid = intent.getIntExtra(TASK_ID,-1)

            Log.d("$tag onReceive","${intent.action}")

            when (intent.action) {
                ACTION_DONE -> {
                    //mark the task as done.
                    //TODO DONE FUNCTION
/*
                    if (TaskScheduler.completeTask(context, currentTid)) {

                        val task = context.getTaskFromString(intent.getStringExtra(TASK_DATA))

                        context.showMobileNotificationTask(task)
                        //Check if we finish the day
                        if (TaskScheduler.isDayComplete(context)) {

                            //This will either show a next task or a the sleep Notification
                            context.notificationShowSleep(dismissable = false)
                            TaskScheduler.endDay(context)

                            Handler().postDelayed({
                                context.getNotificationManager().cancel(NOTIFICATION_TAG,currentTid)

                            },2000)

                        }else{
                            //we'll just show the same TASK
                        }

                        //TODO MAKE THIS OPTIONAL
//                        TaskScheduler.showNext(context)
                    }else{
                        Log.e("onReceive","Something wont wrong Completeing task! UH OH")
                    }
*/
                }

                ACTION_TASK_UNCOMPLETE ->{

                    //TODO DO THIS
/*
                    if (TaskScheduler.uncompleteTask(context, currentTid)) {

                        val task = context.getTaskFromString(intent.getStringExtra(TASK_DATA))
                        context.showMobileNotificationTask(task)

                    }else{

                    }
*/
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

                        //Always attempt to cancel the wake up notification on all ends
                        if(!context.isWatch()){
                            context.notificationDismissWakeUpRemote()
                        }

                        Toast.makeText(this@apply, "Start day", Toast.LENGTH_SHORT).show()
                        TaskScheduler.approve(this@apply)
                        TaskScheduler.getNextUncompletedTask(this@apply) { task ->
                            task?.let {
                                notificationShowTaskMirror(task)
                            }
                        }
                    }
                }

                ACTION_START_MODIFY ->{

                }

                ACTION_TASK_NEXT -> {

                    TaskScheduler.getNextOrderedTask(context, currentTid) { task ->

                        task?.let {
                            //First dismiss both locally and remotely
                            context.notificationDismissTaskMirror(currentTid)

                            //now lets show a new task on both screens
                            context.notificationShowTaskMirror(task)
                        }
                    }
                }

                ACTION_TASK_PREVIOUS ->{

                    TaskScheduler.getPreviousOrderedTask(context, currentTid) { task ->

                        task?.let {
                            //First dismiss both locally and remotely
                            context.notificationDismissTaskMirror(currentTid)

                            //now lets show a new task on both screens
                            context.notificationShowTaskMirror(task)

                        }
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



