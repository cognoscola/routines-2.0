package com.gorillamoa.routines.activity

import android.app.*
import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.support.wearable.activity.WearableActivity
import android.widget.Toast
import com.gorillamoa.routines.R
import com.gorillamoa.routines.extensions.alarmDisableWakeUp
import com.gorillamoa.routines.extensions.alarmEnableWakeUp
import com.gorillamoa.routines.extensions.isAlarmSet
import com.gorillamoa.routines.receiver.WakeUpReceiver
import kotlinx.android.synthetic.main.activity_service_controller.*

class ServiceControllerActivity : WearableActivity(), ServiceConnection {

    @Suppress("unused")
    private val tag = ServiceControllerActivity::class.java.name



    private var notificationManager:NotificationManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_controller)

        //TODO migrate enable/disable of wake up alarm function elsewhere,
        //TODO connect enable/disable alarm function function to toggle UI
        /** get our local settings*/
        wakeUpAlarmToggle.apply {

            isChecked = isAlarmSet()


            setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    alarmEnableWakeUp()
                }else{
                    alarmDisableWakeUp()}
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
