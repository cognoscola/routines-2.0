package com.gorillamoa.routines.core.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.gorillamoa.routines.core.constants.DataLayerConstant.Companion.KEY_TASK_DATA
import com.gorillamoa.routines.core.constants.DataLayerConstant.Companion.KEY_TASK_HISTORY_DATA
import com.gorillamoa.routines.core.coroutines.Coroutines

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

            //TODO SPLIT
            val currentTid = intent.getLongExtra("",-1)
//            val currentTid = intent.getLongExtra(TASK_ID,-1)

            Log.d("$tag onReceive","${intent.action}")

            when (intent.action) {
                ACTION_DONE -> {
                    //mark the task as done.
                    //TODO DONE FUNCTION
                    if (TaskScheduler.completeTaskMirror(context, currentTid)) {
                        val task = context.getTaskFromString(intent.getStringExtra(KEY_TASK_DATA))
                        val lastHistory = context.getHistoryFromString(intent.getStringExtra(KEY_TASK_HISTORY_DATA))

                        //TODO SHOW Sleep once we complete the last task
                        if (TaskScheduler.isDayComplete(context)) {

                            //TODO SPLIT
//                            context.notificationShowSleepMirror()

                        }else{
                            //we'll just show the same TASK if we're not done the day, but make
                            //make sure to get update history information!
                            Coroutines.ioThenMain({context.getDataRepository().getHistoryForTask(task)}){
                                it?.let{
                                    //TODO SPLIT
//                                    context.notificationShowTaskMirror(task,it.lastOrNull())
                                }?:run{
                                    //TODO SPLIT
//                                    context.notificationShowTaskMirror(task,lastHistory)
                                }
                            }
                        }
                    }else{
                        Log.e("onReceive","Something went wrong while completing tasks! UH OH")
                    }
                }

                ACTION_TASK_UNCOMPLETE ->{

                    if (TaskScheduler.uncompleteTaskMirror(context, currentTid)) {
                        val task = context.getTaskFromString(intent.getStringExtra(KEY_TASK_DATA))
                        val history = context.getHistoryFromString(intent.getStringExtra(KEY_TASK_HISTORY_DATA))

                        //TODO SPLIT
                        //context.notificationShowTaskMirror(task,history)

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

                        if (!getOnboardStatus()) {
                            TaskScheduler.approveMirror(context)
                            TaskScheduler.getNextUncompletedTask(this@apply) { task,history ->
                                task?.let {
                                    //TODO SPLIT
//                                    notificationShowTaskMirror(task,history)
                                }
                            }
                        }else{

                            //we go back to the Onboard Page
                            val c = Class.forName("com.gorillamoa.routines.onboard.activities.OnboardActivity")
                            val newIntent = Intent(this, c)
                            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            newIntent.action = "action.wakeup.practise"
                            context.startActivity(newIntent)

                            //we'll close the notification tray
                            context.sendBroadcast(Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
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
                    //TODO SPLIT
//                    Log.d("onReceive","Unknown Action on Task ${intent.getIntExtra(com.gorillamoa.routines.core.extensions.TASK_ID,-1)}")
                }
            }
        }
    }


}



