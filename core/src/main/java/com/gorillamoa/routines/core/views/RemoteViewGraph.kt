package com.gorillamoa.routines.core.views

import android.widget.RemoteViews
import com.google.gson.Gson
import com.gorillamoa.routines.core.data.Task

interface RemoteViewGraph{

    fun remoteGetSmallTaskView(task: Task):RemoteViews
    fun remoteGetLargeTaskView(taskList:String):RemoteViews
    fun remoteGetSmallWakeUpView():RemoteViews
    fun remoteGetLargeWakeUpView(tasks:String):RemoteViews


}