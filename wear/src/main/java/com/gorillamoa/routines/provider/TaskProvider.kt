package com.gorillamoa.routines.provider

import com.gorillamoa.routines.data.Task
import com.gorillamoa.routines.data.TaskType

/**
 * provides tasks
 */
//TODO Inject a task scheduler
class TaskProvider(){

    /**
     * get the next task for the user to perform
     */
    fun getNextTask() {

//        return Task(1,"type1","task01","description")
        //TODO task scheduler should get the next task

    }

    /**
     * get the task to list at the beginning of the day
     */
    fun getWakeUpTasks(){

        //TODO GET STORED TASKS
        //GET SCHEDULER TO SCHEDULE TASKS FOR THE DAY

    }

}