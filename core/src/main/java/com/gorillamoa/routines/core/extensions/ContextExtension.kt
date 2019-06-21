package com.gorillamoa.routines.core.extensions


import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import android.widget.RemoteViews
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import com.google.gson.Gson
import com.gorillamoa.routines.core.data.Task
import com.gorillamoa.routines.core.viewmodels.TaskViewModel
import com.gorillamoa.routines.core.views.RemoteInjectorHelper

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

    return (applicationContext as RemoteInjectorHelper.RemoteGsonProvider).getGson()
}

fun Context.getTaskFromString(taskString:String):Task{
    return getGson().fromJson(taskString, Task::class.java)
}

fun Context.getSmallTaskRemoteView(task: Task):RemoteViews{

    return (applicationContext as RemoteInjectorHelper.RemoteGraphProvider).remoteViewGraph.getSmallTaskRemoteView(task)

}