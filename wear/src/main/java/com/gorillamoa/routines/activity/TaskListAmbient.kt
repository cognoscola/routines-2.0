package com.gorillamoa.routines.activity

import android.os.Bundle
import android.util.Log
import androidx.wear.ambient.AmbientModeSupport

class TaskListAmbient : AmbientModeSupport.AmbientCallback() {

    override fun onEnterAmbient(ambientDetails: Bundle?) {
        // Handle entering ambient mode
        Log.d("onEnterAmbient","")
    }

    override fun onExitAmbient() {
        // Handle exiting ambient mode
        Log.d("onExitAmbient","")
    }

    override fun onUpdateAmbient() {
        // Update the content
        Log.d("onUpdateAmbient","")
    }
}