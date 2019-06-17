package com.gorillamoa.routines.core.views

import android.widget.RemoteViews
import com.gorillamoa.routines.core.data.Task

interface RemoteViewGraph{

    fun getSmallTaskRemoteView(task: Task):RemoteViews
    fun getLargeTaskRemoteView(taskList:String):RemoteViews

}