package com.gorillamoa.routines.core.data


import androidx.annotation.WorkerThread
import java.util.*

//TODO decide wether to fetch from network or not.
/**
 * In the future we'll use this to synchronize
 * data between our local data and network data.
 * For now its just a layer that does nothing.
 */
class TaskRepository(private val taskdao:TaskDao){

    @WorkerThread
    fun getTaskById(tid:Int):Task{
        return taskdao.getTask(tid)
    }

    @WorkerThread
    fun getTaskByIds(ids:ArrayDeque<Int>):List<Task>{
        return taskdao.getTaskByIds(ids.toList())
    }

    @WorkerThread
     fun getTasks():List<Task>{
        return taskdao.getTasks()
    }

    @WorkerThread
    fun insert(task: Task):Long {
        return taskdao.insertTask(task)
    }

    @WorkerThread
    fun insertAndReturnList(task:Task):List<Task>{
        insert(task)
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
                Task(name = "WHM Breathing",description = "Take a cold shower", type = TaskType.TYPE_HABIT),
                Task(name = "Exercises",description = "See exercise", type = TaskType.TYPE_HABIT),
                Task(name = "French Practice",description = "For 1 hour. Verbs. Nouns", type = TaskType.TYPE_GOAL),
                Task(name = "Food Log",description = "Log your food", type = TaskType.TYPE_HABIT),
                Task(name = "Love Project",description = "Find someone cool", type = TaskType.TYPE_HABIT),
                Task(name = "Sankara",description = "Work on Project Sankara", type = TaskType.TYPE_GOAL),
                Task(name = "Food Log",description = "Log information about food", type = TaskType.TYPE_HABIT),
                Task(name = "Friend Log",description = "Log information about friends", type = TaskType.TYPE_HABIT),
                Task(name = "Late Mobilization",description = "1 Hour, Remain Equanimous", type = TaskType.TYPE_HABIT),
                Task(name = "Late Meditation",description = "Postural Exercises and Spine Stretch", type = TaskType.TYPE_HABIT)
        )
    }
}