package com.gorillamoa.routines.configuration

import android.app.Application
import android.widget.RemoteViews
import com.gorillamoa.routines.notifications.*

class App :Application(), RemoteInjectorHelper.RemoteGraphProvider
{
    lateinit var graph:RemoteViewGraph

    //TODO SPLIT
    override val remoteViewGraph: RemoteViewGraph
        get() = graph

    override fun onCreate() {
        super.onCreate()

       graph = object : RemoteViewGraph {
            override fun remoteGetSmallTaskView(task: String): RemoteViews {

                return this@App.getTaskRemoteView(task)
            }

            override fun remoteGetLargeTaskView(history: String): RemoteViews {
                return this@App.getLargeTaskRemoteView(history)
            }

            override fun remoteGetLargeWakeUpView(tasks: String): RemoteViews {
                return this@App.getLargeWakeUpRemoteView(tasks)
            }

            override fun remoteGetSmallWakeUpView(taskLength: Int): RemoteViews {
                return this@App.getWakeupRemoteView(taskLength)
            }

            override fun remoteGetLargeSleepView(): RemoteViews {
                return this@App.getLargeSleepView()
            }

            override fun remoteGetSmallSleepView(): RemoteViews {
                return this@App.getSmallSleepView()
            }
        }
    }

}