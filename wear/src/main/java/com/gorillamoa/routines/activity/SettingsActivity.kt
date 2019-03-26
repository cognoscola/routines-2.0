package com.gorillamoa.routines.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.gorillamoa.routines.R
import com.gorillamoa.routines.fragment.TimePickerFragment


//TODO change this to a general activity full of options
class SettingsActivity:FragmentActivity(){

     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

         setContentView(R.layout.activity_settings)
         setResult(Activity.RESULT_CANCELED)

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
    }

}