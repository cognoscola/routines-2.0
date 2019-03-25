package com.gorillamoa.routines.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
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

                     arguments?.putString(TimePickerFragment.DISPLAY_TEXT, intent?.getStringExtra(TimePickerFragment.DISPLAY_TEXT))
                     setCallbackFunction { hour, minute ->

                         setResult(Activity.RESULT_OK, Intent().apply {
                             putExtra(TimePickerFragment.HOUR, hour)
                             putExtra(TimePickerFragment.MIN, minute)
                         })
                         finish()
                     }

                 })
                 .commit()
    }

}