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

        //clean comment
        /**
         * Marks the task as completed
         */
        const val ACTION_DONE = "task.action.done"

        const val ACTION_SKIP_SHORT = "task.skip.short"
        const val ACTION_SKIP_TODAY = "task.skip.today"
        const val ACTION_INTO_FUTURE = "task.skip.long"
        
        /**
         * Display the next task
         */
        const val ACTION_TASK_NEXT = "task.action.next"
        
        /**
         * Displays the previous task for today
         */
         const val ACTION_TASK_PREVIOUS = "task.action.previous"

        /**
         * Set the task is NOT completed
         */
        const val ACTION_TASK_UNCOMPLETE = "task.action.uncomplete"

        const val ACTION_START_DAY = "wakeup.start"
        const val ACTION_START_MODIFY = "wakeup.modify"

        const val ACTION_EDIT_ORDER = "edit.sort"
        const val ACTION_EDIT_SCHEDULE = "edit.schedule"

    }

    override fun onReceive(context: Context, intent: Intent?) {

        intent?.let {

            val currentTid = intent.getIntExtra(TASK_ID,-1)

            Log.d("$tag onReceive","We received.. at least")

            when (intent.action) {
                ACTION_DONE -> {
                    Log.d("onReceive","ACTION DONE")
                    //mark the task as done.
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
                }

                ACTION_TASK_UNCOMPLETE ->{

                    if (TaskScheduler.uncompleteTask(context, currentTid)) {

                        val task = context.getTaskFromString(intent.getStringExtra(TASK_DATA))
                        context.showMobileNotificationTask(task)

                    }else{

                    }
                }

                ACTION_SKIP_SHORT ->{

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
                }

                ACTION_SKIP_TODAY ->{

                    if (TaskScheduler.scheduleForNextAvailableDay(context, currentTid)) {
                        TaskScheduler.showNext(context)
                    }else{
                        Log.d("onReceive","ACTION_SKIP_TODAY")
                    }
                }

                ACTION_START_DAY->{

                    //TODO UNCOMMENT BOOLEAN CHECK
                    context.apply {
//                        if (context.isNotificationStubborn()) {
                            getNotificationManager().cancel(NOTIFICATION_TAG, WAKE_UP_NOTIFICATION_ID)
//                    }

                            Toast.makeText(this@apply,"Start day",Toast.LENGTH_SHORT).show()
                            TaskScheduler.approve(this@apply)
                            TaskScheduler.getNextUncompletedTask(this@apply) { task ->

                                task?.let {

                                    //Prepare our small remote view
                                    val smallRemoteView = context.getSmallTaskRemoteView(task)

                                    context.notificationShowTask(
                                            task,
                                            dismissPendingIntent = createNotificationDeleteIntentForTask(task.id!!),
                                            //TODO USE stubborn check
                                            dismissable = false,
                                            smallRemoteView = smallRemoteView,
                                            bigRemoteView = null
                                    )
                                }
                            }
                    }
                }

                ACTION_START_MODIFY ->{


                }

                ACTION_TASK_NEXT -> {

                    Log.d("$tag onReceive", "ACTION_NEXT")



                    TaskScheduler.getNextOrderedTask(context, currentTid) { task ->
                        task?.let {
                            context.getNotificationManager().cancel(NOTIFICATION_TAG, currentTid)
                            context.showMobileNotificationTask(task)
                        }
                    }
                }

                ACTION_TASK_PREVIOUS ->{

                    Log.d("$tag onReceive","ACTION_PREVIOUS")
                    TaskScheduler.getPreviousOrderedTask(context, currentTid) { task ->

                        task?.let {
                            context.getNotificationManager().cancel(NOTIFICATION_TAG, currentTid)
                            context.showMobileNotificationTask(task)
                        }
                    }
                    //TODO What happens when TID Is -1?
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



