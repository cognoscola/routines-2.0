package com.gorillamoa.routines.activity

import android.app.*
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.wearable.activity.WearableActivity
import android.widget.Button
import android.widget.Toast
import com.gorillamoa.routines.R
import com.gorillamoa.routines.receiver.WakeUpReceiver
import java.util.*

class ServiceControllerActivity : WearableActivity(), ServiceConnection {

    private val TAG = ServiceControllerActivity::class.java.name

    private val notication_id = 5001
    private val ROUTINES_TAG ="routines"

    private var notificationManager:NotificationManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_controller)


        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(this, WakeUpReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(
                    this,
                    this.resources.getInteger(R.integer.wakeup_alarm_pendingintent_code),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT)
        }

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        findViewById<Button>(R.id.enableServiceButton).setOnClickListener {

            //for now just enable/ disable alarm

            //we don't really need to cancel anything. If we use the
            //same request code, the current alarm will be overridden

            // Set the alarm to start at approximately the time the user indicated
            val calendar: Calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, 8)
                set(Calendar.MINUTE,15)
            }


            // With setInexactRepeating(), you have to use one of the AlarmManager interval
            // constants--in this case, AlarmManager.INTERVAL_DAY.
            alarmManager?.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    alarmIntent
            )
        }

        findViewById<Button>(R.id.disableServiceButton).setOnClickListener {
            alarmManager.cancel(alarmIntent)
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
