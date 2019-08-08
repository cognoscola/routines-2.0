package com.gorillamoa.routines.tools.counter

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast

private const val COUNTER_SETTINGS = "local_counter_settings"
private const val COUNTER_VALUE = "val"

private fun Context.getCounterSettings():SharedPreferences{
    return getSharedPreferences(COUNTER_SETTINGS, Activity.MODE_PRIVATE)
}

fun Context.counterIncrementValue(display:Boolean = false){

    getCounterSettings().apply {

        var value = getInt(COUNTER_VALUE,0)
        value++
        edit().putInt(COUNTER_VALUE,value).apply()
        if(display) Toast.makeText(this@counterIncrementValue,value,Toast.LENGTH_SHORT).show()
    }
}

fun Context.counterSetValue(value:Int){

    getCounterSettings().apply {
        edit().putInt(COUNTER_VALUE,value).apply()
    }
}

fun Context.counterReset(){
    counterSetValue(0)
}