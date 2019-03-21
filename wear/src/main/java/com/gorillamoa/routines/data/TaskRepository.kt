package com.gorillamoa.routines.data


import androidx.annotation.WorkerThread


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
     fun getTasks():List<Task>{
        return taskdao.getTasks()
    }

    @WorkerThread
    fun insert(task: Task) {
        taskdao.insertTask(task)
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
                Task(name = "Meditate Morn",description = "First thing in the morning, Remember to be equanimous"),
                Task(name = "Cold Shower",description = "after meditation"),
                Task(name = "Mobilize",description = "in the day"),
                Task(name = "French Practice",description = "For 1 hour. Verbs. Nouns "),
                Task(name = "Exercises",description = "Climbing Drills, stretching, ")
        )
    }
}