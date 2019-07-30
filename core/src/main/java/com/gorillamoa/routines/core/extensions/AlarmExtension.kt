package com.gorillamoa.routines.core.extensions

import android.app.AlarmManager
import android.content.Context
import com.gorillamoa.routines.core.receiver.SimpleBootReceiver
import java.util.*


/**
 * Will enable the alarm to be set despite device shutdown
 */
fun Context.alarmEnableWakeUpPersistent(){

    alarmEnableWakeUp()
    SimpleBootReceiver.enableBootReceiver(this)
}

/**
 * Enable sleep alarm to be set despite device shutdown
 * @receiver Context
 */
fun Context.alarmEnableSleepPersistent(){
    alarmEnableSleep()
    SimpleBootReceiver.enableBootReceiver(this)
}

/**
 * Disable persistent alarms
 * @receiver Context
 */
fun Context.alarmDisableWakePersistent(){
    alarmDisableWakeUp()
    //don't forget to disable boot receiver
    SimpleBootReceiver.disableBootReciver(this)
}

/**
 * disable persistent ability of alarm
 * @receiver Context
 */
fun Context.alarmDisableSleepPersistent(){
    alarmDisableSleep()
    //don't forget to disable boot receiver
    SimpleBootReceiver.disableBootReciver(this)
}


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
//TODO split
fun Context.alarmDisableWakeUp(){
    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.cancel(createWakeUpAlarmPendingIntent())
    saveAlarmWakeStatus(false)
}

fun Context.alarmEnableSleep(){

    val cal = Calendar.getInstance()
    setSavedSleepTimeToCalendar(cal)
    alarmSetRepeatWithCal(cal,true)
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
    val alarmManager = getAlarmService()

    alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            cal.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            if(isWake){createWakeUpAlarmPendingIntent()} else{ createSleepAlarmPendingIntent()}
    )

    if(isWake)saveAlarmWakeStatus(true)else saveAlarmSleepStatus(true)
}

fun Context.getAlarmService():AlarmManager{
    return getSystemService(Context.ALARM_SERVICE) as AlarmManager
}

