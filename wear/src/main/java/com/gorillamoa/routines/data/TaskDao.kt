package com.gorillamoa.routines.data


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TaskDao{

    @Query("SELECT * FROM Task")
    suspend fun getTasks(): List<Task>

    @Insert
    suspend fun insertTask(task:Task)

/*    @Query("UPDATE users SET age = age + 1 WHERE userId = :userId")
    suspend fun incrementUserAge(userId: String)*/

    /*@Update
    suspend fun updateUser(user: User)*/

    @Delete
    suspend fun deleteTask(task: Task)

}