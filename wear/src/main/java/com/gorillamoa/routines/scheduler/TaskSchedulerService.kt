package com.gorillamoa.routines.scheduler

import android.content.Context
import com.gorillamoa.routines.coroutines.Coroutines
import com.gorillamoa.routines.data.Task

import com.gorillamoa.routines.extensions.getDataRepository
import com.gorillamoa.routines.extensions.saveTaskList
import com.gorillamoa.routines.extensions.stringifyTasks
import java.lang.StringBuilder
import java.util.*

/**
 * Is in charge of scheduling tasks. Scheduling usually
 * happens once at the beginning of the day during the Wake up alarm.
 *
 * Steps for Scheduling:
 *
 * 1. Fetch And schedule a few tasks
 * 2. Receive approval or changes by user during the Wake Up Notification
 * 3. Maintain Order throughout the day
 *
 * 4. If the user wishes it, they can shift around the schedule throughout the day
 *
 */
class TaskScheduler{


    /**
     * Has the scheduler already scheduled tasks?
     */
    private enum class State {

        Unschedule, //the scheduler has not scheduled anything
        Scheduled, //the scheduler has scheduled for the dayy
    }

    companion object {

        /**
         * Using the context, we'll fetch task data
         * and return the string version of it.
         * @param context is the application context
         * @param scheduleCallback is a function to execute since we require
         * fetching from a database, we need to asynchronously execute this function
         *
         * TaskString - is the list of tasks for the day as a string
         * tid - is the first task to start
         */
        fun schedule(context: Context, scheduleCallback: (taskString: String, tid:Int)->Any){

            val repository = context.getDataRepository()


            //For now we'll get all tasks
            Coroutines.ioThenMain({repository.getTasks()})
            { taskList ->

                //We'll need to record the order so that we can fetch these scheduled tasks
                //throughout the day

                val queue = ArrayDeque<Int>()

                taskList?.forEach {
                    queue.push(it.id)
                }
                context.saveTaskList(queue)
                scheduleCallback.invoke(StringBuilder().stringifyTasks(taskList),queue.first)
            }
        }

        fun getTask(context:Context, tid:Int, scheduleCallback: (task:Task?) -> Any?){


            val repository = context.getDataRepository()
            Coroutines.ioThenMain({repository.getTaskById(tid)}){
                scheduleCallback.invoke(it)
            }
        }
    }
}
