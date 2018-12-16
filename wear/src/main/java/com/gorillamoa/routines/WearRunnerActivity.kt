package com.gorillamoa.routines

import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import android.widget.TextView
import android.widget.Toast
import com.gorillamoa.routines.views.TimerView

class WearRunnerActivity : WearableActivity() {

    private lateinit var timerView:TimerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wear_runner)

        timerView = findViewById<TimerView>(R.id.timerView)

        findViewById<TextView>(R.id.text).setOnClickListener {

            when(timerView.getState()){
                TimerView.ClockState.undefined -> {
                    timerView.setCountdownSeconds(30.0f)
                }
                TimerView.ClockState.set -> {
                    timerView.start()

                }
                TimerView.ClockState.running -> {

                }
            }
        }

        // Enables Always-on
        setAmbientEnabled()
    }

    override fun onResume() {
        super.onResume()
//        findViewById<TimerView>(R.id.timerView).elapsedTimeSecond = 15

    }
}
