package com.gorillamoa.routines.core.data

import androidx.room.*

@Dao
interface TaskDao{

    @Query("SELECT * FROM Task")
    fun getTasks(): List<Task>

    @Query("SELECT * FROM TASK WHERE id = :tid")
    fun getTask(tid:Int):Task

    @Query("SELECT * FROM TASK WHERE id IN (:ids)")
    fun getTaskByIds(ids: List<Int>): List<Task>

    @Update
    fun updateTask(task: Task)

    @Insert
    fun insertTasks(vararg task:Task)

    @Delete
    fun deleteTask(task: Task)

    @Query("DELETE FROM Task")
    fun clearAll()


}