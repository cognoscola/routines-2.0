package com.gorillamoa.routines.provider

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import java.util.*

private const val LOCAL_SETTINGS ="local_app_settings"
private const val isAlarmActive= "isAlarmActive"

private const val WAKE_UP_HOUR = "wake_up_hour"
private const val WAKE_UP_MINUTE = "wake_up_minute"

fun Context.getLocalSettings():SharedPreferences{

    //TODO investigate cost of retreaving shared preferences
    return getSharedPreferences(LOCAL_SETTINGS, Activity.MODE_PRIVATE)
}

fun Context.getWakeUpHour():Int = getLocalSettings().getInt(WAKE_UP_HOUR,-1)
fun Context.getWakeUpMinute():Int = getLocalSettings().getInt(WAKE_UP_MINUTE,-1)

/**
 * Convenient method for setting the HOUR and MINUTE to a calendar
 * @param cal is the calendar to set
 */
fun Context.applySavedTimeToCalendar(cal:Calendar){

    val prefs = getLocalSettings()

    cal.set(Calendar.HOUR_OF_DAY, prefs.getInt(WAKE_UP_HOUR,-1))
    cal.set(Calendar.MINUTE,prefs.getInt(WAKE_UP_MINUTE,-1))

}

/**
 * Convenient method save into preferences the alarm time
 */
fun Context.saveAlarmTime(hour:Int,minute:Int){
    val prefs= getLocalSettings()
    prefs
            .edit()
            .putInt(WAKE_UP_HOUR,hour)
            .putInt(WAKE_UP_MINUTE,minute)
            .apply()
}

fun Context.isAlarmSet():Boolean{
    return getLocalSettings().getBoolean(isAlarmActive,false)
}



