package com.gorillamoa.routines.core.extensions

import android.content.Context
import com.gorillamoa.routines.core.data.TaskDatabase
import com.gorillamoa.routines.core.data.DataRepository

fun Context.getDataRepository(): DataRepository {

    val db= TaskDatabase.getDatabase(this)
    return DataRepository(db.taskDao(), db.taskHistoryDao(),db.dayHistoryDao())
}

//        val taskList = getGson().fromJson(dataString, object : TypeToken<List<Task>>() {}.type)

fun Context.processDeletePath(dataString:String){

}

fun Context.processUpdatePath(dataString:String){

    //Get the local data repository,

}

fun Context.processAddPath(dataString:String){

}

