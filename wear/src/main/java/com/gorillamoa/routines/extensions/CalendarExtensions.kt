package com.gorillamoa.routines.extensions

import android.content.Context
import java.util.*

private const val WAKE_UP_HOUR = "wake_up_hour"
private const val WAKE_UP_MINUTE = "wake_up_minute"

//TODO figure out how to get const from files
fun Context.setSavedTimeToCalendar(cal: Calendar){

    val prefs = getLocalSettings()
    cal.timeInMillis = System.currentTimeMillis()
    cal.set(Calendar.HOUR_OF_DAY, prefs.getInt(WAKE_UP_HOUR,-1))
    cal.set(Calendar.MINUTE,prefs.getInt(WAKE_UP_MINUTE,-1))
    cal.add(Calendar.DATE,1) //specify to fire TOMORROW
}

fun Context.setTimeToCalendarAndStore(cal: Calendar, hour:Int, minute:Int){

    cal.timeInMillis = System.currentTimeMillis()
    cal.set(Calendar.HOUR_OF_DAY, hour)
    cal.set(Calendar.MINUTE,minute)
    cal.add(Calendar.DATE,1) //specify to fire TOMORROW
    saveAlarmTime(hour,minute)
}