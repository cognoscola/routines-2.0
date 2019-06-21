package com.gorillamoa.routines.core.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.gorillamoa.routines.core.extensions.*

import com.gorillamoa.routines.core.scheduler.TaskScheduler



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
class AlarmReceiver:BroadcastReceiver(){

    @Suppress("unused")
    private val tag:String = AlarmReceiver::class.java.name
    companion object {

        /**
         * When this receiver has an intent with a type ACTION_ONBOARD
         * it means that it should execute in a manner in line with on-boarding
         * the user. That is, generate a notification with the user's first task
         */
        const val ACTION_ONBOARD = "W0"

        /**
         * When the receiver has an intent with a type EVENT_WAKEUP, it
         * means that the receiver should process the intent normally.
         * I.e. schedule tasks as normal
         */
        const val EVENT_WAKEUP  = "W1"


        const val ACTION_SLEEP = "S1"

        /**
         * Rest from whatever activity the user is curerntly undertaking
         */
        const val ACTION_REST = "R"

        const val ACTION_TIMER = "T"

        const val KEY_ALARM = "A"


        const val WAKE_UP_INTENT_CODE = 1
        const val SLEEP_INTENT_CODE =2

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
                    ,context.createNotificationMainIntentForOnboarding(
                            //TODO LAUNCH VIA INTENT FILTER NAME instead of class name
                            "com.gorillamoa.routines.activity.OnboardActivity"
                    ))
                }

                EVENT_WAKEUP -> {

                    Log.d("onReceive", "EVENT_WAKEUP")

                    //TODO CHECK IF WE HAVEN"T ALREADY RECEIVED THIS EVENT. USE THE DATA LAYER
                    // we don't want to receive two wake up events from both the Alarm and the
                    //event from the network..in which case we should just use the data layer
                    //to manage the synchronization task...

                    TaskScheduler.schedule(context){ taskString ->

                        context.notificationShowWakeUpMirror(taskString)

                        //TODO USE INTENT FILTER NAME
/*
                        context.notificationShowWakeUp(
                                taskString,
                                context.createNotificationMainIntentForWakeUp(),
                                context.createNotificationDeleteIntentForWakeUp()
                        )
*/
                    }
                }

                ACTION_SLEEP ->{

                    Log.d("onReceive","Sleep Alarm went off!")
                    //TODO dismiss other task notifications

                    context.notificationShowSleep()
                    TaskScheduler.endDay(context)

                }

                ACTION_REST -> {


                    Log.d("$tag onReceive","Rest Timer Went off")

                    //TODO ususally if watchface is visible, we'll not show the notification, create
                    //a settings option for  this
//                    context.notificationShowRest()

                    context.saveAlarmRestTriggerStatus(true)
                }

                ACTION_TIMER ->{

                    //show a notification for the timer

                    //TODO Give option to chose this or that, but not both!
                    //TODO ususally if watchface is visible, we'll not show the notification
//                    context.notificationShowTimer()

                    //trigger any other listeners
                    context.saveAlarmTimerTriggerStatus(true)
                }


                else ->{

                    Log.e("onReceive","Alarm Intent did not have ACTION")
                    //TODO create a notification that something went wrong
                }
            }
        }
    }
}