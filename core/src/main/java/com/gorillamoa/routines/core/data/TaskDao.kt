package com.gorillamoa.routines.core.data

import androidx.room.*

@Dao
interface TaskDao{

    @Query("SELECT * FROM Task")
    fun getTasks(): List<Task>

    @Query("SELECT * FROM TASK WHERE id = :tid")
    fun getTask(tid:Long):Task?

    @Query("SELECT * FROM TASK WHERE id IN (:ids)")
    fun getTaskByIds(ids: List<Long>): List<Task>

    @Update
    fun updateTask(task: Task)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTasks(vararg task: Task)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTask(task:Task):Long

    @Delete
    fun deleteTask(task: Task)

    @Query("DELETE FROM Task")
    fun clearAll()


}