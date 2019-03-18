package com.gorillamoa.routines.viewmodel

import android.app.Application
import androidx.annotation.UiThread
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gorillamoa.routines.coroutines.Coroutines

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

  /*  private var parentJob = Job()
    private val coroutineContext:CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)*/



    private val repository:TaskRepository
    private val _tasks = MutableLiveData<List<Task>>()
    val tasks: LiveData<List<Task>> get() = _tasks


    init{

        val taskDao = TaskDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao)
//        scope.launch(Dispatchers.IO){
//            allTasks = repository.getTasks()
    }

    @UiThread
    fun loadTasks():LiveData<List<Task>>{
        Coroutines.ioThenMain({repository.getTasks()}){
            _tasks.value = it
        }
        return tasks
    }


    fun insert(task:Task){
        Coroutines.io{
            repository.insert(task)
        }
    }

    override fun onCleared() {
        super.onCleared()
//        parentJob.cancel()
    }

}