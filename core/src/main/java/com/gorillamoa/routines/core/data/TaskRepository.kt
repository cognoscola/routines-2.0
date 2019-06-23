package com.gorillamoa.routines.core.data

import android.content.Context
import androidx.annotation.WorkerThread
import com.gorillamoa.routines.core.coroutines.Coroutines
import com.gorillamoa.routines.core.services.DataLayerListenerService
import java.util.*


//TODO as part of the synchronization strategy, we'll send over last commit time when the app starts up and compare
//commit times to see if we have a more recent commit in another device
//the compare method will involve check all tasks and just copying the action

/**
 * In the future we'll use this to synchronize
 * data between our local data and network data.
 * For now its just a layer that does nothing.
 */
class TaskRepository(private val taskdao:TaskDao){

    fun insertMirror(context: Context, task: Task){
        Coroutines.ioThenMain({insert(task)}){ tid ->
            DataLayerListenerService.insertRemotely(context,task.apply { id = tid  })
        }
    }

    fun updateMirror(context: Context,task: Task){
        Coroutines.ioThenMain({update(task)}){
            DataLayerListenerService.updateRemotely(context,task)
        }
    }

    fun deleteMirror(context: Context,task: Task){
        Coroutines.ioThenMain({delete(task)}){
            DataLayerListenerService.deleteRemotely(context,task)
        }
    }

    @WorkerThread
    fun getTaskById(tid:Long):Task{
        return taskdao.getTask(tid)
    }

    @WorkerThread
    fun getTaskByIds(ids:ArrayDeque<Long>):List<Task>{
        return taskdao.getTaskByIds(ids.toList())
    }

    @WorkerThread
     fun getTasks():List<Task>{
        return taskdao.getTasks()
    }

    /**
     * User should call this to insert, and the repo will
     * take care to insert remotely
     */
    @WorkerThread
    fun insert(task: Task):Long {

        return taskdao.insertTask(task)
    }

    @WorkerThread
    fun delete(task:Task){
        taskdao.deleteTask(task)
    }

    @WorkerThread
    fun update(task:Task){
        taskdao.updateTask(task)
    }


    @WorkerThread
    fun insertAndReturnList(task:Task):List<Task>{
        insert(task)
        return taskdao.getTasks()
    }

    @WorkerThread
    fun deleteAndReturnList(task:Task):List<Task>{
        delete(task)
        return taskdao.getTasks()
    }

    @WorkerThread
    fun updateAndReturnList(task:Task):List<Task>{
        update(task)
        return taskdao.getTasks()
    }


    @WorkerThread
    fun clearAndReturnList():List<Task>{
        clearAll()
        return taskdao.getTasks()
    }

    @WorkerThread
    fun clearAll(){
        taskdao.clearAll()
    }

    @WorkerThread
    fun dummy(){
        taskdao.insertTasks(

                Task(name = "Early Mobilization",description = "Postural Exercises and Spine Stretch", type = TaskType.TYPE_HABIT),
                Task(name = "Morning Meditation",description = "1 Hour, Remain Equanimous", type = TaskType.TYPE_HABIT),
                Task(name = "WHM Breathing",description = "Take a cold shower", type = TaskType.TYPE_HABIT)

                /*Task(name = "Exercises",description = "See exercise", type = TaskType.TYPE_HABIT),
                Task(name = "French Practice",description = "For 1 hour. Verbs. Nouns", type = TaskType.TYPE_GOAL),
                Task(name = "Food Log",description = "Log your food", type = TaskType.TYPE_HABIT),
                Task(name = "Love Project",description = "Find someone cool", type = TaskType.TYPE_HABIT),
                Task(name = "Sankara",description = "Work on Project Sankara", type = TaskType.TYPE_GOAL),
                Task(name = "Food Log",description = "Log information about food", type = TaskType.TYPE_HABIT),
                Task(name = "Friend Log",description = "Log information about friends", type = TaskType.TYPE_HABIT),
                Task(name = "Late Mobilization",description = "1 Hour, Remain Equanimous", type = TaskType.TYPE_HABIT),
                Task(name = "Late Meditation",description = "Postural Exercises and Spine Stretch", type = TaskType.TYPE_HABIT)*/
        )
    }
}