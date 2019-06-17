package com.gorillamoa.routines.core.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.gorillamoa.routines.core.data.Task

import com.gorillamoa.routines.core.extensions.*
import com.gorillamoa.routines.core.scheduler.TaskScheduler
import com.gorillamoa.routines.core.views.RemoteInjectorHelper

class NotificationActionReceiver:BroadcastReceiver(){
    @Suppress("unused")
    private val tag:String = NotificationActionReceiver::class.java.name

    companion object {

        //clean comment
        const val ACTION_DONE = "task.done"

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

                    //Dismiss the notification
                    //TODO stubborn check
                    context.getNotificationManager().cancel(NOTIFICATION_TAG,tid)


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
                    context.apply {
//                        if (context.isNotificationStubborn()) {
                            getNotificationManager().cancel(NOTIFICATION_TAG, WAKE_UP_NOTIFICATION_ID)
//                    }

                            Toast.makeText(this@apply,"Start day",Toast.LENGTH_SHORT).show()
                            TaskScheduler.approve(this@apply)
                            TaskScheduler.getNextUncompletedTask(this@apply) { task ->

                                task?.let {

                                    //Prepare our small remote view
                                    val smallRemoteView = (context.applicationContext as RemoteInjectorHelper.RemoteGraphProvider).remoteViewGraph.getSmallTaskRemoteView(task)

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

//
                }

                ACTION_START_MODIFY ->{


                }

                ACTION_TASK_NEXT ->{

                    TaskScheduler.getNextOrderedTask(context, tid) {task ->

                    Toast.makeText(context,"NEXT:${task!!.id}",Toast.LENGTH_SHORT).show()
                            task.let { showMobileNotificationTask(context,task) }

                    }
                }

                ACTION_TASK_PREVIOUS ->{

                    TaskScheduler.getPreviousOrderedTask(context, tid) { task ->
                        Toast.makeText(context,"PREVIOUS:${task!!.id}",Toast.LENGTH_SHORT).show()
                        task.let { showMobileNotificationTask(context,task) }
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

    private fun showMobileNotificationTask(context:Context, task: Task){
        context.apply {

            getNotificationManager().cancel(NOTIFICATION_TAG, WAKE_UP_NOTIFICATION_ID)
            val smallRemoteView = (context.applicationContext as RemoteInjectorHelper.RemoteGraphProvider).remoteViewGraph.getSmallTaskRemoteView(task)

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



