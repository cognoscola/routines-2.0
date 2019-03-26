package com.gorillamoa.routines.scheduler

import android.content.Context
import android.util.Log
import com.gorillamoa.routines.coroutines.Coroutines
import com.gorillamoa.routines.data.Task
import com.gorillamoa.routines.extensions.*
import java.lang.Exception

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

    //TODO account for day's progress. For now we'll just try to see # of tasks completed
    //TODO reset at the wake up alarm

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
        fun schedule(context: Context, scheduleCallback: (taskString: String)->Any){

            val repository = context.getDataRepository()


            //For now we'll get all tasks
            Coroutines.ioThenMain({repository.getTasks()})
            { taskList ->

                //We'll need to record the order so that we can fetch these scheduled tasks
                //throughout the day, for now just place them in the order that they appear
                //TODO FETCH some habits
                //TODO FETCH Some goals
                val queue = ArrayDeque<Int>()

                taskList?.forEach {
                    queue.push(it.id)
                }
                context.saveTaskList(queue)
                context.setReadyToApprove()
                scheduleCallback.invoke(StringBuilder().stringifyTasks(taskList))
            }
        }

        /**
         * The user has approved the schedule and so now we can begin assigning tasks
         */
        fun approve(context:Context){
            val taskList = context.getDayTaskList()
            context.resetStats(taskList.size)
        }


        /**
         * Reschedule the task a few hours into the future
         * @param context is the application context
         * @param tid is the task id
         *
         */
        //TODO determine if we should skip all the way back or just a few tasks
        fun rescheduleOneTask(context: Context, tid:Int){

            if (tid != -1) {

                val taskList = context.getDayTaskList()
                //does this task exist in the list?
                if (taskList.contains(tid)) {

                    if (taskList.size == 1) {
                        //we can't do anything, we can't skip
                        //TODO only make skipable if there are 2 ore more tasks
                    }

                    //to make things easier for us, WE NEED to assume that
                    //tid is the Last element, if not throw an error

                    if (tid == taskList.last) {

                        //for now lets just move the 2nd one to the front

                        val current = taskList.removeLast()
                        val next = taskList.removeLast()

                        taskList.addLast(current)
                        taskList.addLast(next)

                        context.saveTaskList(taskList)

                    } else {
                        throw Exception("Not last Exception!")
                    }
                }
            }


        }

        fun completeTask(context: Context, tid: Int) {

            val taskList = context.getDayTaskList()

            if (tid != -1) {
                //we'll fetch the next tid from prefs
                if(taskList.removeFirstOccurrence(tid)){

                    context.incrementCompletionCount()
                    context.saveTaskList(taskList)
                }
                //TODO update the task history in the DB
            }
        }


        /**
         * Will fetch the next task which the scheduler thinks should be fetched
         * @param context is the application context
         * @param currentTid is the current task id (which the user is currently doing
         * @param scheduleCallback is the call back function to fetch the next task
         */
        fun getNextTask(context:Context, currentTid:Int, scheduleCallback: (task:Task?) -> Any?){

            var nextTid:Int =-1
            val taskList = context.getDayTaskList()

            //always fetch from the end of the list
            if (taskList.size >= 1) {
                nextTid = taskList.last
            }

            if (nextTid != -1) {

                val repository = context.getDataRepository()
                Log.d("getNextTask","Will show Task:$nextTid")
                Coroutines.ioThenMain({repository.getTaskById(nextTid)}){
                    scheduleCallback.invoke(it)
                }
            }else{

                Log.d("getNextTask","Out of tasks!")
                //TODO check if any tasks were completed, if not don't show sleep notification
                    scheduleCallback.invoke(null)
                //TODO schedule alarm at some point S

            }


        }

        /**
         * The user may finish all his tasks, or they finish the day despite completing
         * all tasks. Its time to reset everything
         */
        fun endDay(context: Context){
            Log.d("endDay","The day is over")
            context.cancelApproval()
        }
    }
}
