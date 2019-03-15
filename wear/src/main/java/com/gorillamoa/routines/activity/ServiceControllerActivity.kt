package com.gorillamoa.routines.activity

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.support.wearable.activity.WearableActivity
import android.widget.Toast
import com.gorillamoa.routines.R
import com.gorillamoa.routines.extensions.*
import kotlinx.android.synthetic.main.activity_service_controller.*

class ServiceControllerActivity : WearableActivity(), ServiceConnection {

    @Suppress("unused")
    private val tag = ServiceControllerActivity::class.java.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_controller)

        /** get our local settings*/
        wakeUpAlarmToggle.apply {

            isChecked = isAlarmSet()

            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    alarmEnableWakeUp()
                }else{
                    alarmDisableWakeUp()}
            }
        }


        enableServiceButton?.setOnClickListener {
            broadcastShowWakeUp()
        }


        disableServiceButton?.setOnClickListener {
            broadcastShowRandomTask()
        }

        createTask?.setOnClickListener {

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
