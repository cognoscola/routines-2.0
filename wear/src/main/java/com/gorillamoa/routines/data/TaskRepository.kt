package com.gorillamoa.routines.data


import android.arch.lifecycle.LiveData
import android.support.annotation.WorkerThread


//TODO decide wether to fetch from network or not.
/**
 * In the future we'll use this to synchronize
 * data between our local data and network data.
 * For now its just a layer that does nothing.
 */
class TaskRepository(private val taskdao:TaskDao){

    val allTasks: LiveData<List<Task>> = taskdao.getTasks()

    @WorkerThread
    suspend fun insert(task: Task) {
        taskdao.insertTask(task)
    }
}