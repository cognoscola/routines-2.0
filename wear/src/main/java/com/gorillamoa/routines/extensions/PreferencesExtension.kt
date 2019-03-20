package com.gorillamoa.routines.extensions

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import java.util.*

private const val LOCAL_SETTINGS ="local_app_settings"
private const val isAlarmActive= "isAlarmActive"

private const val WAKE_UP_HOUR = "wake_up_hour"
private const val WAKE_UP_MINUTE = "wake_up_minute"
private const val TASK_ORDER = "task_order"

fun Context.getLocalSettings():SharedPreferences{

    //later investigate cost of retrieving shared preferences
    return getSharedPreferences(LOCAL_SETTINGS, Activity.MODE_PRIVATE)
}

fun Context.getWakeUpHour():Int = getLocalSettings().getInt(WAKE_UP_HOUR,-1)
fun Context.getWakeUpMinute():Int = getLocalSettings().getInt(WAKE_UP_MINUTE,-1)



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

fun Context.saveAlarmStatus(isAlarmSet:Boolean){
    val prefs = getLocalSettings()
    prefs.edit()
            .putBoolean(isAlarmActive,isAlarmSet)
            .apply()
}

fun Context.saveTaskList(queue:ArrayDeque<Int>){
    if (queue.size > 0) {

        val prefs = getLocalSettings()
        prefs.edit()
            .putString(TASK_ORDER,queue.joinToString(","))
            .apply()
    }
}



