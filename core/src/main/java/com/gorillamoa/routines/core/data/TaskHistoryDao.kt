package com.gorillamoa.routines.core.data

import androidx.room.*

@Dao
interface TaskHistoryDao{

    @Query("SELECT * FROM TaskHistory WHERE tid = :tid")
    fun getAllHistoryForTask(tid:Long): List<TaskHistory>

    @Query("SELECT * FROM TaskHistory WHERE tid = :tid AND timeCompleted BETWEEN :startDayTime AND :endDayTime ")
    fun getHistoryForTaskToday(tid:Long, startDayTime:Long, endDayTime:Long):TaskHistory?

    /*@Query("SELECT * FROM TASK WHERE id = :tid")
    fun getTask(tid:Long):Task*/

    /*@Query("SELECT * FROM TASK WHERE id IN (:ids)")
    fun getTaskByIds(ids: List<Long>): List<Task>*/

    @Query("UPDATE TaskHistory SET completed = :completed WHERE tid =:thid")
    fun setCompletionStatus(completed:Boolean, thid:Long)

    @Update
    fun updateTaskHistory(taskHistory: TaskHistory)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTaskHistories(vararg task: TaskHistory)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTaskHistory(task:TaskHistory):Long

    @Delete
    fun deleteTaskHistory(task: TaskHistory)

    @Query("DELETE FROM TaskHistory")
    fun clearAll()


}