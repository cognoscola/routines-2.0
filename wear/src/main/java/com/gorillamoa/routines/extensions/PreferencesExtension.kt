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

fun Context.decrementCompletionCount(){
    val settings = getLocalSettings()
    var count = settings.getInt(TOTAL_COMPLETED,-1)
    if (count != -1) {
        count--
        if(count < 0 ) count =0
        settings.edit().putInt(TOTAL_COMPLETED,count).apply()
    }
}


fun Context.getCompletionCountToday():Int{
    return getLocalSettings().getInt(TOTAL_COMPLETED,-1)
}
fun Context.getTotalAssignedToday():Int{
    return getLocalSettings().getInt(TOTAL_ASSIGNED,-1)
}

