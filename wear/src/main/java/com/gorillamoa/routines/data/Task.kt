package com.gorillamoa.routines.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Task(

        @PrimaryKey
        val tid:Int,

        val type:TaskType = TaskType.TYPE_UNKNOWN,

        val name:String = "A task",

        var description:String? = null


        //TODO add addittional options
        //TODO add history
)