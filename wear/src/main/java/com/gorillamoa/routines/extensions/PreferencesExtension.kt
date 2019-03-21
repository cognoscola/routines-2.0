package com.gorillamoa.routines.extensions

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
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
        val taskString = queue.joinToString(",")
        Log.d("saveTaskList","Scheduled Tasks: $taskString")
        prefs.edit()
            .putString(TASK_ORDER,taskString)
            .apply()
    }
}

/**
 * We get the task list, if there is any.
 */
fun Context.getDayTaskList():ArrayDeque<Int>{

    val prefs = getLocalSettings()
    val taskString = prefs.getString(TASK_ORDER,"-1")
    if (taskString != "-1") {
        val deque = ArrayDeque<Int>()
        if (taskString!!.contains(",")) {

            try {
                val sequence = taskString.split(",")
                sequence.forEach {
                    try {
                        deque.add(it.toInt())
                    } catch (e: IllegalArgumentException) {
                        Log.d("getDayTaskList", "Tried to convert numbers", e)
                    }
                }
            } catch (e: Exception) {
                Log.d("getDayTaskList","",e)
                return ArrayDeque<Int>().apply { add(-1) }
            }
        }else{
            //only one task today
            deque.add(taskString.toInt())
        }
        return deque
    }else{
        return ArrayDeque<Int>().apply {
          add(-1)
        }
    }
}

fun Context.getCurrentTask():Int{
    return getDayTaskList().first
}

