package com.gorillamoa.routines.app


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.widget.RemoteViews
import android.widget.Toast
import com.google.gson.Gson
import com.gorillamoa.routines.core.receiver.AlarmReceiver
import com.gorillamoa.routines.dagger.DebugAppComponent
import com.gorillamoa.routines.notifications.*


/**
 * We'll configure notification channels every time the app starts
 */
class App:BaseApplication(), RemoteInjectorHelper.RemoteGraphProvider, RemoteInjectorHelper.RemoteGsonProvider
{

    lateinit var graph:AppComponent

    override val remoteViewGraph: RemoteViewGraph
        get() = graph


//    override fun getGson():Gson {
//        return gsonObject
//    }

    private val gsonObject by lazy {
        return@lazy Gson()
    }

    fun setMockMode(mockMode: Boolean) {
//        graph = DebugAppComponent.Initializer.init(mockMode,this)
    }

    override fun onCreate() {
        super.onCreate()
        graph = object:AppComponent{

            override fun remoteGetSmallTaskView(task: String): RemoteViews {
                return this@App.getTaskRemoteView(task)
            }

            override fun remoteGetLargeTaskView(history:String): RemoteViews {
                return this@App.getLargeTaskRemoteView(history)
            }


            override fun remoteGetLargeWakeUpView(tasks:String): RemoteViews {
                //TODO potentially use the new function definition
                return this@App.getLargeWakeUpRemoteView(tasks)
            }

            override fun remoteGetSmallWakeUpView(taskLength:Int): RemoteViews {
                return this@App.createWakeUpRemoteView(taskLength)
            }

            override fun remoteGetLargeSleepView(): RemoteViews {
                return this@App.getLargeSleepView()
            }

            override fun remoteGetSmallSleepView(): RemoteViews {
                return this@App.getSmallSleepView()
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

        attachCallbacks(this)
    }

    private fun attachCallbacks(context: Context){
        AlarmReceiver.setAlarmEventCallbacks(

                object :AlarmReceiver.AlarmReceiverApi{
                    override fun processWakeUpEvent() {
                        Toast.makeText(context,"Woke up",Toast.LENGTH_SHORT).show()

                    }
                }
        )
    }

}
