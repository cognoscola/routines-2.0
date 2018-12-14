package com.gorillamoa.routines

import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import android.widget.TextView
import android.widget.Toast
import com.gorillamoa.routines.views.TimerView

class WearRunnerActivity : WearableActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wear_runner)


        findViewById<TextView>(R.id.text).setOnClickListener {
            findViewById<TimerView>(R.id.timerView).setTime(15.0f)
            Toast.makeText(this, "click",Toast.LENGTH_SHORT).show()
        }


        // Enables Always-on
        setAmbientEnabled()
    }

    override fun onResume() {
        super.onResume()
//        findViewById<TimerView>(R.id.timerView).elapsedTimeSecond = 15

    }
}
