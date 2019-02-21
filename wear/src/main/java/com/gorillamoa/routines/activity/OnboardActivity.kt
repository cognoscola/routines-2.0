package com.gorillamoa.routines.activity

import android.app.AlarmManager
import android.app.PendingIntent
import android.os.Bundle

import android.support.wearable.activity.WearableActivity
import com.gorillamoa.routines.R
import com.gorillamoa.routines.fragment.InformationFragment
import com.gorillamoa.routines.fragment.SplashFragment
import com.gorillamoa.routines.fragment.TimePickerFragment
import kotlinx.android.synthetic.main.activity_onboard.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/**
 * Created by Guillermo Alvarez Colman 15/02/2019
 *
 * This handles "On boarding" of users when they first enter the app.
 * We need to collect some information so that we can configure the app for them.
 * E.g. What time they wake up - in order to program the alarm
 * E.g. What they they sleep - in order to program the sleep alarm
 *
 * In this context, alarm refers to device wakeup alarm that shows a notification
 * to the user to let them know its time to plan their tasks. It is not necessarily a
 * "Wake up from sleep" alarm.
 *
 */
class OnboardActivity:WearableActivity(){

    //TODO change the activity launcher name (what the user sees)


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_onboard)

        fragmentManager.beginTransaction()
                .add(R.id.fragmentContainerInsetLayout, SplashFragment())
                .commit()

        GlobalScope.launch {

            delay(2000)

            val arguments = Bundle().apply {
                putString(
                        resources.getString(R.string.info_argument_key),
                        resources.getString(R.string.onboard_welcome_text))
            }
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerInsetLayout, InformationFragment().apply {
                        setArguments(arguments)
                    } )
                    .commit()
        }

        fragmentContainerInsetLayout.setOnClickListener {

            fragmentContainerInsetLayout.setOnClickListener(null)
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerInsetLayout, TimePickerFragment())
                    .commit()
        }
    }

}