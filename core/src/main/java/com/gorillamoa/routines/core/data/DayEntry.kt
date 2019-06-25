package com.gorillamoa.routines.core.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

//TODO A schema for storing the day's stats
@Entity(tableName = "DayHistory")
class DayHistory(
        @PrimaryKey(autoGenerate = true)
        val dhid:Long? = null,
        val date:Long, //is the date
        val timeStarted:Long,
        val timeFinished:Long,
        val totalTasksScheduled:Int,
        val totalCompleted:Int,
        val totalUnCompleted:Int
)

//Some ideas
// key
// date
// assigned count
// completed count
// skip count
// completion order

@Entity(tableName = "TaskHistory")
data class TaskHistory(

        @PrimaryKey(autoGenerate = true)
        val thid:Long?=null,
        val tid:Long, //This is the matching Task ID
        val timeCompleted: Date, //including the date
        val info:String? = null, //not sure yet
        val completed:Boolean, //did the user skip this today
        val skippedCount:Int //This is how many times the user has been shown the task, but they choose
//to do at some other time. The scheduler will try to find an appropriate time to schedule this
//in the future using the timeCompleted and skip Count
)