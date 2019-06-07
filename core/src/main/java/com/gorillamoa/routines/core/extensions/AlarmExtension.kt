package com.gorillamoa.routines.core.extensions

import android.app.AlarmManager
import android.content.Context
import com.gorillamoa.routines.core.receiver.SimpleBootReceiver
import java.util.*

/**
 * A place where we can keep extensions regarding alarm
 * setting
 */

//TODO comment some things

/**
 * Will enable the alarm to be set despite device shutdown
 */
fun Context.alarmEnableWakeUpPersistent(){

    alarmEnableWakeUp()
    SimpleBootReceiver.enableBootReceiver(this)
}

fun Context.alarmEnableSleepPersistent(){
    alarmEnableSleep()
    SimpleBootReceiver.enableBootReceiver(this)
}

fun Context.alarmDisableWakePersistent(){
    alarmDisableWakeUp()
    //don't forget to disable boot receiver
    SimpleBootReceiver.disableBootReciver(this)
}

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

    //clean
    if(isWake)saveAlarmWakeStatus(true)else saveAlarmSleepStatus(true)
}

fun Context.getAlarmService():AlarmManager{
    return getSystemService(Context.ALARM_SERVICE) as AlarmManager
}

