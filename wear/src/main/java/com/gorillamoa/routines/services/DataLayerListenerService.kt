package com.gorillamoa.routines.services

import android.util.Log
import android.widget.Toast
import com.google.android.gms.wearable.*
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

        /**
         * Another device has received the wake up event, so lets show the wake up
         * event here as well
         */
        const val EVENT_WAKEUP = "event.wakeup"


        //TODO we need to monitor the data layer isWakeUpShowing variable. whenever it changes we just behave accordingly
        //much simpler than sending alot of messages across
        /**
         * Determine wether the wake up notification should show
         */
        const val isWakeShowing = "event.wakeup.visibility"


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
                    it.dataItem.also {item ->
                        if (item.uri.path.compareTo("/day") == 0) {
                            DataMapItem.fromDataItem(item).dataMap.apply {
                                if(getBoolean(isWakeShowing,false)){
                                   broadcastShowWakeUp()
                                }else{
                                    getNotificationManager().cancel(NOTIFICATION_TAG, WAKE_UP_NOTIFICATION_ID)
                                }
                            }
                        }
                    }

                }
                DataEvent.TYPE_DELETED ->{

                }
                else -> {

                }
            }
        }
    }

}