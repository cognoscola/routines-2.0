package com.gorillamoa.routines.core.views

import android.widget.RemoteViews
import com.google.gson.Gson
import com.gorillamoa.routines.core.data.Task

interface RemoteViewGraph{

    fun getSmallTaskRemoteView(task: Task):RemoteViews
    fun getLargeTaskRemoteView(taskList:String):RemoteViews
    fun getGson(): Gson

}