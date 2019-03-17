package com.gorillamoa.routines.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData

import com.gorillamoa.routines.data.Task
import com.gorillamoa.routines.data.TaskDatabase
import com.gorillamoa.routines.data.TaskRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * This is how UI will interact with our application data (aka our tasks)
 */
class TaskViewModel(application: Application): AndroidViewModel(application){

    private var parentJob = Job()
    private val coroutineContext:CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)
    private val repository:TaskRepository

    val allTasks: LiveData<List<Task>>

    init{
        val taskDao = TaskDatabase.getDatabase(application,scope).taskDao()
        repository = TaskRepository(taskDao)
        allTasks = repository.allTasks
    }

    fun insert(task:Task) = scope.launch(Dispatchers.IO){
        repository.insert(task)
    }

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }

}