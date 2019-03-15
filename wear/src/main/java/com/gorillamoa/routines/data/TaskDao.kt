package com.gorillamoa.routines.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface TaskDao{

    @Query("SELECT * FROM tasks")
    suspend fun getTasks():List<Task>

}