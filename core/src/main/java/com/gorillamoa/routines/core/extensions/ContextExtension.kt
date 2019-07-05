package com.gorillamoa.routines.core.extensions


import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration

import android.widget.RemoteViews
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import com.google.gson.Gson
import com.gorillamoa.routines.core.data.Task
import com.gorillamoa.routines.core.data.TaskHistory
import com.gorillamoa.routines.core.viewmodels.TaskViewModel
import com.gorillamoa.routines.core.views.FragmentInjectorHelper

fun FragmentActivity.connectAndLoadViewModel():TaskViewModel{

    val taskViewModel = ViewModelProviders.of(this).get(TaskViewModel::class.java)
    taskViewModel.loadTasks()
    return taskViewModel

}

fun Context.isWatch():Boolean{

    val uiServie = getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
    return uiServie.currentModeType == Configuration.UI_MODE_TYPE_WATCH
}

fun Context.getGson(): Gson {
//TODO provide GSON with dagger
//    return (applicationContext as RemoteInjectorHelper.RemoteGsonProvider).getGson()
    return Gson()
}

fun Context.getTaskFromString(taskString:String):Task{
    return getGson().fromJson(taskString, Task::class.java)
}

//TODO fetch list instead of just one
fun Context.getHistoryFromString(historyString:String): TaskHistory {
    return getGson().fromJson(historyString, TaskHistory::class.java)
}


fun Context.getTaskListFragment():Fragment{
    return (applicationContext as FragmentInjectorHelper.FragmentGraphProvider).fragmentGraph.getTaskListActivityFragment()
}

fun Context.getTaskViewFragment():Fragment{
    return (applicationContext as FragmentInjectorHelper.FragmentGraphProvider).fragmentGraph.getTaskViewFragment()
}


