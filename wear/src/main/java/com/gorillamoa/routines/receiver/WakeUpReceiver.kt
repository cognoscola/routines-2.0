package com.gorillamoa.routines.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.gorillamoa.routines.extensions.*

/**
 * What to do when our app sounds the "wake up" alarm.
 * Again, this alarm is not a "wake from human sleep" alarm. Instead
 * it lets the user know, via some UI, notifications probably, that they
 * should prepare the day's tasks.
 *
 * There are two types of "Wake up" Alarms:
 * ACTION_ONBOARD - fires once when we are onboarding the user
 * ACTION_WAKEUP - fires every morning at the specified time
 *
 */
class WakeUpReceiver:BroadcastReceiver(){

    companion object {

        const val MAX_NOTIFICATION_LINE_LENGTH = 23

        /**
         * When this receiver has an intent with a type ACTION_ONBOARD
         * it means that it should execute in a manner in line with on-boarding
         * the user. That is, generate a notification with the user's first task
         */
        const val ACTION_ONBOARD = "W0"

        /**
         * When the receiver has an intent with a type ACTION_DEFAULT, it
         * means that the receiver should process the intent normally.
         * I.e. schedule tasks as normal
         */
        const val ACTION_DEFAULT  = "W1"


        const val KEY_ALARM = "A"
        const val WAKE_UP_INTENT_CODE = 1

    }

    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("onReceive","Woken up...")
        
        intent?.let {

            if (intent.hasExtra(KEY_ALARM)) {
                Log.d("onReceive","By Alarm")
            }
            
            when (it.action) {

                ACTION_ONBOARD ->{
                    Log.d("onReceive", "ACTION_ONBOARD")

                    context.notificationShowWakeUp(StringBuilder().apply {
                        addTaskLine("Plant Seed", "0/1") }.toString()
                    ,context.createNotificationMainIntentForOnboarding())
                }

                ACTION_DEFAULT -> {

                    Log.d("onReceive", "ACTION_DEFAULT")
                    context.notificationShowWakeUp(StringBuilder().apply {
                            addTaskLine("meditate","1hr")
                            addTaskLine("Meeting with John again twice","9min")
                            addTaskLine("Ultra super short","1reps")
                            addTaskLine("Dod","4catches")
                            addTaskLine("pick kids up from school","2p")
                            buildEndLine(20)
                    }.toString()
                    ,context.createNotificationMainIntentForWakeUp())
                }
                else ->{

                    throw Exception("This wake up alarm did not receive instructions!")
                    //TODO create a notification that something went wrong
                }
            }
        }
    }



}