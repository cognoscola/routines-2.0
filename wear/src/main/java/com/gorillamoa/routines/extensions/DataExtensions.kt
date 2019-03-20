package com.gorillamoa.routines.extensions

import android.content.Context
import com.gorillamoa.routines.data.TaskDatabase
import com.gorillamoa.routines.data.TaskRepository

fun Context.getDataRepository():TaskRepository{

    return TaskRepository(TaskDatabase.getDatabase(this).taskDao())
}