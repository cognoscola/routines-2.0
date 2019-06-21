package com.gorillamoa.routines.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.widget.RemoteViews
import com.google.gson.Gson
import com.gorillamoa.routines.core.data.Task
import com.gorillamoa.routines.core.extensions.NOTIFICATION_CHANNEL_ONE
import com.gorillamoa.routines.core.extensions.NOTIFICATION_CHANNEL_TWO
import com.gorillamoa.routines.core.views.RemoteViewGraph
import com.gorillamoa.routines.core.views.RemoteInjectorHelper
import com.gorillamoa.routines.getLargeWakeUpRemoteView
import com.gorillamoa.routines.getTaskRemoteView
import com.gorillamoa.routines.getWakeupRemoteView


/**
 * We'll configure notification channels every time the app starts
 */
class App:Application(),RemoteInjectorHelper.RemoteGraphProvider, RemoteInjectorHelper.RemoteGsonProvider
{

    lateinit var graph:AppComponent

    override val remoteViewGraph:RemoteViewGraph
        get() = graph


    override fun getGson():Gson {
        return gsonObject
    }

    private val gsonObject by lazy {
        return@lazy Gson()
    }

    override fun onCreate() {
        super.onCreate()

        graph = object:AppComponent{

            override fun remoteGetSmallTaskView(task: Task): RemoteViews {
                return this@App.getTaskRemoteView(task)
            }

            override fun remoteGetLargeTaskView(taskList:String): RemoteViews {
                return this@App.getLargeWakeUpRemoteView(taskList)
            }


            override fun remoteGetLargeWakeUpView(tasks:String): RemoteViews {
                return this@App.getLargeWakeUpRemoteView(tasks)
            }

            override fun remoteGetSmallWakeUpView(taskLength:Int): RemoteViews {
                return this@App.getWakeupRemoteView(taskLength)
            }


        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            /** channel for the wake up / sleep notifications */
            val channelName = "Routine Notifications"
            val channelDescriptorText = "Routines Tasks for Channel"


            NotificationChannel(NOTIFICATION_CHANNEL_ONE,channelName, NotificationManager.IMPORTANCE_DEFAULT).apply {
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
                 description = channelDescriptorText
                (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(this)

            }

            NotificationChannel(NOTIFICATION_CHANNEL_TWO,channelName, NotificationManager.IMPORTANCE_HIGH).apply {
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
                description = channelDescriptorText
                (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(this)

            }
        }
    }


}
