package com.gorillamoa.routines.activity

import android.app.Activity
import android.os.Bundle

import androidx.fragment.app.FragmentActivity
import com.gorillamoa.routines.R
import com.gorillamoa.routines.fragment.GeneralSettingsFragment


//TODO change this to a general activity full of options
//TODO CREATE SETTINGS BUTTON IN THE MAIN ACTIVITY
//TODO CREATE SETTINGS Activity launcher in app launcher
//todo create settings activity launcher from the watchface settings

class SettingsActivity:FragmentActivity(){

     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

         setContentView(R.layout.activity_settings)
         setResult(Activity.RESULT_CANCELED)

         supportFragmentManager.beginTransaction()
                 .add(R.id.fragmentContainer, GeneralSettingsFragment())
                 .commit()

/*
         supportFragmentManager.beginTransaction()
                 .add(R.id.fragmentContainerInsetLayout,TimePickerFragment().apply {

                     Log.d("onCreate","Found Extra ${intent?.getStringExtra(TimePickerFragment.DISPLAY_TEXT)}")
                     arguments =  Bundle().apply {
                         putString(TimePickerFragment.DISPLAY_TEXT, intent?.getStringExtra(TimePickerFragment.DISPLAY_TEXT))
                     }
                     setCallbackFunction { hour, minute,phase  ->

                         setResult(Activity.RESULT_OK, Intent().apply {
                             putExtra(TimePickerFragment.HOUR, hour)
                             putExtra(TimePickerFragment.MIN, minute)
                             putExtra(TimePickerFragment.PHASE, phase)
                         })
                         finish()
                     }
                 })
                 .commit()
*/
    }

}