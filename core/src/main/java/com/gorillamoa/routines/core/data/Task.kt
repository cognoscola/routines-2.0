package com.gorillamoa.routines.core.data


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Task")
data class Task(

        @PrimaryKey(autoGenerate = true)
        var id:Long? = null,

        val type: TaskType = TaskType.TYPE_UNKNOWN,
        val name:String,
        val description:String? = null,
        val frequency:Float = 1.0f,
        val date:Long = 0L

        //TODO add addittional options
        //TODO add history


) {
        fun toPrettyString(): String {
                return "Task: ID${id}, Name:${name}, description:${description}, type:${type}"
        }
}