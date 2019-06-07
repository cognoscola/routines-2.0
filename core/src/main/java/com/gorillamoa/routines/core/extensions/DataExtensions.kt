package com.gorillamoa.routines.core.extensions

import android.content.Context
import com.gorillamoa.routines.core.data.TaskDatabase
import com.gorillamoa.routines.core.data.TaskRepository

fun Context.getDataRepository(): TaskRepository {

    return TaskRepository(TaskDatabase.getDatabase(this).taskDao())
}