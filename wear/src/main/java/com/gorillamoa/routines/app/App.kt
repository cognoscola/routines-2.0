package com.gorillamoa.routines.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import com.gorillamoa.routines.R
import com.gorillamoa.routines.provider.TaskProvider


/**
 * We'll configure notification channels every time the app starts
 */
class App:Application(){

    //TODO use dagger to inject a Provider
    var taskProvider:TaskProvider?= null

    fun getTasksProvider():TaskProvider{
        if (taskProvider == null) {
            taskProvider = TaskProvider()
        }
        return taskProvider!!
    }

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            /** channel for the wake up / sleep notifications */
            val channelName = "Routine Notifications"
            val channelDescriptorText = "Routines Tasks for Channel"

            NotificationChannel(this.resources.getString(R.string.notificationchannel_one),channelName, NotificationManager.IMPORTANCE_DEFAULT).apply {
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
                 description = channelDescriptorText
                (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(this)

            }
        }
    }

    //TODO Create options menu
    //TODO In options menu create Notifications Behaviour sections
    //TODO Dismiss behaviour - cannot dismiss, finish task, do nothing, snooze
    //TODO Get statistic to see what people choose best.

    //TODO wake up options :
    //TODO 1. Pick as you go
    //TODO 2. In order
    //TODO 3. Random
    //TODO 4. Change order

    //TODO ADD LIST OF HABITS - to form tasks that are recurrent forever
    //TODO ADD LIST OF GOALS - to form tasks that are recurrent until deadline or specified
    //TODO ADD LIST OF UNKNOWN TASKS - no deadline, so it probably wasn't important
    //TODO the system should generate a list of tasks, for these goals and habits depending on frequency specified
    //TODO and deadline, tasks closer to deadline should appear more frequently.
    //TODO Add feature so that we LIMIT a task


    //TODO TASKS should have option of snoozing (15min, 30min, 1HR, tomorrow) etc.

}

fun Context.getTaskPRovider():TaskProvider{
    return (applicationContext as App).getTasksProvider()
}