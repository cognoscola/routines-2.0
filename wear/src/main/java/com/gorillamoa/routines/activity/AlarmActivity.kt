package com.gorillamoa.routines.activity

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.WindowManager
import androidx.fragment.app.FragmentActivity
import androidx.wear.ambient.AmbientModeSupport
import com.gorillamoa.routines.R
import com.gorillamoa.routines.extensions.saveAlarmTimerTriggerStatus
import kotlinx.android.synthetic.main.empty_activity.*

class AlarmActivity : FragmentActivity(), AmbientModeSupport.AmbientCallbackProvider {

    private var mAmbientController: AmbientModeSupport.AmbientController? = null
    @Suppress("unused")
    private val tag:String = AlarmActivity::class.java.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.empty_activity)
        Log.d("$tag onCreate","Alarm Activity Started!")

        //TODO ACQUIRE WAKE LOCK

        /* Show a success toast*/
        /* Vibrate shortly */
        // Turn on the screen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setTurnScreenOn(true)
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }

        //prevent the app from being touched?
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);

        // ...but notify us that it happened.
        window.setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        mAmbientController = AmbientModeSupport.attach(this)
        mAmbientController!!.setAmbientOffloadEnabled(true)

        //TODO create a LOW-POWER ALARM TYPE AS WELL

        background?.setOnTouchListener { view, motionEvent ->

            if (motionEvent.action == MotionEvent.ACTION_UP) {
                mAmbientController!!.setAmbientOffloadEnabled(false)
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                finish()
                true
            }else{
                false
            }
        }
        background?.setOnClickListener {
            Log.d("$tag onCreate","Background Clicked!")
            mAmbientController!!.setAmbientOffloadEnabled(false)
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            finish()
        }
    }


    override fun getAmbientCallback(): AmbientModeSupport.AmbientCallback {
        return AlarmAmbientCallback()
    }

    override fun onStop() {
        saveAlarmTimerTriggerStatus(false)
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        Log.d("$tag onTouchEvent","Captured Touch Event")
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        finish()
        return super.onTouchEvent(event)
    }

    class AlarmAmbientCallback : AmbientModeSupport.AmbientCallback() {

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
}