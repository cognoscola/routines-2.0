package com.gorillamoa.routines.data

import android.arch.lifecycle.LiveData
import androidx.annotation.WorkerThread


//TODO decide wether to fetch from network or not.
class TaskRepository(private val taskdao:TaskDao){

    val allTasks: LiveData<List<Task>> = taskdao.getTasks()

    @WorkerThread
    suspend fun insert(task: Task) {
        taskdao.insertTask(task)
    }
}