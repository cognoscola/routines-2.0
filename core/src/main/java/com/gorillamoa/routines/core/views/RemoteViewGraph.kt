package com.gorillamoa.routines.core.views

import android.widget.RemoteViews
import com.gorillamoa.routines.core.data.Task

interface RemoteViewGraph{

    fun remoteGetSmallTaskView(task: Task):RemoteViews
    fun remoteGetLargeTaskView(taskList:String):RemoteViews
    fun remoteGetSmallWakeUpView(taskLength:Int):RemoteViews
    fun remoteGetLargeWakeUpView(tasks:String):RemoteViews
    fun remoteGetLargeSleepView():RemoteViews
    fun remoteGetSmallSleepView():RemoteViews

}