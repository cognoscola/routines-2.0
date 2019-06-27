package com.gorillamoa.routines.core.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

//TODO A schema for storing the day's stats
@Entity(tableName = "DayHistory")
class DayHistory(
        @PrimaryKey(autoGenerate = true)
        var dhid:Long? = null,
        var date:Long, //is the date
        var timeStarted:Long,
        var timeFinished:Long,
        var totalTasksScheduled:Int,
        var totalCompleted:Int,
        var totalUnCompleted:Int
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
        var thid:Long?=null,
        var tid:Long, //This is the matching Task ID
        var timeCompleted: Date, //including the date
        var info:String? = null, //not sure yet
        var completed:Boolean, //did the user skip this today
        var skippedCount:Int //This is how many times the user has been shown the task, but they choose
//to do at some other time. The scheduler will try to find an appropriate time to schedule this
//in the future using the timeCompleted and skip Count
)