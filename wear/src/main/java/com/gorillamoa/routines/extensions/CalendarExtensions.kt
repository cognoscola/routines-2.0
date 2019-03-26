package com.gorillamoa.routines.extensions

import android.content.Context
import java.util.*

private const val WAKE_UP_HOUR = "wake_up_hour"
private const val WAKE_UP_MINUTE = "wake_up_minute"
private const val WAKE_PHASE = "wake_phase"
private const val SLEEP_HOUR = "sleep_hour"
private const val SLEEP_MINUTE = "sleep_minute"
private const val SLEEP_PHASE = "sleep_phase"

//TODO finish commenting these functions
//TODO truncate these codes


fun Context.setSavedWakeTimeToCalendar(cal: Calendar){

    val prefs = getLocalSettings()
    cal.setAlarmTime(prefs.getInt(WAKE_UP_HOUR,-1),prefs.getInt(WAKE_UP_MINUTE,-1),prefs.getInt(WAKE_PHASE,0))
}

fun Context.setWakeTimeToCalendarAndStore(cal: Calendar, hour:Int, minute:Int,phase:Int){

    cal.setAlarmTime(hour,minute,phase)
    saveWakeTime(hour,minute,phase)
}

fun Context.setSavedSleepTimeToCalendar(cal: Calendar){

    val prefs = getLocalSettings()
    cal.setAlarmTime(prefs.getInt(SLEEP_HOUR,-1),prefs.getInt(SLEEP_MINUTE,-1), prefs.getInt(SLEEP_PHASE,0))

}


fun Context.setSleepTimeToCalendarAndStore(cal: Calendar, hour:Int, minute:Int,phase:Int){

    cal.setAlarmTime(hour,minute,phase)
    saveSleepTime(hour,minute,phase)
}


fun Calendar.setAlarmTime(hour:Int,minute:Int,phase:Int){
    timeInMillis = System.currentTimeMillis()
    set(Calendar.AM_PM,phase)
    set(Calendar.HOUR, hour)
    set(Calendar.MINUTE,minute)
    add(Calendar.DATE,1) //specify to fire TOMORROW
}