package com.gorillamoa.routines.data

data class Task(

        val type:TaskType = TaskType.TYPE_UNKNOWN,
        val name:String = "A task",
        var description:String? = null
        //TODO add addittional options
        //TODO add history
)