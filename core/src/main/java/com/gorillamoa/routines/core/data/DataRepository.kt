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
class DataRepository(
        private val taskdao:TaskDao,
        private val taskHistoryDao: TaskHistoryDao,
        private val dayHistoryDao: DayHistoryDao){

    /****************************************
     * TASK
     *****************************************/
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
    fun getTaskById(tid:Long):Task?{
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
        taskHistoryDao.clearAll()
        dayHistoryDao.clearAll()
    }

    @WorkerThread
    fun dummy(){

        taskdao.insertTasks(
        Task(name = "Morning Meditation",description = "1 Hour, Remain Equanimous", type = TaskType.TYPE_HABIT, frequency = 1f),
        Task(name = "Morning Meal Prep",description = "Seg Meal Log", type = TaskType.TYPE_HABIT, frequency = 1f),
        Task(name = "Morning Exercise and Log",description = "Postural Exercises and Spine Stretch", type = TaskType.TYPE_HABIT, frequency = 1f),
        Task(name = "WHM Breathing",description = "Take a cold shower", type = TaskType.TYPE_HABIT, frequency = 1f),
        Task(name = "Sankara",description = "See Sankara Log", type = TaskType.TYPE_HABIT,frequency = 1f),
        Task(name = "French", description = "Just 1 hour", type = TaskType.TYPE_HABIT,frequency = 0.5f),
        Task(name = "Paint", description = "Ego and creativity cannot go together", type = TaskType.TYPE_HABIT,frequency = 1f),
        Task(name = "Breakfast Log", description = "Using cronometer", type = TaskType.TYPE_HABIT,frequency = 1f),
        Task(name = "Lunch Log", description = "Using cronometer", type = TaskType.TYPE_HABIT,frequency = 1f),
        Task(name = "Dinner Log", description = "Using cronometer", type = TaskType.TYPE_HABIT,frequency = 1f),
        Task(name = "Gratitude Log", description = "Using cronometer", type = TaskType.TYPE_HABIT,frequency = 1f),
        Task(name = "Journal", description = "Using cronometer", type = TaskType.TYPE_HABIT,frequency = 1f),
        Task(name = "Wood Working", description = "Using cronometer", type = TaskType.TYPE_HABIT,frequency = 0.2f),
        Task(name = "Evening Exercise", description = "Using cronometer", type = TaskType.TYPE_HABIT,frequency = 1f),
        Task(name = "Evening Log", description = "Using cronometer", type = TaskType.TYPE_HABIT,frequency = 1f),
        Task(name = "Evening Meditation", description = "1hr Equanimity", type = TaskType.TYPE_HABIT,frequency = 1f)
        )
        taskdao.getTasks().forEach {
            //TODO At the start of the day, we could create task history for each task, then we don't worry about wether they exist in our db
            taskHistoryDao.insertTaskHistory(TaskHistory(tid = it.id!!, timeCompleted = Date(),info = "LaLa1",completed = false,skippedCount = 2 ))
            taskHistoryDao.insertTaskHistory(TaskHistory(tid = it.id!!, timeCompleted = Calendar.getInstance().run {
                add(Calendar.HOUR,25)
                time
            },info = "LaLa2",completed = false,skippedCount = 1 ))
        }
    }

    /****************************************
     * TASK HISTORY 
     *****************************************/
    @WorkerThread
    fun completeTask(tid:Long){

        val startDay  = getDayStartTimeMillis()
        val endDay = startDay + (24 * 60 * 60 *1000)

        val taskHistory = taskHistoryDao.getHistoryForTaskToday(tid,startDay,endDay)
        taskHistory?.let {

//            it.completed = true
            taskHistoryDao.setCompletionStatus(true,it.thid!!)
//            taskHistoryDao.updateTaskHistory(it)

            //If not found, we simply insert.
        }?:run {
            appendTaskEntry(TaskHistory(
                    tid = tid,
                    timeCompleted = Date(),
                    info = "",
                    completed = true,
                    skippedCount = 0
            ))
        }
    }

    fun getDayStartTimeMillis():Long{
        return Calendar.getInstance().run {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            timeInMillis
        }
    }

    @WorkerThread
    fun unCompleteTask(tid:Long){

        val startDay  = getDayStartTimeMillis()
        val endDay = startDay + (24 * 60 * 60 *1000)

        taskHistoryDao.getHistoryForTaskToday(tid,startDay,endDay)?.let {

            taskHistoryDao.setCompletionStatus(false,it.thid!!)

        }?:run{
            appendTaskEntry(TaskHistory(
                    tid = tid,
                    timeCompleted = Date(),
                    info = "",
                    completed = false,
                    skippedCount = 0
            ))
        }
    }


    @WorkerThread
    fun appendTaskEntry(taskHistory: TaskHistory){
        taskHistoryDao.insertTaskHistory(taskHistory)
    }

    @WorkerThread
    fun getHistoryForTask(task: Task):List<TaskHistory>{
        return taskHistoryDao.getAllHistoryForTask(task.id!!)
    }

    @WorkerThread
    fun clearAllTaskHistory(){
        taskHistoryDao.clearAll()
    }


}