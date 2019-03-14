package com.gorillamoa.routines.extensions

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.gorillamoa.routines.receiver.WakeUpReceiver
import java.util.*

/**
 * A place where we can keep extensions regarding alarm
 * setting
 */

/**
 * Convenience method to enable the alarm.
 * The time to set alarm will be fetched from preferences
 */
fun Context.alarmEnableWakeUp(){

    val cal = Calendar.getInstance()
    setSavedTimeToCalendar(cal)
    alarmSetRepeatWithCal(cal)
}

/**
 * Convenience disable the Wake up Alarm.
 */
fun Context.alarmDisableWakeUp(){
    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.cancel(createWakeUpPendingIntent())
    saveAlarmStatus(false)
}

/**
 * Set the repeating alarm with the given calendar
 */
fun Context.alarmSetRepeatWithCal(cal:Calendar){
    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

    alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            cal.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            createWakeUpPendingIntent()
    )

    saveAlarmStatus(true)
}

