package com.gorillamoa.routines.data

import androidx.lifecycle.LiveData
import androidx.annotation.WorkerThread


//TODO decide wether to fetch from network or not.
/**
 * In the future we'll use this to synchronize
 * data between our local data and network data.
 * For now its just a layer that does nothing.
 */
class TaskRepository(private val taskdao:TaskDao){

    @WorkerThread
     fun getTasks():List<Task>{
        return taskdao.getTasks()
    }

    @WorkerThread
    fun insert(task: Task) {
        taskdao.insertTask(task)
    }

    @WorkerThread
    fun clearAll(){
        taskdao.clearAll()
    }
}