package com.gorillamoa.routines

import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import android.widget.TextView
import android.widget.Toast
import com.gorillamoa.routines.views.TimerView

class WearRunnerActivity : WearableActivity() {

    private lateinit var timerView:TimerView
    private lateinit var updateText:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wear_runner)

        timerView = findViewById(R.id.timerView)

        updateText = findViewById(R.id.statusView)

        updateText.setOnClickListener {

            when(timerView.getState()){
                TimerView.ClockState.undefined -> {
                    timerView.setCountdownSeconds(10)
                }
                TimerView.ClockState.set -> {
                    timerView.start()
                }
                TimerView.ClockState.running -> {

                }
            }
        }

        timerView.setTextUpdateCallback { text ->
            runOnUiThread {
                updateText.text = text
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
