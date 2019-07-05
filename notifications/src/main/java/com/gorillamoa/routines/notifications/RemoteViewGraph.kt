package com.gorillamoa.routines.notifications

import android.widget.RemoteViews

interface RemoteViewGraph{

    /**
     * Because modules are not allowed to depend to each other,
     * we pass primitives.
     */
    fun remoteGetSmallTaskView(task: String):RemoteViews
    fun remoteGetLargeTaskView(history:String):RemoteViews
    fun remoteGetSmallWakeUpView(taskLength:Int):RemoteViews
    fun remoteGetLargeWakeUpView(tasks:String):RemoteViews
    fun remoteGetLargeSleepView():RemoteViews
    fun remoteGetSmallSleepView():RemoteViews

}