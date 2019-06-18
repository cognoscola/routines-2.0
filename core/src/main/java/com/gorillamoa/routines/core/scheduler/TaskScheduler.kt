package com.gorillamoa.routines.core.scheduler

import android.content.Context
import android.util.Log
import com.gorillamoa.routines.core.extensions.DisableScheduler
import com.gorillamoa.routines.core.extensions.EnableScheduler
import com.gorillamoa.routines.core.extensions.createNotificationDeleteIntentForTask
import com.gorillamoa.routines.core.extensions.getCompletedTaskList
import com.gorillamoa.routines.core.extensions.getDataRepository
import com.gorillamoa.routines.core.extensions.getDayTaskList
import com.gorillamoa.routines.core.extensions.getSavedOrder
import com.gorillamoa.routines.core.extensions.saveCompletedTaskList
import com.gorillamoa.routines.core.extensions.saveOrder
import com.gorillamoa.routines.core.extensions.saveTaskList
import com.gorillamoa.routines.core.extensions.saveTaskLists
import com.gorillamoa.routines.core.extensions.stringifyTasks
import com.gorillamoa.routines.core.data.Task
import com.gorillamoa.routines.core.extensions.*

import java.lang.StringBuilder
import java.util.*

/**
 * Is in charge of scheduling tasks. Scheduling usually
 * happens once at the beginning of the day during the Wake up alarm.
 *
 * Steps for Scheduling:
 *
 * 1. Reset the day. - sets everything to 0
 * 1. Fetch And schedule a few tasks
 * 2. Receive approval or changes by user during the Wake Up Notification
 * 3. Maintain Order throughout the day
 * 4. If the user wishes it, they can shift around the schedule throughout the day by scheduling
 * individual tasks as they come up.
 * 5. Sleep notification comes up if - user finishes all tasks, or sleep alarm goes off
 *
 */
class TaskScheduler{


    //TODO account for day's progress. For now we'll just try to see # of tasks completed
    //TODO reset at the wake up alarm
    companion object {
        @Suppress("unused")
        private val tag:String = TaskScheduler::class.java.name

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
            com.gorillamoa.routines.core.coroutines.Coroutines.ioThenMain({repository.getTasks()})
            { taskList ->

                //We'll need to record the order so that we can fetch these scheduled tasks
                //throughout the day, for now just place them in the order that they appear
                //TODO FETCH some habits
                //TODO FETCH Some goals
                val queue = ArrayDeque<Int>()
                val orderList = ArrayList<Int>()
                //save the empty list to the Completed list

                taskList?.forEach {
                    queue.push(it.id)
                    orderList.add(it.id!!)
                }

                context.saveTaskLists(queue,ArrayDeque())
                context.saveOrder(orderList)

                //TODO wait for approval of user
                context.EnableScheduler()
                scheduleCallback.invoke(StringBuilder().stringifyTasks(taskList))
            }
        }

        fun isComplete(context: Context, tid: Int):Boolean {

            val completedList = context.getCompletedTaskList()
            return completedList.contains(tid)
        }

        /**
         * Unschedule a specified Task.
         * @param tid is the task id
         */
        fun unscheduleTask(context:Context, tid:Int){


            val unfinished = context.getDayTaskList()
            if (unfinished.contains(tid)) {
                unfinished.remove(tid)
                context.saveTaskList(unfinished)
            }

            val finished = context.getCompletedTaskList()
            if (finished.contains(tid)) {
                finished.remove(tid)
                context.saveCompletedTaskList(finished)
            }

            val order = context.getSavedOrder()
            if (order.contains(tid)) {
                order.remove(tid)
                context.saveOrder(order)
            }


        }

        /**
         * Schedule a specific task indicated by the user
         * @param tid is the task id
         */
        fun scheduleTask(context:Context, tid:Int){

            //TODO determine where to put this (which order)
            //for now just put it at the end
            context.apply {
                val unfinished = getDayTaskList()
                unfinished.add(tid)
                saveTaskList(unfinished)

                val order = getSavedOrder()
                order.add(tid)
                saveOrder(order)
            }
        }

        /**
         * The user has approved the schedule and so now we can begin assigning tasks
         */
        fun approve(context:Context){
//            val taskList = context.getDayTaskList()

        }

        //TODO come up with a scheme to schedule into the future
        fun scheduleIntoUnknownFuture(){

        }

        /**
         * Schedule the current visible task for another day.
         * We don't know when to schedule yet but for now we'll just remove from the
         * list
         * @return true is we should fetch another task
         */
        fun scheduleForNextAvailableDay(context:Context, tid:Int):Boolean{

            var shouldFetch = false
            val taskList = context.getDayTaskList()
            if (taskList.contains(tid)) {

                if (tid == taskList.last) {
                    shouldFetch = true
                }

                if (taskList.removeFirstOccurrence(tid)) {
                    context.saveTaskList(taskList)
                }
            }

            return shouldFetch
        }

        /**
         * Reschedule the task a few hours into the future
         * @param context is the application context
         * @param tid is the task id
         * @param count how many tasks ahead should we reschedule
         *
         */
        //TODO determine if we should skip all the way back or just a few tasks
        fun scheduleNTasksForward(context: Context, tid:Int,steps:Int):Boolean{

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

                    Log.d("scheduleNTasksForward","Checking TID:$tid vs last:${taskList.last}")
                    //if false it usually means two intents went off! Both attempted to schedule
                    if (tid == taskList.last) {

                        //for now lets just move the 2nd one to the front

                        var skipSteps = Math.min(taskList.size, steps)
                        val tempArray = ArrayDeque<Int>(skipSteps)

                        val current = taskList.removeLast()

                        while (skipSteps != 0) {

                            tempArray.add(taskList.removeLast())
                            skipSteps--
                        }

                        taskList.add(current)

                        while (tempArray.isNotEmpty()) {
                            taskList.add(tempArray.removeLast())
                        }

                        context.saveTaskList(taskList)
                        return true
                    }
                    return false
                }
                return false
            }
            return false
        }

        /**
         * User may wish to mark a task as uncompleted for whatever reasons
         * @param context is the application context
         * @param tid is the task id
         * @return wether the operation was succesfful or not
         */
        fun uncompleteTask(context:Context, tid:Int):Boolean{

            if (tid == -1) {
                return false// nothing to do here
            }

            val taskList = context.getDayTaskList()
            val doneList = context.getCompletedTaskList()

            Log.d("uncompleteTask","Before")
            Log.d("uncompleteTask","Unfinished: ${taskList.joinToString(",")}")
            Log.d("uncompleteTask","finished: ${doneList.joinToString(",")}")

            if (tid != -1) {
                //we'll fetch the next tid from prefs
                if(doneList.removeFirstOccurrence(tid)){

                    //TODO retain the original order so that we know when to add this task
                    taskList.add(tid)
                    context.saveTaskLists(taskList,doneList)
                    return true
                }
                //TODO update the task history in the DB
            }else {return false}
            return false
        }

        /**
         * We finished the task
         * @param context is the application context
         * @param tid is the id of the task completed
         */
        fun completeTask(context: Context, tid: Int):Boolean {

            val taskList = context.getDayTaskList()
            val doneList = context.getCompletedTaskList()

            if (tid != -1) {
                //we'll fetch the next tid from prefs
                if(taskList.removeFirstOccurrence(tid)){

                    doneList.add(tid)
                    context.saveTaskLists(taskList,doneList)
                    return true
                }
                //TODO update the task history in the DB
            }else {return false}
            return false
        }

        /**
         * Returns the previous task in the scheduled day. If the current Task is the first of the day
         * the call will return the last scheduled task
         */
        fun getPreviousOrderedTask(context: Context, currentTid: Int,schedulerCallback: (task: Task?) -> Any?){

            if (currentTid == 0) {
                getNextUncompletedTask(context, schedulerCallback)
                return
            }
            val order = context.getSavedOrder()

            if (order.size > 0) {

                val position = order.indexOf(currentTid)

                val nextPosition:Int = if (position == 0) {
                    order.size - 1
                }else{
                    position -1
                }

                com.gorillamoa.routines.core.coroutines.Coroutines.ioThenMain({context.getDataRepository().getTaskById(order[nextPosition])}){
                    schedulerCallback.invoke(it)
                }

            }else{
                //we don't have a schedule yet so just pass back nothing
                //TODO prevent user from calling this function when there isn't anny task scheduled
                schedulerCallback.invoke(null)
            }
        }

        /**
         * Return the next task in our scheduled set. If the task is last it will return the first
         * task of the day
         */
        fun getNextOrderedTask(context: Context,currentTid:Int, schedulerCallback:(task:Task?) ->Any?){

            if (currentTid == 0) {
                getNextUncompletedTask(context, schedulerCallback)
                return
            }
            val order = context.getSavedOrder()
            if (order.size > 0) {
                val position = order.indexOf(currentTid)
                val nextPosition:Int = if (position == (order.size - 1)) {
                    0
                }else{
                    position + 1
                }

                com.gorillamoa.routines.core.coroutines.Coroutines.ioThenMain({context.getDataRepository().getTaskById(order[nextPosition])}){

                    schedulerCallback.invoke(it)
                }

            }else{
                //we don't have a schedule yet so just pass back nothing
                //TODO prevent user from calling this function when there isn't anny task scheduled
                schedulerCallback.invoke(null)
            }

        }


        /**
         * Will fetch the next task which the scheduler thinks should be fetched
         * @param context is the application context
         * @param currentTid is the current task id (which the user is currently doing
         * @param scheduleCallback is the call back function to fetch the next task
         */
        fun getNextUncompletedTask(context:Context, scheduleCallback: (task:Task?) -> Any?){

            var nextTid:Int =-1
            val taskList = context.getDayTaskList()

            //always fetch from the end of the list
            if (taskList.size >= 1) {
                nextTid = taskList.last
            }

            if (nextTid != -1) {

                val repository = context.getDataRepository()
                Log.d("getNextUncompletedTask","Will show Task:$nextTid")
                com.gorillamoa.routines.core.coroutines.Coroutines.ioThenMain({repository.getTaskById(nextTid)}){
                    scheduleCallback.invoke(it)
                }
            }else{

                Log.d("getNextUncompletedTask","Out of tasks!")
                //TODO check if any tasks were completed, if not don't show sleep notification
                    scheduleCallback.invoke(null)
                //TODO schedule alarm at some point S
            }
        }

        /**
         * The user may finish all his tasks, or they finish the day despite completing
         * all tasks. Its time to remove any lingering scheduled tasks
         */
        //TODO give user one more chance to finish a task

        fun endDay(context: Context){
            Log.d("endDay","The day is over")
            context.DisableScheduler()


            val taskList = context.getDayTaskList()
            while (taskList.size > 0) {
                taskList.remove()
                //TODO DON"T CLEAR THIS LIST, instead use another flag to tell when we're scheduled or not
                //call it a isScheduled Flag
                context.saveTaskList(taskList)
            }

            //TODO save statistics
            //TODO dismiss notifications
        }

        fun showNext(context:Context){
            getNextUncompletedTask(context) { task ->

                task?.let {
                    context.showMobileNotificationTask(task)
                    //first time using this notation, so just to clarify. Since task was null the
                    //commands on the right side of the elvis (?:) notation was executed

                } ?: run {
                    context.notificationShowSleep()
                    endDay(context)
                }
            }
        }

        fun skipAndShowNext(context: Context, currentTid: Int) {
            if (scheduleNTasksForward(context, currentTid, 2)) {
                showNext(context)
            }
        }
    }
}