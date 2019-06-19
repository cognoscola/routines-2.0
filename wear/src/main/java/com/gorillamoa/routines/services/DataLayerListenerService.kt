package com.gorillamoa.routines.services

import android.util.Log
import android.widget.Toast
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import com.gorillamoa.routines.core.extensions.broadcastShowWakeUp
import com.gorillamoa.routines.core.extensions.notificationShowWakeUp
import com.gorillamoa.routines.core.scheduler.TaskScheduler

/**
 * Listens for data changes (in case we are synchronized with the mobile)
 */

//TODO SYNCRHONIZE WAKE UP EVENT
//TODO SYNCRHONIZE TASK ACTIONS FINISH/UNFINISH/SKIP
//TODO SYNCRHONIZE HISTORY
//TODO SYNCHRONIZE CURRENT VISIBLE TASK
//TODO SYNCHRONIZE SLEEP

class DataLayerListenerService:WearableListenerService(){

    @Suppress("unused")
    private val tag:String = DataLayerListenerService::class.java.name

    companion object {

        /**
         * Another device has received the wake up event, so lets show the wake up
         * event here as well
         */
        const val EVENT_WAKEUP = "event.wakeup"


        //TODO we need to monitor the data layer isWakeUpShowing variable. whenever it changes we just behave accordingly
        //much simpler than sending alot of messages across
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

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)

        // Check to see if the message is to launch the
        when (messageEvent.path) {
            EVENT_WAKEUP -> {

                TaskScheduler.schedule(this) {
                    notificationShowWakeUp(
                            it,
                            null,
                            null,
                            false,
                            smallRemoteView = null,
                            bigRemoteView = null)

                }
            }
            else -> {
            }
        }
    }


    override fun onDataChanged(dataEvents: DataEventBuffer) {
       Log.d("$tag onDataChanged","$dataEvents")

        Toast.makeText(applicationContext,"Received",Toast.LENGTH_SHORT).show()

        dataEvents.forEach {

            when (it.type) {
                DataEvent.TYPE_CHANGED -> {



                }
                DataEvent.TYPE_DELETED ->{

                }
                else -> {

                }
            }
        }
    }

}