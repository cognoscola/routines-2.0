package com.gorillamoa.routines.extensions

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import java.util.*

private const val LOCAL_SETTINGS ="local_app_settings"
private const val isWakeAlarmActive= "isWakeAlarmActive"
private const val isSleepAlarmActive= "isSleepAlarmActive"

private const val WAKE_UP_HOUR = "wake_up_hour"
private const val WAKE_UP_MINUTE = "wake_up_minute"

private const val SLEEP_HOUR = "slee_hour"
private const val SLEEP_MINUTE = "sleep_minute"


//Task related information
private const val TASK_ORDER = "order"
private const val TOTAL_ASSIGNED  = "assigned"


private const val READY_TO_APPROVE = "ready"

/**
 * The number of tasks completed for the day. Note that this value is 'locked'
 * if it is -1. Change it to another value to unlock it
 */
private const val TOTAL_COMPLETED  = "completed"



fun Context.getLocalSettings():SharedPreferences{

    //later investigate cost of retrieving shared preferences
    return getSharedPreferences(LOCAL_SETTINGS, Activity.MODE_PRIVATE)
}

fun Context.getWakeUpHour():Int = getLocalSettings().getInt(WAKE_UP_HOUR,-1)
fun Context.getWakeUpMinute():Int = getLocalSettings().getInt(WAKE_UP_MINUTE,-1)



/**
 * Convenient method save into preferences the alarm time
 */
fun Context.saveWakeTime(hour:Int, minute:Int){
    val prefs= getLocalSettings()
    prefs
            .edit()
            .putInt(WAKE_UP_HOUR,hour)
            .putInt(WAKE_UP_MINUTE,minute)
            .apply()
}

fun Context.saveSleepTime(hour:Int, minute:Int){
    val prefs= getLocalSettings()
    prefs
            .edit()
            .putInt(SLEEP_HOUR,hour)
            .putInt(SLEEP_MINUTE,minute)
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

fun Context.isReadyToApprove():Boolean{
    return getLocalSettings().getBoolean(READY_TO_APPROVE,false)
}

fun Context.setReadyToApprove(){
    getLocalSettings().edit().putBoolean(READY_TO_APPROVE,true).apply()
}

fun Context.cancelApproval(){
    getLocalSettings().edit().putBoolean(READY_TO_APPROVE,false).apply()
}



fun Context.resetStats(count:Int){
    val settings = getLocalSettings()
    settings.edit()
            .putInt(TOTAL_ASSIGNED, count)
            .putInt(TOTAL_COMPLETED, 0)
            .apply()

}

fun Context.incrementCompletionCount(){
    val settings = getLocalSettings()
    var count = settings.getInt(TOTAL_COMPLETED,-1)
    if (count != -1) {
        count++
        settings.edit().putInt(TOTAL_COMPLETED,count).apply()
    }
}


fun Context.getCompletionCountToday():Int{
    return getLocalSettings().getInt(TOTAL_COMPLETED,-1)
}
fun Context.getTotalAssignedToday():Int{
    return getLocalSettings().getInt(TOTAL_ASSIGNED,-1)
}

