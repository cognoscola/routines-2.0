package com.gorillamoa.routines.core.extensions


import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import com.gorillamoa.routines.core.viewmodels.TaskViewModel

fun FragmentActivity.connectAndLoadViewModel():TaskViewModel{

    val taskViewModel = ViewModelProviders.of(this).get(TaskViewModel::class.java)
    taskViewModel.loadTasks()
    return taskViewModel

}

fun Context.isWatch():Boolean{

    val uiServie = getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
    return uiServie.currentModeType == Configuration.UI_MODE_TYPE_WATCH
}
