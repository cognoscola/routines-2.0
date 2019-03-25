package com.gorillamoa.routines.extensions

import android.content.Context
import java.util.*

private const val WAKE_UP_HOUR = "wake_up_hour"
private const val WAKE_UP_MINUTE = "wake_up_minute"
private const val SLEEP_HOUR = "slee_hour"
private const val SLEEP_MINUTE = "sleep_minute"


fun Context.setSavedWakeTimeToCalendar(cal: Calendar){

    val prefs = getLocalSettings()
    cal.setAlarmTime(prefs.getInt(WAKE_UP_HOUR,-1),prefs.getInt(WAKE_UP_MINUTE,-1))
}

fun Context.setWakeTimeToCalendarAndStore(cal: Calendar, hour:Int, minute:Int){

    cal.setAlarmTime(hour,minute)
    saveWakeTime(hour,minute)
}

fun Context.setSavedSleepTimeToCalendar(cal: Calendar){

    val prefs = getLocalSettings()
    cal.setAlarmTime(prefs.getInt(SLEEP_HOUR,-1),prefs.getInt(SLEEP_MINUTE,-1))

}

fun Context.setSleepTimeToCalendarAndStore(cal: Calendar, hour:Int, minute:Int){

    cal.setAlarmTime(hour,minute)
    saveSleepTime(hour,minute)
}

fun Calendar.setAlarmTime(hour:Int,minute:Int){
    timeInMillis = System.currentTimeMillis()
    set(Calendar.HOUR_OF_DAY, hour)
    set(Calendar.MINUTE,minute)
    add(Calendar.DATE,1) //specify to fire TOMORROW
}