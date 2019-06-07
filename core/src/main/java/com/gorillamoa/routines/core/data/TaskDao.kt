package com.gorillamoa.routines.core.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.gorillamoa.routines.core.data.Task

@Dao
interface TaskDao{

    @Query("SELECT * FROM Task")
    fun getTasks(): List<Task>

    @Query("SELECT * FROM TASK WHERE id = :tid")
    fun getTask(tid:Int):Task

    @Query("SELECT * FROM TASK WHERE id IN (:ids)")
    fun getTaskByIds(ids: List<Int>): List<Task>

    @Insert
    fun insertTask(task:Task):Long

/*    @Query("UPDATE users SET age = age + 1 WHERE userId = :userId")
    suspend fun incrementUserAge(userId: String)*/

    /*@Update
    suspend fun updateUser(user: User)*/

    @Insert
    fun insertTasks(vararg task:Task)

    @Delete
    fun deleteTask(task: Task)

    @Query("DELETE FROM Task")
    fun clearAll()


}