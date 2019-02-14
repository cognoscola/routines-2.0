package com.gorillamoa.routines

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.wearable.activity.WearableActivity
import android.widget.Button
import android.widget.Toast

class ServiceControllerActivity : WearableActivity(), ServiceConnection {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_controller)

        findViewById<Button>(R.id.enableServiceButton).setOnClickListener {

        }

        findViewById<Button>(R.id.disableServiceButton).setOnClickListener {



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
