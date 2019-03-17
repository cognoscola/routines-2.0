package com.gorillamoa.routines.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Task(

        @PrimaryKey
        val tid:Int,

        val type:String,

        val name:String,

        var description:String? = null


        //TODO add addittional options
        //TODO add history
)