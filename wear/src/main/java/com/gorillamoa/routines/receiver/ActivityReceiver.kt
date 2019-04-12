package com.gorillamoa.routines.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity
import com.gorillamoa.routines.extensions.notificationShowActivity
import com.gorillamoa.routines.extensions.saveAlarmRestStatus

class ActivityReceiver:BroadcastReceiver(){

    override fun onReceive(context: Context?, intent: Intent?) {

        if (ActivityTransitionResult.hasResult(intent)) {
            val result = ActivityTransitionResult.extractResult(intent)!!
            for (event in result.transitionEvents) {

                if (event.activityType == DetectedActivity.STILL) {

                    context?.saveAlarmRestStatus(event.transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                }

                //TODO add option to disable the recognition activity temporarily
                context?.notificationShowActivity(getActivityName(event.activityType), event.transitionType)

//                Log.d("onReceive","ActivityRecog: ${getActivityName(event.activityType)} ${getEventName(event.transitionType)}")
                // chronological sequence of events....
            }
        }
    }

    fun getActivityName(type:Int):String{
        return when(type){
            0 ->  "IN_VEHICLE"
            1 -> "ON_BICYCLE"
            2 -> "ON_FOOT"
            3 -> "STILL"
            4 -> "UNKNOWN"
            5 -> "TILTING"
            else -> "UNKNOWN"

        }
    }

}