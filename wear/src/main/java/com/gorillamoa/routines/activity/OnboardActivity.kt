package com.gorillamoa.routines.activity

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle

import android.support.wearable.activity.WearableActivity

import android.widget.Button
import com.gorillamoa.routines.R
import com.gorillamoa.routines.fragment.InformationFragment
import com.gorillamoa.routines.fragment.SplashFragment
import com.gorillamoa.routines.fragment.TimePickerFragment
import com.gorillamoa.routines.receiver.WakeUpReceiver
import kotlinx.android.synthetic.main.activity_onboard.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

/**
 * Created by Guillermo Alvarez Colman 15/02/2019
 *
 * This handles "On boarding" of users when they first enter the app.
 * We need to collect some information so that we can configure the app for them.
 * E.g. What time they wake up - in order to program the alarm
 * E.g. What they they sleep - in order to program the sleep alarm
 *
 * In this context, alarm refers to device wakeup alarm that shows a notification
 * to the user to let them know its time to plan their tasks. It is not necesserily a
 * "Wake up from sleep" alarm.
 *
 */
class OnboardActivity:WearableActivity(){

    //TODO change the activity launcher name (what the user sees)

    var hasWakeupTime = false

    private var alarmManager: AlarmManager? = null
    private lateinit var alarmIntent: PendingIntent
    private val WAKEUP_REQUESTCODE_TEST = 0

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_onboard)

        fragmentManager.beginTransaction()
                .add(R.id.fragmentContainerInsetLayout, SplashFragment())
                .commit()

        GlobalScope.launch {

            delay(2000)
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerInsetLayout, InformationFragment())
                    .commit()
        }

        fragmentContainerInsetLayout.setOnClickListener {

            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerInsetLayout, TimePickerFragment())
                    .commit()
        }

       //TODO move this stuff elsewhere

//        val timePicker = findViewById<TimePicker>(R.id.wakeuptTimePicker)
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmIntent = Intent(this, WakeUpReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(this, WAKEUP_REQUESTCODE_TEST, intent, 0)
        }

        //TODO create a better time picker than what cucrently exists

        /** Get the wake up and sleep times of the user, via the time picker*/
        findViewById<Button>(R.id.nextButton).setOnClickListener {

            if (!hasWakeupTime) {

                //we don't really need to cancel anything. If we use the
                //same request code, the current alarm will be overridden

                // Set the alarm to start at approximately the time the user indicated
                val calendar: Calendar = Calendar.getInstance().apply {
                    timeInMillis = System.currentTimeMillis()
                    //temporarily ring 30 seconds ahead
                    timeInMillis += 1000
//                    set(Calendar.HOUR_OF_DAY, 14)
                }


                // With setInexactset(Calendar.HOUR_OF_DAY, 14)
                //                }Repeating(), you have to use one of the AlarmManager interval
                // constants--in this case, AlarmManager.INTERVAL_DAY.
                alarmManager?.set(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        alarmIntent
                )

/*
                 alarmManager?.setInexactRepeating(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        AlarmManager.INTERVAL_DAY,
                        alarmIntent
                )*/

            }else{


                //TODO set the sleep alarms

            }
        }
    }


}