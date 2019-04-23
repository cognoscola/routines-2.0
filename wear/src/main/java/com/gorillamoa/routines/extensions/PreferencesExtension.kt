package com.gorillamoa.routines.extensions

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import java.util.*
import kotlin.collections.ArrayList

private const val LOCAL_SETTINGS ="local_app_settings"
private const val isWakeAlarmActive= "isWakeAlarmActive"
private const val isSleepAlarmActive= "isSleepAlarmActive"
const val isRestAlarmActive = "isRestAlarmActive"

const val isTimerAlarmActive = "isTimerAlarmActive"
const val isTimerAlarmTriggered = "isTimerAlarmTriggered"
const val selectedMinutesTimer = "timerMinute"

//TODO allow user to turn this feature on or off
private const val isActivityRecognictionOn ="isRecognitionOn"

//TODO remove other copies of these values
private const val WAKE_UP_HOUR = "wake_up_hour"
private const val WAKE_UP_MINUTE = "wake_up_minute"
private const val WAKE_PHASE = "wake_phase"

private const val SLEEP_HOUR = "sleep_hour"
private const val SLEEP_MINUTE = "sleep_minute"
private const val SLEEP_PHASE = "sleep_phase"
//TODO END

//Task related information
private const val TASK_INCOMPLETE = "INCOMPLETE" // tasks not yet completed
private const val TASK_COMPLETE = "COMPLETE" //which tasks completed
private const val TASK_ORDER = "order" //task order


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

fun Context.saveTimerTime(timeMillis:Long){

    val prefs = getLocalSettings()
    prefs
            .edit()
            .putLong(selectedMinutesTimer,timeMillis)
            .apply()
}

fun Context.getTimerTime():Long{
    return getLocalSettings().getLong(selectedMinutesTimer,0)
}


//clean truncate these and accept one parameter
fun Context.isWakeAlarmSet():Boolean{
    return getLocalSettings().getBoolean(isWakeAlarmActive,false)
}

fun Context.isSleepAlarmSet():Boolean{
    return getLocalSettings().getBoolean(isSleepAlarmActive,false)
}

fun Context.isRestAlarmActive():Boolean{
    return getLocalSettings().getBoolean(isRestAlarmActive,false)
}

fun Context.isRecognitionOn():Boolean{
    return getLocalSettings().getBoolean(isActivityRecognictionOn,false)
}

fun Context.isTimerAlarmActive():Boolean{
    return getLocalSettings().getBoolean(isTimerAlarmActive,false)
}

//clean truncate these
fun Context.saveRecognitionStatus(isRecogOn:Boolean){
    val prefs = getLocalSettings()
    prefs.edit()
            .putBoolean(isActivityRecognictionOn,isRecogOn)
            .apply()
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

fun Context.saveAlarmRestStatus(isAlarmSet:Boolean){
    val prefs = getLocalSettings()
    prefs.edit()
            .putBoolean(isRestAlarmActive,isAlarmSet)
            .apply()
}

fun Context.saveAlarmTimerStatus(isAlarmSet:Boolean){
    val prefs = getLocalSettings()
    prefs.edit()
            .putBoolean(isTimerAlarmActive,isAlarmSet)
            .apply()
}



/**
 * Specify that the alarm is triggered. All preference listeners will
 * be updated
 */
fun Context.saveAlarmTimerTriggerStatus(isAlarmSet:Boolean){
    val prefs = getLocalSettings()
    prefs.edit()
            .putBoolean(isTimerAlarmTriggered,isAlarmSet)
            .apply()
}


/**
 * save task list
 * @param queue is the task list
 */
fun Context.saveTaskList(queue:ArrayDeque<Int>) {

    try {
        val prefs = getLocalSettings()
        val taskString = queue.joinToString(",")
        Log.d("saveTaskList", "Scheduled Tasks: $taskString")
        prefs.edit()
                .putString(TASK_INCOMPLETE, taskString).apply()

    } catch (e: Exception) {
        Log.e("saveTaskList","Could not update task list",e)
    }

}


/**
 * save completed task list
 * @param queue is the completed task list
 */
fun Context.saveCompletedTaskList(queue:ArrayDeque<Int>) {

    try {
        val prefs = getLocalSettings()
        val taskString = queue.joinToString(",")
        Log.d("saveTaskList", "Scheduled Tasks: $taskString")
        prefs.edit()
                .putString(TASK_COMPLETE, taskString).apply()
    } catch (e: Exception) {
        Log.e("saveCompletedTaskList","Could not update task list",e)
    }

}


fun Context.saveTaskLists(queue:ArrayDeque<Int>,completed:ArrayDeque<Int>){
    val prefs = getLocalSettings()
    prefs.edit().apply{

        var taskString = queue.joinToString(",")
        Log.d("saveTaskList","Scheduled Tasks: $taskString")
        putString(TASK_INCOMPLETE,taskString)

        taskString = completed.joinToString(",")
        Log.d("saveTaskList","Completed Tasks: $taskString")
        putString(TASK_COMPLETE,taskString)

        apply()
    }
}

fun Context.saveOrder(list:ArrayList<Int>){
     getLocalSettings().edit().apply{
         putString(TASK_ORDER,list.joinToString(","))
    }.apply()
}

fun Context.getTaskListKey() = TASK_INCOMPLETE
fun Context.getTaskFinishedKey() = TASK_COMPLETE
fun Context.getOrderKey() = TASK_ORDER


fun Context.getCompletedTaskList():ArrayDeque<Int>{
    return fetchArrayFromPreference(TASK_COMPLETE)
}


/**
 * We get the task list, if there is any.
 */
fun Context.getDayTaskList():ArrayDeque<Int>{
    return fetchArrayFromPreference(TASK_INCOMPLETE)
}

fun Context.getSavedOrder():ArrayList<Int>{
    val prefs = getLocalSettings()
    val taskString = prefs.getString(TASK_ORDER,"-1")
    if (taskString != "-1") {
        val deque = ArrayList<Int>()
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
                return ArrayList()
            }
        }else{
            //only one task today?

            if (taskString.isNotEmpty()) {
                deque.add(taskString.toInt())
            }
        }
        return deque
    }else{
        return ArrayList()
    }
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


