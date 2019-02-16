package com.gorillamoa.routines.activity

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.support.wearable.activity.WearableActivity
import android.widget.Button
import android.widget.Toast
import com.gorillamoa.routines.R

class ServiceControllerActivity : WearableActivity(), ServiceConnection {

    private val TAG = ServiceControllerActivity::class.java.name

    private val notication_id = 5001
    private val ROUTINES_TAG ="routines"

    private var notificationManager:NotificationManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_controller)


        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        findViewById<Button>(R.id.enableServiceButton).setOnClickListener {

            //set repeating alarms

            //for now we'll just show or hide a notification


            // Create notifØication

            val builder = Notification.Builder(applicationContext,this.resources.getString(R.string.notificationchannel_one))
            //val notificationIntent = Intent(applicationContext, EmotionPickerActivity::class.java)
          //  val pickerIntent = PendingIntent.getActivity(applicationContext, 0, notificationIntent, 0)

            builder.setContentTitle("Today's Schedule")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentText("Task B in Progress")
                   // .setContentIntent(pickerIntent)
                    .setAutoCancel(true)

            notificationManager?.notify(ROUTINES_TAG,notication_id, builder.build())

        }

        findViewById<Button>(R.id.disableServiceButton).setOnClickListener {

            //disable alarms
            notificationManager?.cancel(ROUTINES_TAG,notication_id)
           // notificationManager?.cancelAll()

        }

        // Enables Always-on
        setAmbientEnabled()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        Toast.makeText(this, "Service Disconnected",Toast.LENGTH_SHORT).show()
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        Toast.makeText(this, "Service Connected",Toast.LENGTH_SHORT).show()
    }
}
