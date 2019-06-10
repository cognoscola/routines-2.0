package com.gorillamoa.routines.core.viewmodels

import android.app.Application
import androidx.annotation.UiThread
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gorillamoa.routines.core.coroutines.Coroutines

import com.gorillamoa.routines.core.data.Task
import com.gorillamoa.routines.core.data.TaskDatabase
import com.gorillamoa.routines.core.data.TaskRepository
import java.util.*

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

    //the object on which we can observe changes
    val tasks: LiveData<List<Task>> get() = _tasks

    init{

        val taskDao = TaskDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao)
//        scope.launch(Dispatchers.IO){
//            allTasks = repository.getTasks()
    }

    @UiThread
    fun loadTasks():LiveData<List<Task>>{
        com.gorillamoa.routines.core.coroutines.Coroutines.ioThenMain({repository.getTasks()}){
            _tasks.value = it
        }
        return tasks
    }

    @UiThread
    fun loadTasks(list:ArrayDeque<Int>):LiveData<List<Task>>{

        com.gorillamoa.routines.core.coroutines.Coroutines.ioThenMain({repository.getTaskByIds(list)}){
            _tasks.value = it
        }
        return tasks
    }

    @UiThread
    fun insertAndReturnList(task:Task):LiveData<List<Task>>{
        com.gorillamoa.routines.core.coroutines.Coroutines.ioThenMain({repository.insertAndReturnList(task)}){
            _tasks.value = it
        }
        return tasks
    }

    @UiThread
    fun clearReturnList(){
        com.gorillamoa.routines.core.coroutines.Coroutines.ioThenMain({repository.clearAndReturnList()}){
            _tasks.value = it
        }
    }

    fun insert(task:Task){
        //Update the change in memory. The LiveData structure will report a change

        com.gorillamoa.routines.core.coroutines.Coroutines.io{
            repository.insert(task)
        }
    }

    fun clear(){
        com.gorillamoa.routines.core.coroutines.Coroutines.io {
            repository.clearAll()
        }
    }

    fun dummy(){
        com.gorillamoa.routines.core.coroutines.Coroutines.io{
            repository.dummy()
        }
    }

    /**
     * as in this class' memory was cleared
     */
    override fun onCleared() {
        super.onCleared()
//        parentJob.cancel()
    }


}