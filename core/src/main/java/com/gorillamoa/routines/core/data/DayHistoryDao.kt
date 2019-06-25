package com.gorillamoa.routines.core.data

import androidx.room.*

@Dao
interface DayHistoryDao{

    @Query("SELECT * FROM DayHistory WHERE dhid = :id")
    fun getAllHistoryForId(id:Long): List<DayHistory>



    /*@Query("SELECT * FROM TASK WHERE id = :tid")
    fun getTask(tid:Long):Task*/

    /*@Query("SELECT * FROM TASK WHERE id IN (:ids)")
    fun getTaskByIds(ids: List<Long>): List<Task>*/

    @Update
    fun updateTaskHistory(dayHistory: DayHistory)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTasks(vararg dayHistory: DayHistory)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTaskHistory(dayHistory: DayHistory):Long

    @Delete
    fun deleteTaskHistory(dayHistory: DayHistory)

    @Query("DELETE FROM DayHistory")
    fun clearAll()


}