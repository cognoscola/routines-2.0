package com.gorillamoa.routines.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.gorillamoa.routines.extensions.TASK_ID
import com.gorillamoa.routines.scheduler.TaskScheduler

class TaskActionReceiver:BroadcastReceiver(){
    companion object {

        //clean comment
        const val ACTION_DONE = "task.done"

        const val ACTION_SKIP_SHORT = "task.skip.short"
        const val ACTION_SKIP_TODAY = "task.skip.today"
        const val ACTION_INTO_FUTURE = "task.skip.long"
    }

    override fun onReceive(context: Context, intent: Intent?) {

        intent?.let {

            val tid = intent.getIntExtra(TASK_ID,-1)

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

                    if (TaskScheduler.scheduleForNextAvaiableDay(context, tid)) {
                        TaskScheduler.showNext(context)
                    }else{
                        Log.d("onReceive","ACTION_SKIP_TODAY")
                    }
                }

                else -> {
                    Log.d("onReceive","Unknown Action on Task ${intent.getIntExtra(TASK_ID,-1)}")
                }
            }
        }
    }
}