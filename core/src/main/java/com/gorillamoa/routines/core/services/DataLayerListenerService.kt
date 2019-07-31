package com.gorillamoa.routines.core.services

import android.annotation.TargetApi
import android.content.Context
import android.net.Uri

import android.util.Log
import com.google.android.gms.wearable.*
import com.gorillamoa.routines.core.constants.DataLayerConstant
import com.gorillamoa.routines.core.constants.DataLayerConstant.Companion.KEY_TASK_DATA
import com.gorillamoa.routines.core.data.Task
import com.gorillamoa.routines.core.extensions.*

import com.gorillamoa.routines.core.scheduler.TaskScheduler
import com.gorillamoa.routines.core.constants.DataLayerConstant.Companion.DATA_TASK_MOBILE_DELETE_PATH
import com.gorillamoa.routines.core.constants.DataLayerConstant.Companion.DATA_TASK_MOBILE_INSERT_PATH
import com.gorillamoa.routines.core.constants.DataLayerConstant.Companion.DATA_TASK_MOBILE_UPDATE_PATH
import com.gorillamoa.routines.core.constants.DataLayerConstant.Companion.DATA_TASK_WEAR_DELETE_PATH
import com.gorillamoa.routines.core.constants.DataLayerConstant.Companion.DATA_TASK_WEAR_INSERT_PATH
import com.gorillamoa.routines.core.constants.DataLayerConstant.Companion.DATA_TASK_WEAR_UPDATE_PATH
import com.gorillamoa.routines.core.constants.DataLayerConstant.Companion.KEY_PROGRESS_ACTIVE
import com.gorillamoa.routines.core.constants.DataLayerConstant.Companion.KEY_PROGRESS_COMPLETED
import com.gorillamoa.routines.core.constants.DataLayerConstant.Companion.KEY_PROGRESS_ORDER
import com.gorillamoa.routines.core.constants.DataLayerConstant.Companion.KEY_PROGRESS_UNCOMPLETED
import com.gorillamoa.routines.core.constants.DataLayerConstant.Companion.KEY_TASK_HISTORY_DATA
import com.gorillamoa.routines.core.constants.DataLayerConstant.Companion.PROGRESS_PATH
import com.gorillamoa.routines.core.constants.DataLayerConstant.Companion.TASK_PATH

import com.gorillamoa.routines.core.coroutines.Coroutines
import com.gorillamoa.routines.core.data.TaskHistory
import java.lang.Exception
import java.util.*

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

        fun insertRemotely(context: Context, task: Task) {
            executeGenericDataTransfer(context,task,if (context.isWatch()) DATA_TASK_MOBILE_INSERT_PATH else DATA_TASK_WEAR_INSERT_PATH)
        }

        fun deleteRemotely(context: Context,task: Task){
            executeGenericDataTransfer(context,task,if (context.isWatch()) DATA_TASK_MOBILE_DELETE_PATH else DATA_TASK_WEAR_DELETE_PATH)
        }

        fun updateRemotely(context: Context,task: Task){
            executeGenericDataTransfer(context,task,if (context.isWatch()) DATA_TASK_MOBILE_UPDATE_PATH else DATA_TASK_WEAR_UPDATE_PATH)
        }

        fun executeGenericDataTransfer(context: Context,task: Task,path:String){
            val putDataReq: PutDataRequest = PutDataMapRequest.create(path)
                    .run {
                        dataMap.putString(KEY_TASK_DATA, context.getGson().toJson(task)) //save the time
                        dataMap.putString(DataLayerConstant.KEY_TIME, getTimeInstant())
                        asPutDataRequest()
                    }
            putDataReq.setUrgent()
            Wearable.getDataClient(context).putDataItem(putDataReq)
        }

        fun getTimeInstant(): String {
            val cal = Calendar.getInstance()
            cal.timeInMillis = System.currentTimeMillis()
            return "${cal.get(Calendar.HOUR)}:${cal.get(Calendar.MINUTE)}:${cal.get(Calendar.SECOND)}"
        }

        fun sendTaskData(context:Context, taskData:String, historyData:String){

            val putDataReq: PutDataRequest = PutDataMapRequest.create(TASK_PATH).run {
                dataMap.putString(KEY_TASK_DATA, taskData )
                dataMap.putString(KEY_TASK_HISTORY_DATA, historyData)

                //save the time
                val cal = Calendar.getInstance()
                cal.timeInMillis = System.currentTimeMillis()
                dataMap.putString(DataLayerConstant.KEY_TIME, "${cal.get(Calendar.HOUR)}:${cal.get(Calendar.MINUTE)}:${cal.get(Calendar.SECOND)}")

                asPutDataRequest()
            }
            putDataReq.setUrgent()
            Wearable.getDataClient(context).putDataItem(putDataReq)
        }

        fun updateDayMirror(
                context: Context,
                isDayActive:Boolean,
                orderList:String,
                unCompletedList:String,
                completedList:String){
            val putDataReq: PutDataRequest = PutDataMapRequest.create(
                    PROGRESS_PATH
//                    if(isWatch)DataLayerConstant.2PROGRESS_MOBILE_PATH else DataLayerConstant.PROGRESS_WEAR_PATH
            )
                    .run {
                        dataMap.putBoolean(KEY_PROGRESS_ACTIVE, isDayActive) //save the time
                        dataMap.putString(KEY_PROGRESS_ORDER, orderList)
                        dataMap.putString(KEY_PROGRESS_COMPLETED, completedList)
                        dataMap.putString(KEY_PROGRESS_UNCOMPLETED, unCompletedList)
                        asPutDataRequest()
                    }
            putDataReq.setUrgent()
            Wearable.getDataClient(context).putDataItem(putDataReq)
        }

        fun endDayMirror(context:Context){

            val dataItemUri = Uri.Builder().scheme(PutDataRequest.WEAR_URI_SCHEME).path(PROGRESS_PATH).build()
            Wearable.getDataClient(context).deleteDataItems(dataItemUri)
        }

    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        Log.d("$tag onDataChanged ", "$dataEvents")
        Log.d("$tag onDataChanged ", "I AM ${if (isWatch()) "Watch" else "MOBILE"}")

        dataEvents.forEach {

            Log.d("$tag onDataChanged", "Host: ${it.dataItem.uri.host}")

            val dataMap = DataMapItem.fromDataItem(it.dataItem).dataMap
            when (it.type) {
                DataEvent.TYPE_CHANGED -> {

                    //In Any case we'll check the time it was issued
                    Log.d("$tag onDataChanged", " Changed Time Issued: ${dataMap.getString(DataLayerConstant.KEY_TIME)}")

                    /**
                     * Here we always received a list of IDS of scheduled tasks, we'll assume we already
                     * have the information for these tasks, so we'll just fetch
                     * them from our local database and display them
                     */
                    //TODO should we check for missing tasks in the database? We could use this as an opportunity to sync dbs
                    if (DataLayerConstant.WAKE_UP_PATH.equals(it.dataItem.uri.path)) {

                        val unCompletedList = stringToArray(dataMap.getString(KEY_TASK_DATA))
                        saveTaskList(unCompletedList)

                        Coroutines.ioThenMain({getDataRepository().getTaskByIds(unCompletedList)}){tasks->
                            tasks?.let {
                                //TODO SPLIT
//                                notificationShowWakeUpLocal(tasks)
                            }
                        }

                    } else if (DataLayerConstant.TASK_PATH.equals(it.dataItem.uri.path)) {

                        //Here task data is of type task
                        val taskData = dataMap.getString(KEY_TASK_DATA)
                        val historyData = dataMap.getString(KEY_TASK_HISTORY_DATA)

                        val gson = getGson()
                        val task = gson.fromJson(taskData, Task::class.java)
                        val history = gson.fromJson(historyData, TaskHistory::class.java)

                        //We'll show the task again, because it may be that
                        //the task was marked as complete from another device and we
                        //we may need to uodate it here.
                        //TODO SPLIT
//                        notificationShowTaskLocal(task,history)
/*
                        if (!isAlreadyShowing(task.id!!.toInt())) {
                            notificationShowTaskLocal(task)
                        }
*/

                    } else if (DataLayerConstant.SLEEP_PATH.equals(it.dataItem.uri.path)){

//                        notificationShowSleepLocal()
//TODO SPLIT
                    } else if (DataLayerConstant.PROGRESS_PATH.equals(it.dataItem.uri.path)) {

                        TaskScheduler.processDayInformation(
                                applicationContext,
                                dataMap.getBoolean(KEY_PROGRESS_ACTIVE),
                                dataMap.getString(KEY_PROGRESS_UNCOMPLETED),
                                dataMap.getString(KEY_PROGRESS_COMPLETED),
                                dataMap.getString(KEY_PROGRESS_ORDER)
                        )
                    }/* else if (DataLayerConstant.PROGRESS_WEAR_PATH.equals(it.dataItem.uri.path)){
                        TaskScheduler.processDayInformation(
                                applicationContext,
                                dataMap.getBoolean(KEY_PROGRESS_ACTIVE),
                                dataMap.getString(KEY_PROGRESS_UNCOMPLETED),
                                dataMap.getString(KEY_PROGRESS_COMPLETED),
                                dataMap.getString(KEY_PROGRESS_ORDER)
                        )
                    }*/

                    /**
                     * This is a special case which we'll use to synchronize data between
                     * mobile and Wear.
                     * Since Wear is a Standalone, Wear needs to have full capibilities of the
                     * phones, so the devices are treated as equal in terms of data, so they need
                     * to contain exactly the same data all the time.
                     * Because the databases in each device may have a different table KEY counts,
                     * if we insert individually, we may get two different tasks with the same
                     * details but different TaskIDs. So we fix this by inserting to one data,
                     * and then sending the details to the other.
                     */
                    else if (DataLayerConstant.DATA_TASK_WEAR_INSERT_PATH.equals(it.dataItem.uri.path)) {

                        try {
                            val taskData = dataMap.getString(KEY_TASK_DATA)
                            processInsertData(getGson().fromJson(taskData, Task::class.java))
                        } catch (e: Exception) {
                            Log.e(tag, "There was a problem inserting", e)
                        }
                    } else if (DataLayerConstant.DATA_TASK_MOBILE_INSERT_PATH.equals(it.dataItem.uri.path)) {
                        try {
                            val taskData = dataMap.getString(KEY_TASK_DATA)
                            processInsertData(getGson().fromJson(taskData, Task::class.java))

                        } catch (e: Exception) {
                            Log.e(tag, "There was a problem inserting", e)
                        }
                    } else if (DataLayerConstant.DATA_TASK_WEAR_DELETE_PATH.equals(it.dataItem.uri.path)) {
                        try {
                            val taskData = dataMap.getString(KEY_TASK_DATA)
                            processDeleteData(getGson().fromJson(taskData, Task::class.java))

                        } catch (e: Exception) {
                            Log.e(tag, "There was a problem deleting", e)
                        }
                    } else if (DataLayerConstant.DATA_TASK_MOBILE_DELETE_PATH.equals(it.dataItem.uri.path)) {
                        try {
                            val taskData = dataMap.getString(KEY_TASK_DATA)
                            processDeleteData(getGson().fromJson(taskData, Task::class.java))

                        } catch (e: Exception) {
                            Log.e(tag, "There was a problem deleting", e)
                        }
                    } else if (DataLayerConstant.DATA_TASK_WEAR_UPDATE_PATH.equals(it.dataItem.uri.path)) {
                        try {
                            val taskData = dataMap.getString(KEY_TASK_DATA)
                            processUpdateData(getGson().fromJson(taskData, Task::class.java))

                        } catch (e: Exception) {
                            Log.e(tag, "There was a problem updating", e)
                        }
                    } else if (DataLayerConstant.DATA_TASK_MOBILE_UPDATE_PATH.equals(it.dataItem.uri.path)) {
                        try {
                            val taskData = dataMap.getString(KEY_TASK_DATA)
                            processUpdateData(getGson().fromJson(taskData, Task::class.java))
                        } catch (e: Exception) {
                            Log.e(tag, "There was a problem updating", e)
                        }
                    }
                }
                DataEvent.TYPE_DELETED -> {

                    //TODO test situation where we may get a delete issue after a
                    //new notification issue b/c of network lag
                    Log.d("notificationRoutine", "onDataChanged Delete issued")

                    if (DataLayerConstant.WAKE_UP_PATH.equals(it.dataItem.uri.path)) {
                    //TODO SPLIT
//                        notificationDismissWakeUp()
                    } else if (DataLayerConstant.TASK_PATH.equals(it.dataItem.uri.path)) {

                        getAllTaskShowing().forEach { notification ->
                            //we only show 1 task, so we'll take this opportunity to dismiss ALL
                            //task notifications
                            //TODO SPLIT
//                            notificationDismissTask(notification.id)
                        }
                    } else if (DataLayerConstant.SLEEP_PATH.equals(it.dataItem.uri.path)) {

                        //TODO SPLIT
//                        notificationDismissSleepLocally()

                    } else if (DataLayerConstant.PROGRESS_PATH.equals(it.dataItem.uri.path)){
                        TaskScheduler.endDay(this@DataLayerListenerService)
                    }
                }
                else -> {

                }
            }
        }
    }

    fun processInsertData(task: Task){
        Log.d("$tag onDataChanged Data","got Insert Command")
        Coroutines.ioThenMain({ getDataRepository().insertAndReturnList(task)}){
            printList(it)
        }
    }

    fun processDeleteData(task: Task){
        Log.d("$tag onDataChanged Data","got Delete Command")
        Coroutines.ioThenMain({ getDataRepository().deleteAndReturnList(task)}){
            printList(it)
        }
    }

    fun processUpdateData(task: Task){
        Log.d("$tag onDataChanged Data","got Update Command")
        Coroutines.ioThenMain({ getDataRepository().updateAndReturnList(task)}){
           printList(it)
        }
    }

    fun printList(list:List<Task>?){
        Log.d("$tag onDataChanged Data","New List:")
        list?.forEach {task ->
            Log.d("$tag onDataChanged Data",task.toPrettyString())
        }
    }

    @TargetApi(23)
    private fun isAlreadyShowing(id:Int):Boolean {

        //TODO SPLIT
        return false
        /*return if (getNotificationManager().activeNotifications.find {
                    //Check if we aren't already displaying a notification
                    Log.d("notificationRoutine", "onDataChanged check: ${it.id}")
                    it.id == id
                } == null) {
            false
        } else {
            Log.d("notificationRoutine", "onDataChanged We're already showing $id")
            true
        }
*/    }

    override fun onCapabilityChanged(capibility: CapabilityInfo?) {
        super.onCapabilityChanged(capibility)

    }

    override fun onPeerConnected(node: Node?) {
        super.onPeerConnected(node)
    }


    override fun onConnectedNodes(nodes: MutableList<Node>?) {
        super.onConnectedNodes(nodes)


    }

}