package com.gorillamoa.routines.activity

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import android.util.Log
import com.google.android.gms.location.*
import com.gorillamoa.routines.R
import com.gorillamoa.routines.core.extensions.isRecognitionOn
import com.gorillamoa.routines.core.extensions.saveRecognitionStatus
import com.gorillamoa.routines.core.receiver.ActivityReceiver
import kotlinx.android.synthetic.main.activity_recognition.*

class RecognitionActivity : WearableActivity() {

    private val Tag:String = RecognitionActivity::class.java.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recognition)

        recognizing.isChecked = isRecognitionOn()
        recognizing.setOnCheckedChangeListener { view, isChecked ->

            if (isChecked) {
                enableRecog()
            }else{
                disableRecog()
            }
        }

        // Enables Always-on
        setAmbientEnabled()
    }

    private fun getRecognitionPendingIntent():PendingIntent{
        return Intent(this@RecognitionActivity, com.gorillamoa.routines.core.receiver.ActivityReceiver::class.java).let {
            PendingIntent.getBroadcast(this@RecognitionActivity,0,it,PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    private fun enableRecog(){
        val transitions = mutableListOf<ActivityTransition>()

        //find if we're still
        transitions +=
                ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.STILL)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build()

        transitions +=
                ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.STILL)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        .build()

        val request = ActivityTransitionRequest(transitions)
        // myPendingIntent is the instance of PendingIntent where the app receives callbacks.
        val task = ActivityRecognition.getClient(this)
                .requestActivityTransitionUpdates(request,getRecognitionPendingIntent()
                )

        task.addOnSuccessListener {
            // Handle success
            Log.d("$Tag onCreate","Listening success")
            saveRecognitionStatus(true)
        }

        task.addOnFailureListener { e: Exception ->
            // Handle error
            Log.d("$Tag onCreate","Listening failure",e)
        }
    }

    private fun disableRecog(){
        val pintent = getRecognitionPendingIntent()
        val task = ActivityRecognition.getClient(this@RecognitionActivity)
                .removeActivityTransitionUpdates(pintent)

        task.addOnSuccessListener {
            Log.d("$Tag onStop","successful Cancel")
            saveRecognitionStatus(false)
            pintent.cancel()
        }

        task.addOnFailureListener { e: Exception ->
            Log.e("$Tag onStop", e.message)
        }
    }

}
