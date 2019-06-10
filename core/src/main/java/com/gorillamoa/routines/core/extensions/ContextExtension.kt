package com.gorillamoa.routines.core.extensions


import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import com.gorillamoa.routines.core.viewmodels.TaskViewModel

fun FragmentActivity.connectAndLoadViewModel():TaskViewModel{

    val taskViewModel = ViewModelProviders.of(this).get(TaskViewModel::class.java)
    taskViewModel.loadTasks()
    return taskViewModel

}
