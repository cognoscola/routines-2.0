package com.gorillamoa.routines.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import com.gorillamoa.routines.core.extensions.NOTIFICATION_CHANNEL_ONE
import com.gorillamoa.routines.core.extensions.NOTIFICATION_CHANNEL_TWO


/**
 * We'll configure notification channels every time the app starts
 */
class App:Application(){


    override fun onCreate() {
        super.onCreate()

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

    //TODO Create options menu
    //TODO automatically show notifications if the user doesn't have watchface on.
    //TODO give option to show permanent or dismiss notification

    //TODO ADD options to select notifications or other methods durin oboard method
    //TODO While onboarding, show triangles slowly come together after each steps of the process

    //TODO In options menu create Notifications Behaviour sections
    //TODO Dismiss behaviour - cannot dismiss, finish task, do nothing, snooze
    //TODO Get statistic to see what people choose best.

    //TODO wake up options :
    //TODO 1. Pick as you go
    //TODO 2. In order
    //TODO 3. Random
    //TODO 4. Change order

    //TODO add time tracker.

    //TODO ADD LIST OF HABITS - to form tasks that are recurrent forever
    //TODO ADD LIST OF GOALS - to form tasks that are recurrent until deadline or specified
    //TODO ADD LIST OF UNKNOWN TASKS - no deadline, so it probably wasn't important
    //TODO the system should generate a list of tasks, for these goals and habits depending on frequency specified
    //TODO and deadline, tasks closer to deadline should appear more frequently.
    //TODO Add feature so that we LIMIT a task's time

    //TODO since you can't use custom UIs on notifications, try image generation.
    //make seamless backgrounds, text, etc. through image generation.

    //TODO build addons, and charge for them, etc. tracking friends, exercises, work tasks, etc.

    //TODO TASKS should have option of snoozing (15min, 30min, 1HR, tomorrow) etc.

    //TODO I finished a task but I am unable to check it off because notification is not there! It is showing a different task!


    //TODO clear task notifications sometime between Sleep and Wake Notifications

    //TODO add days' progress bar around the circumference of the app

    //TODO DATA TRACKING AND LOGGING for insight (habit building)

    //TODO Alarm must turn on screen


    //later CREATE A BUG CHART

    //TODO SCHEDULE TASKS IN CALENDAR ON A DAILY BASIS

    //TODO INQUIRE ABOUT TILES

    //FUTURE FEATURES:
    //TODO TRACK TIME SPENT ON A TASK

    //TODO PORT TO ANDROID


    //BUG LIST
    //TODO clicking on the alarm, the background also captures the event. Dont' do this!


    //DEBUG - ADD DEBUG CLOCKING, so we can TEST


}
