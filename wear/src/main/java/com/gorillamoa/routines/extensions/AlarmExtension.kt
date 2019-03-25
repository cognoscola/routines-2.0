package com.gorillamoa.routines.extensions

import android.app.AlarmManager
import android.content.Context
import java.util.*

/**
 * A place where we can keep extensions regarding alarm
 * setting
 */

//TODO comment some things
/**
 * Convenience method to enable the alarm.
 * The time to set alarm will be fetched from preferences
 */
fun Context.alarmEnableWakeUp(){

    val cal = Calendar.getInstance()
    setSavedWakeTimeToCalendar(cal)
    alarmSetRepeatWithCal(cal,true)
}

/**
 * Convenience disable the Wake up Alarm.
 */
fun Context.alarmDisableWakeUp(){
    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.cancel(createWakeUpAlarmPendingIntent())
    saveAlarmWakeStatus(false)
}

fun Context.alarmEnableSleep(){

    val cal = Calendar.getInstance()
    setSavedSleepTimeToCalendar(cal)
    alarmSetRepeatWithCal(cal,false)
}

fun Context.alarmDisableSleep(){

    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.cancel(createWakeUpAlarmPendingIntent())
    saveAlarmSleepStatus(false)
}



/**
 * Set the repeating alarm with the given calendar
 * @param cal is the calendar object containing information about when to set alarm
 * @param isWake determines wether this alarm will be a wake up or a sleep notification
 */
fun Context.alarmSetRepeatWithCal(cal:Calendar, isWake:Boolean){
    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

    alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            cal.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            if(isWake){createWakeUpAlarmPendingIntent()} else{ createSleepAlarmPendingIntent()}


    )

    saveAlarmWakeStatus(true)
}

