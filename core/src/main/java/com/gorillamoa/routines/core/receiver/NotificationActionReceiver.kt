package com.gorillamoa.routines.core.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import com.gorillamoa.routines.core.extensions.*
import com.gorillamoa.routines.core.scheduler.TaskScheduler

class NotificationActionReceiver:BroadcastReceiver(){
    @Suppress("unused")
    private val tag:String = NotificationActionReceiver::class.java.name

    companion object {

        //clean comment
        const val ACTION_DONE = "task.done"

        const val ACTION_SKIP_SHORT = "task.skip.short"
        const val ACTION_SKIP_TODAY = "task.skip.today"
        const val ACTION_INTO_FUTURE = "task.skip.long"

        const val ACTION_START_DAY = "wakeup.start"
        const val ACTION_START_MODIFY = "wakeup.modify"

        const val ACTION_EDIT_ORDER = "edit.sort"
        const val ACTION_EDIT_SCHEDULE = "edit.schedule"
    }

    override fun onReceive(context: Context, intent: Intent?) {

        intent?.let {

            val tid = intent.getIntExtra(com.gorillamoa.routines.core.extensions.TASK_ID,-1)

            Log.d("$tag onReceive","We received.. at least")

            when (intent.action) {
                ACTION_DONE -> {
                    Log.d("onReceive","ACTION DONE")
                    //mark the task as done.
                    if (TaskScheduler.completeTask(context, tid)) {
                        TaskScheduler.showNext(context)
                    }else{
                        Log.e("onReceive","Something wont wrong Completeing task! UH OH")
                    }
                }

                ACTION_SKIP_SHORT ->{
                    Log.d("schedule:SKIP","receiver got id ${tid}")
                    TaskScheduler.skipAndShowNext(context,tid)

                    if (TaskScheduler.scheduleNTasksForward(context, tid, 2)) {
                        TaskScheduler.showNext(context)
                    }else{
                        Log.d("onReceive","ACTION_SKIP_SHORT")
                    }
                }

                ACTION_SKIP_TODAY ->{

                    if (TaskScheduler.scheduleForNextAvailableDay(context, tid)) {
                        TaskScheduler.showNext(context)
                    }else{
                        Log.d("onReceive","ACTION_SKIP_TODAY")
                    }
                }

                ACTION_START_DAY->{


                    //TODO UNCOMMENT BOOLEAN CHECK

//                    if (context.isNotificationStubborn()) {
                        context.getNotificationManager().cancel(NOTIFICATION_TAG, WAKE_UP_NOTIFICATION_ID)
//                    }

                    Toast.makeText(context,"Start day",Toast.LENGTH_SHORT).show()
                    TaskScheduler.approve(context)
                    TaskScheduler.getNextUncompletedTask(context) { task ->

                        task?.let {

                            context.notificationShowTask(
                                    task,
                                    dimissPendingIntent = context.createNotificationDeleteIntentForTask(task.id!!),
                                    dismissable = false
                            )
                        }
                    }
                }

                ACTION_START_MODIFY ->{


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


