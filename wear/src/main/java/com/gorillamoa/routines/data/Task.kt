package com.gorillamoa.routines.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Task")
data class Task(

        @PrimaryKey(autoGenerate = true)
        var id:Int? = null,

//        val type:String,

        val name:String,
        var description:String? = null


        //TODO add addittional options
        //TODO add history
)