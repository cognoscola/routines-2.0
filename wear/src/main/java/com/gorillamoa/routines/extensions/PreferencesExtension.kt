package com.gorillamoa.routines.extensions

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import java.util.*

private const val LOCAL_SETTINGS ="local_app_settings"
private const val isWakeAlarmActive= "isWakeAlarmActive"
private const val isSleepAlarmActive= "isSleepAlarmActive"

//TODO remove other copies of these values
private const val WAKE_UP_HOUR = "wake_up_hour"
private const val WAKE_UP_MINUTE = "wake_up_minute"
private const val WAKE_PHASE = "wake_phase"

private const val SLEEP_HOUR = "sleep_hour"
private const val SLEEP_MINUTE = "sleep_minute"
private const val SLEEP_PHASE = "sleep_phase"
//TODO END

//Task related information
private const val TASK_ORDER = "order" //also serves as tasks not yet completed
private const val TASK_DONE = "done" //which tasks completed


private const val IS_ACTIVE = "ready" //wether the app is currently working on not


fun Context.getLocalSettings():SharedPreferences{
    //later investigate cost of retrieving shared preferences
    return getSharedPreferences(LOCAL_SETTINGS, Activity.MODE_PRIVATE)
}



/**
 * Convenient method save into preferences the alarm time
 */

//TODO Truncate these 2 methods below
fun Context.saveWakeTime(hour:Int, minute:Int,phase:Int){
    val prefs= getLocalSettings()
    prefs
            .edit()
            .putInt(WAKE_UP_HOUR,hour)
            .putInt(WAKE_UP_MINUTE,minute)
            .putInt(WAKE_PHASE,phase)
            .apply()
}

fun Context.saveSleepTime(hour:Int, minute:Int,phase:Int){
    val prefs= getLocalSettings()
    prefs
            .edit()
            .putInt(SLEEP_HOUR,hour)
            .putInt(SLEEP_MINUTE,minute)
            .putInt(SLEEP_PHASE,phase)
            .apply()
}


fun Context.isWakeAlarmSet():Boolean{
    return getLocalSettings().getBoolean(isWakeAlarmActive,false)
}

fun Context.isSleepAlarmSet():Boolean{
    return getLocalSettings().getBoolean(isSleepAlarmActive,false)
}

fun Context.saveAlarmWakeStatus(isAlarmSet:Boolean){
    val prefs = getLocalSettings()
    prefs.edit()
            .putBoolean(isWakeAlarmActive,isAlarmSet)
            .apply()
}

fun Context.saveAlarmSleepStatus(isAlarmSet:Boolean){
    val prefs = getLocalSettings()
    prefs.edit()
            .putBoolean(isSleepAlarmActive,isAlarmSet)
            .apply()
}



/**
 * save task list
 * @param queue is the task list
 */
fun Context.saveTaskList(queue:ArrayDeque<Int>){
    if (queue.size > 0) {

        val prefs = getLocalSettings()
        val taskString = queue.joinToString(",")
        Log.d("saveTaskList","Scheduled Tasks: $taskString")
        prefs.edit()
            .putString(TASK_ORDER,taskString).apply()
    }
}

fun Context.saveTaskLists(queue:ArrayDeque<Int>,completed:ArrayDeque<Int>){
    val prefs = getLocalSettings()
    prefs.edit().apply{

        var taskString = queue.joinToString(",")
        Log.d("saveTaskList","Scheduled Tasks: $taskString")
        putString(TASK_ORDER,taskString)

        taskString = completed.joinToString(",")
        Log.d("saveTaskList","Completed Tasks: $taskString")
        putString(TASK_DONE,taskString)

        apply()
    }
}


fun Context.getCompletedTaskList():ArrayDeque<Int>{
    return fetchArrayFromPreference(TASK_DONE)
}

/**
 * We get the task list, if there is any.
 */
fun Context.getDayTaskList():ArrayDeque<Int>{
    return fetchArrayFromPreference(TASK_ORDER)
}

fun Context.fetchArrayFromPreference(listName:String):ArrayDeque<Int>{
    val prefs = getLocalSettings()
    val taskString = prefs.getString(listName,"-1")
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
                return ArrayDeque()
            }
        }else{
            //only one task today?

            if (taskString.isNotEmpty()) {
                deque.add(taskString.toInt())
            }
        }
        return deque
    }else{
        return ArrayDeque()
    }
}

fun Context.isEnabled(){
    getLocalSettings().getBoolean(IS_ACTIVE,false)
}

fun Context.EnableScheduler(){
    getLocalSettings().edit().putBoolean(IS_ACTIVE,true).apply()
}

fun Context.DisableScheduler(){
    getLocalSettings().edit().putBoolean(IS_ACTIVE,false).apply()
}


