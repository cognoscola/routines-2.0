package com.gorillamoa.routines.activity

import android.annotation.SuppressLint
import android.app.*
import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.support.wearable.activity.WearableActivity
import android.widget.Toast
import com.gorillamoa.routines.R
import com.gorillamoa.routines.provider.isAlarmSet
import com.gorillamoa.routines.provider.applySavedTimeToCalendar
import com.gorillamoa.routines.receiver.WakeUpReceiver
import kotlinx.android.synthetic.main.activity_service_controller.*
import java.util.*

class ServiceControllerActivity : WearableActivity(), ServiceConnection {

    @Suppress("unused")
    private val tag = ServiceControllerActivity::class.java.name



    private var notificationManager:NotificationManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_controller)

        //TODO migrate enable/disable of wake up alarm function elsewhere,
        //TODO connect enable/disable alarm function function to toggle UI

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        /** get our local settings*/
        wakeUpAlarmToggle.apply {
            isEnabled = isAlarmSet()

            setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    //enable alarm

                    val alarmIntent = Intent(this@ServiceControllerActivity, WakeUpReceiver::class.java).let { intent:Intent ->
                        PendingIntent.getBroadcast(
                                this@ServiceControllerActivity,
                                WakeUpReceiver.WAKE_UP_INTENT_CODE,
                                intent,
                                PendingIntent.FLAG_UPDATE_CURRENT)
                    }
                    val calendar: Calendar = Calendar.getInstance().apply {
                        timeInMillis = System.currentTimeMillis()
                        add(Calendar.DATE,1)
                        applySavedTimeToCalendar(this)
                    }


                    alarmManager.setInexactRepeating(
                            AlarmManager.RTC_WAKEUP,
                            calendar.timeInMillis,
                            AlarmManager.INTERVAL_DAY,
                            alarmIntent
                    )


                }else{
                    //disable alarm
                    alarmManager.cancel(PendingIntent.getBroadcast(
                            this@ServiceControllerActivity,
                            WakeUpReceiver.WAKE_UP_INTENT_CODE,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT)
                    )
                }
            }
        }


        //TODO Launch wake up notification from UI
        //TODO Launch task notification from UI



        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


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
