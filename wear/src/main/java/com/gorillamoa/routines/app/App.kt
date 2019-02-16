package com.gorillamoa.routines.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import com.gorillamoa.routines.R


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

            NotificationChannel(this.resources.getString(R.string.notificationchannel_one),channelName, NotificationManager.IMPORTANCE_DEFAULT).apply {
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
                 description = channelDescriptorText
                (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(this)

            }
        }
    }
}