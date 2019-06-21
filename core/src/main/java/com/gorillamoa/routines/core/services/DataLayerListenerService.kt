package com.gorillamoa.routines.core.services

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.Log
import com.google.android.gms.wearable.*
import com.gorillamoa.routines.core.constants.DataLayerConstant
import com.gorillamoa.routines.core.constants.DataLayerConstant.Companion.KEY_TASK_DATA
import com.gorillamoa.routines.core.data.Task
import com.gorillamoa.routines.core.extensions.*

import com.gorillamoa.routines.core.scheduler.TaskScheduler

/**
 * Listens for data changes (in case we are synchronized with the mobile)
 */

//TODO SYNCRHONIZE WAKE UP EVENT
//TODO SYNCRHONIZE TASK ACTIONS FINISH/UNFINISH/SKIP
//TODO SYNCRHONIZE HISTORY
//TODO SYNCHRONIZE CURRENT VISIBLE TASK
//TODO SYNCHRONIZE SLEEP

//TODO make this Listener available to both mobile and wear,
//and that way mobile can be updated when Wear commands are taken

class DataLayerListenerService:WearableListenerService(){

    @Suppress("unused")
    private val tag:String = DataLayerListenerService::class.java.name

    companion object {

        //TODO we need to monitor the data layer isWakeUpShowing variable. whenever it changes we just behave accordingly
        //much simpler than sending alot of messages across
        /**
         * Determine wether the wake up notification should show
         */
        const val EVENT_WAKEUP = "event.wakeup.visibility"


        /**
         * The device should hide the notification
         */
        const val EVENT_HIDE_WAKEUP = "event.wakeup.hide"

        /**
         * Another device has received the sleep event, so lets show the sleep
         * notification here as well
         */
        const val EVENT_SLEEP = "event.sleep"

        /**
         * A task has its history updated. Update the local database task
         */
        const val EVENT_TASK_HISTORY_CHANGED = "event.task_info_change"

        /**A task has its info changed, requires synchronization
         * One of: frequency, name, type has changed so update the local database
         */
        const val EVENT_TASK_INFO_CHANGED ="event.task_settings_change"

        /**
         * The user's history info has updated
         * One of: wake history, sleep history, task completion history
         */
        const val EVENT_USER_HISTORY_UPDATED = "event.user_history_UPDATED"

        /**
         * The user has reacted to the wake up event by Starting the day.
         * This action will require syncing of completed/uncompleted task lists
         */
        const val ACTION_WAKE = "action.startday"

        /**
         * The user has reacted to the sleep event.
         * mark what the user has done: One of Sleep, snooze the sleep
         */
        const val ACTION_SLEEP = "action.sleep"

        /**
         * The user has marked a task finished.
         * Sync completed/uncompleted task lists
         */
        const val ACTION_DONE = "action.done"

        /**
         * The user has marked a task unfinished.
         * Sync the completed/uncompleted task list
         */
        const val ACTION_UNFINISHED = "action.uncomplete"

        /**
         * The user is now viewing a different task.
         * Sync the "current" viewing task
         */
        const val ACTION_VIEW_TASK = "action.viewchange"
    }


    override fun onDataChanged(dataEvents: DataEventBuffer) {
        Log.d("$tag onDataChanged ", "$dataEvents")
        Log.d("$tag onDataChanged ", "I AM ${if (isWatch()) "Watch" else "MOBILE"}")

        dataEvents.forEach {

            Log.d("$tag onDataChanged", "Host: ${it.dataItem.uri.host}")

            val dataMap = DataMapItem.fromDataItem(it.dataItem).dataMap
            when (it.type) {
                DataEvent.TYPE_CHANGED -> {

                    //first lets get the data if any
                    val taskData = dataMap.getString(KEY_TASK_DATA)

                    //In Any case we'll check the time it was issued

                    Log.d("$tag onDataChanged", " Changed Time Issued: ${dataMap.getString(DataLayerConstant.KEY_TIME)}")

                    if (DataLayerConstant.WAKE_UP_PATH.equals(it.dataItem.uri.path)) {

                        //We'll use the scheduler to get the task list,
                        //TODO but we must synchronize task completion data somewhere else
                        TaskScheduler.schedule(this){ tasks ->
                            tasks?.let{
                                Log.d("notificationRoutine","onDataChanged")

                                if (!isAlreadyShowing(WAKE_UP_NOTIFICATION_ID)) {
                                    notificationShowWakeUpLocal(it)
                                }
                            }
                        }
                    }

                    else if(DataLayerConstant.TASK_PATH.equals(it.dataItem.uri.path)){

                        //Here task data is of type task
                        val task = getGson().fromJson(taskData, Task::class.java)
                        if (!isAlreadyShowing(task.id!!)) {
                            notificationShowTaskLocal(task)
                        }
                    }
                }
                DataEvent.TYPE_DELETED -> {

                    //TODO test situation where we may get a delete issue after a
                    //new notification issue b/c of network lag
                    Log.d("notificationRoutine","onDataChanged Delete issued")

                    if (DataLayerConstant.WAKE_UP_PATH.equals(it.dataItem.uri.path)) {
                        Log.d("notificationRoutine","WAKE")
                        notificationDismissWakeUp()
                    }else if (DataLayerConstant.TASK_PATH.equals(it.dataItem.uri.path)){

                        Log.d("notificationRoutine","TASK")
                        getAllTaskShowing().forEach {

                            //we only show 1 task, so we'll take this opportunity to dismiss ALL
                            //task notifications
                            notificationDismissTask(it.id)
                        }
                    }
                }
                else -> {

                }
            }
        }
    }

    @TargetApi(23)
    private fun isAlreadyShowing(id:Int):Boolean {
        return if (getNotificationManager().activeNotifications.find {
                    //Check if we aren't already displaying a notification
                    Log.d("notificationRoutine", "onDataChanged check: ${it.id}")
                    it.id == id
                } == null) {
            false
        } else {
            Log.d("notificationRoutine", "onDataChanged We're already showing $id")
            true
        }


    }


}