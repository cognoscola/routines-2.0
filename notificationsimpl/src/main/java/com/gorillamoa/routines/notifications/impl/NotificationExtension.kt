package com.gorillamoa.routines.notifications.impl

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.notificationsimpl.R

/*
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.net.Uri
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.PutDataRequest
import com.google.android.gms.wearable.PutDataRequest.WEAR_URI_SCHEME
import com.google.android.gms.wearable.Wearable
import com.gorillamoa.routines.core.R
import com.gorillamoa.routines.core.constants.DataLayerConstant
import com.gorillamoa.routines.core.constants.DataLayerConstant.Companion.WAKE_UP_PATH
import com.gorillamoa.routines.core.constants.DataLayerConstant.Companion.TASK_PATH
import com.gorillamoa.routines.core.constants.DataLayerConstant.Companion.KEY_TASK_DATA
import com.gorillamoa.routines.core.constants.DataLayerConstant.Companion.SLEEP_PATH

import com.gorillamoa.routines.core.data.Task
import com.gorillamoa.routines.core.data.TaskHistory
import com.gorillamoa.routines.core.receiver.NotificationActionReceiver.Companion.ACTION_DONE
import com.gorillamoa.routines.core.receiver.NotificationActionReceiver.Companion.ACTION_SKIP_TODAY
import com.gorillamoa.routines.core.receiver.NotificationActionReceiver.Companion.ACTION_TASK_NEXT
import com.gorillamoa.routines.core.receiver.NotificationActionReceiver.Companion.ACTION_TASK_UNCOMPLETE
import com.gorillamoa.routines.core.receiver.NotificationActionReceiver.Companion.ACTION_WAKE_START_DAY
import com.gorillamoa.routines.core.scheduler.TaskScheduler
import com.gorillamoa.routines.core.services.DataLayerListenerService

import java.util.*


*/
/** Prepare the intent for when user dismisses the notification **//*

//TODO obfuscate strings later using obfuscation library

*/
/********************************************************************************
 * WAKE UP NOTIFICATION FUNCTIONS
 *********************************************************************************//*

*/

/**
 * Show the wake up notification
 * @receiver Context
 * @param taskData String? contains the string information to show to show as part of the
 * notification's content
 * @param mainPendingIntent PendingIntent?
 * @param dismissPendingIntent PendingIntent?
 * @param dismissable Boolean
 * @param smallRemoteView RemoteViews?
 * @param bigRemoteView RemoteViews?
 * @param channel String
 */

fun Context._notificationShowWakeUp(taskData:String? = null,
                                    mainPendingIntent: PendingIntent?,
                                    dismissPendingIntent:PendingIntent? = null,
                                    dismissable:Boolean = true,
                                    smallRemoteView: RemoteViews? = null,
                                    bigRemoteView:RemoteViews?= null,
                                    channel:String,
                                    tag:String,
                                    notificatonId:Int) {

    val manager = getNotificationManager()

    _getBuilder(channel,false).apply {

        if (isWatch()) {

            setStyle(prepareBigTextStyle(
                    tasks = taskData?:getString(R.string.notification_missing_data),
                    title = getHtml("Today's tasks &#128170;")))

            //TODO UNCOMMENT FOR WATCH
//            addWakeUpAction(this@notificationShowWakeUp,"Start Day", ACTION_WAKE_START_DAY)
            //addWakeUpAction(this@notificationShowWakeUp,"Edit", ACTION_START_MODIFY, WAKE_UP_NOTIFICATION_ID!!)
        }else{

            //lets add RemoteView
            smallRemoteView?.let {
                setCustomContentView(smallRemoteView)
            }

            bigRemoteView?.let {
                setCustomBigContentView(bigRemoteView)
            }
        }

        //TODO MODULE SPLIT
        determineOnGoingAbility(this@apply,dismissable)

        mainPendingIntent?.let { setContentIntent(mainPendingIntent) }
        //TODO make the dismiss action optional, as in let user decide how a dismiss behaviour works!
        //Give option to do nothing or to go forward or cancel
        dismissPendingIntent?.let { setDeleteIntent(it) }

        manager.notify(
                tag,
                notificatonId,
                build())
    }
}

fun NotificationCompat.Builder.setWatchContent(content:String,title:String){

}

fun NotificationCompat.Builder.setMobileContent(smallRemoteView: RemoteViews,bigRemoteView: RemoteViews){

}



/**
 * Notify other devices that they should build a notification of type WAKE UP
 *//*

fun Context.notificationShowWakeUpRemote(tasks:ArrayDeque<Long>){

    notificationShowRemote(tasks.joinToString(","),WAKE_UP_PATH)
}

*/
/**
 * Builds a mirrored notification both on the Local device and on
 * other connected nodes. When either is ACTIONED, the same action occurs on both devices.
 * @param tasks is the task list to show
 *//*

fun Context.notificationShowWakeUpMirror(tasks:ArrayDeque<Long>){
    Log.d("notificationRoutine","notificationShowWakeUpMirror")

    //Next lets build a remote notification
    notificationShowWakeUpRemote(tasks)
}

*/
/**
 * Builds a local notification
 * @param tasks is the string of tasks to display
 *//*

fun Context.notificationShowWakeUpLocal(tasks:List<Task>){
    Log.d("notificationRoutine","notificationShowWakeUpLocal")

    removeAllNotificationsExceptSpecified(WAKE_UP_NOTIFICATION_ID)

    notificationShowWakeUp(
            tasks,
            mainPendingIntent = null,
            dismissPendingIntent = createNotificationDeleteIntentForWakeUp(),
            //TODO CHECK IF WE SHOULD ALLOW DISMISSAL with stubborn settings
            dismissable = false,
            //TODO get the actual task length
            smallRemoteView = if(!isWatch())remoteGetSmallWakeUpView(tasks.size)else null,
            //TODO Get stringbuilder from dagger singleton
            bigRemoteView = if(!isWatch())remoteGetLargeWakeUpView(StringBuilder().stringifyTasks(tasks)) else null
    )
}

*/
/**
 * Notify remote nodes that they should remove their Wake Up Notifications
 * if they are displaying one
 *//*

fun Context.notificationDismissWakeUpRemote(){

    val dataItemUri = Uri.Builder().scheme(WEAR_URI_SCHEME).path(WAKE_UP_PATH).build()
    Wearable.getDataClient(this).deleteDataItems(dataItemUri)
}

*/
/**
 * Convenience
 * Cancels (removes) the Wake up Notification if there is one
 *//*

fun Context.notificationDismissWakeUp(){

    getNotificationManager().cancel(NOTIFICATION_TAG, WAKE_UP_NOTIFICATION_ID)
}

*/
/**
 * Convenience method to dismiss all wake up notifications across devices
 *//*

fun Context.notificationDismissWakeUpMirror(){

    notificationDismissWakeUpRemote()
    notificationDismissWakeUp()
}

*/
/**
 * Create an Action button for a task notification
 * Note: Dismiss Intent is fired if one of these actions is clicked. We should take care
 * to create dismiss funtions for custom actions on RemoteView
 * @param context is the application context
 * @param actionText is the text to display on the button
 * @param action is the Intent action that will redirect to the proper functoin
 *//*

fun NotificationCompat.Builder.addWakeUpAction(context: Context,actionText:String, action:String){

    addAction(NotificationCompat.Action.Builder(
            R.mipmap.ic_launcher,
            actionText,
            context.createNotificationActionPendingIntentForWakeUp(action)
    ).build())
}


*/
/********************************************************************************
 * TASK NOTIFICATION FUNCTIONS
 *********************************************************************************//*


fun Context.notificationShowTask(task: Task,
                                 history:TaskHistory? = null,
                                 mainPendingIntent: PendingIntent? = null,
                                 dismissPendingIntent: PendingIntent? = null,
                                 dismissable: Boolean = true,
                                 smallRemoteView: RemoteViews? = null,
                                 bigRemoteView: RemoteViews? = null) {

    val manager = getNotificationManager()

    _getBuilder().apply {

        if (isWatch()) {

            val title:Spanned

            if(TaskScheduler.isComplete(this@notificationShowTask, task.id!!)){
                Log.d("notificationRoutine","notificationShowTask Task is Completed!")
                addTaskAction(this@notificationShowTask,"Uncheck", ACTION_TASK_UNCOMPLETE,task,history)

                title= getHtml("<strike>${task.name}</strike>")
            }else{
                Log.d("notificationRoutine","notificationShowTask Task is InComplete")

                title = getHtml(task.name)

                //Mark as done
                addTaskAction(this@notificationShowTask,"Done      ", ACTION_DONE,task,history)
                //Equivalent to NEXT
                addTaskAction(this@notificationShowTask,"Delay     ", ACTION_TASK_NEXT,task,history)
                //Removes from the Queue
                addTaskAction(this@notificationShowTask,"Skip Today", ACTION_SKIP_TODAY,task,history)
            }

            setContentTitle(title)
            setStyle(prepareBigTextStyle(StringBuilder().stringifyHistory(history), title))

        }else{

            smallRemoteView?.let { setCustomContentView(smallRemoteView) }
            bigRemoteView?.let { setCustomBigContentView(bigRemoteView) }

        }

        setContentText(task.description)
        setDeleteIntent(dismissPendingIntent)

        Log.d("schedule","Showing Notification id: ${task.id}")

        determineOnGoingAbility(this,dismissable)

        manager.notify(
                NOTIFICATION_TAG,
                task.id!!.toInt(),
                build()
        )
    }
}

*/
/**
 * Builds a mirrored notification both on the Local device and on
 * other connected nodes. When either is ACTIONED, the same action occurs on both devices.
 * @param task is the task to show
 *//*

fun Context.notificationShowTaskMirror(task:Task,history:TaskHistory?= null){
    Log.d("notificationRoutine","notificationShowTaskMirror")

    //lets show a remote notification now
    notificationShowTaskRemote(task,history)
}

fun Context.notificationShowTaskLocal(task:Task, history:TaskHistory? = null){
    Log.d("notificationRoutine"," notificationShowTaskLocal Task:${task.id}")

    removeAllNotificationsExceptSpecified(task.id!!.toInt())

    notificationShowTask(
            task,
            history,
            mainPendingIntent = null,
            dismissPendingIntent = createNotificationDeleteIntentForTask(task),
            //TODO check stubborn to see if we should dismiss
            dismissable = false,
            smallRemoteView = if(!isWatch())remoteGetSmallTaskView(task)else null,
            //TODO make a big remote view for tasks

            //TODO fetch builder from Dagger
            bigRemoteView= if(!isWatch())remoteGetLargeTaskView(StringBuilder().stringifyHistory(history)) else null
            )
}

fun Context.removeAllNotificationsExceptSpecified(tid:Int){

    //we need to make sure to only show this one TASK
    val manager = getNotificationManager()
    getAllTaskShowing().forEach {notification ->
        if (notification.id != tid) {
            manager.cancel(NOTIFICATION_TAG,notification.id)
        }
    }
}

*/
/**
 * Show a task remotely
 *//*

fun Context.notificationShowTaskRemote(task:Task, history:TaskHistory?=null){
    Log.d("notificationRoutine","notificationShowTaskRemote")

    DataLayerListenerService.sendTaskData(this,getGson().toJson(task),getGson().toJson(history))
//    notificationShowRemote(getGson().toJson(task), TASK_PATH)
}

fun Context.notificationDismissTaskMirror(tid:Long){

    notificationDismissTask(tid.toInt())
    notificationDismissTaskRemote()
}

*/
/**
 * Convenience
 * Cancels (removes) the Task  Notification if there is one
 * @param task is the task to remove
 *//*

fun Context.notificationDismissTask(tid:Int) {
    Log.d("notificationRoutine", "notificationDismissTask $tid")

    getNotificationManager().cancel(NOTIFICATION_TAG, tid)
}

*/
/**
 * Notify remote nodes that they should remove their task Notifications
 * if they are displaying one
 *//*

fun Context.notificationDismissTaskRemote(){
    Log.d("notificationRoutine","notificationDismissTaskRemote")

    val dataItemUri = Uri.Builder().scheme(WEAR_URI_SCHEME).path(TASK_PATH).build()
    Wearable.getDataClient(this).deleteDataItems(dataItemUri)
}

*/
/**
 * Create an Action button for a task notification
 * Note: Dismiss Intent is fired if one of these actions is clicked. We should take care
 * to create dismiss funtions for custom actions on RemoteView
 * @param context is the application context
 * @param actionText is the text to display on the button
 * @param action is the Intent action that will redirect to the proper functoin
 *//*

fun NotificationCompat.Builder.addTaskAction(context: Context,actionText:String, action:String, task:Task,history:TaskHistory?){
    Log.d("notificationRoutine","addTaskAction")

    addAction(NotificationCompat.Action.Builder(
            R.mipmap.ic_launcher,
            actionText,
            context.createNotificationActionPendingIntentForTask(task,history, action)
    ).build())
}

*/
/********************************************************************************
 * SLEEP NOTIFICATION FUNCTIONS
 *********************************************************************************//*


fun Context.notificationShowSleepMirror(){
    notificationShowRemote("",SLEEP_PATH)
}

fun Context.notificationShowSleepLocal(){

    removeAllNotificationsExceptSpecified(SLEEP_NOTIFICATION_ID)

    notificationShowSleep(
            mainPendingIntent = null,
            dismissPendingIntent = createNotificationDeleteIntentForSleep(),
            dismissable = true,
            smallRemoteView = if(!isWatch())remoteGetSmallSleepView() else null,
            bigRemoteView = null
//            bigRemoteView = remoteGetLargeSleepView()
    )
}

fun Context.notificationShowSleep(
        mainPendingIntent: PendingIntent? = null,
        dismissPendingIntent: PendingIntent? = null,
        dismissable: Boolean = true,
        smallRemoteView: RemoteViews? = null,
        bigRemoteView: RemoteViews? = null
){

    val manager = getNotificationManager()
    _getBuilder().apply {

        //TODO launch with alarm OR with task completion
        val completed = getCompletedTaskList()
        val uncompleted = getDayTaskList()
        val total = completed.size + uncompleted.size

        (true)
        if (isWatch()) {

            //TODO change text depending on wether we came from an alarm or from a task completision
            setContentTitle(getHtml("All done! &#127881"))
            setContentText("See Today's Accomplishments ")

//            addTaskAction(this@notificationShowTask,"Uncheck", ACTION_DONE,task)

            setStyle(prepareBigTextStyle("$completed/$total Tasks Completed!",getHtml("Results:")))
        }else{

            smallRemoteView?.let { setCustomContentView(smallRemoteView) }
            bigRemoteView?.let { setCustomBigContentView(bigRemoteView) }
        }

        setDeleteIntent(dismissPendingIntent)

        determineOnGoingAbility(this@apply,dismissable)

        manager.notify(
                NOTIFICATION_TAG,
                SLEEP_NOTIFICATION_ID,
                build()
        )
    }


}

fun Context.notificationDismissSleepRemote(){

    val dataItemUri = Uri.Builder().scheme(WEAR_URI_SCHEME).path(SLEEP_PATH).build()
    Wearable.getDataClient(this).deleteDataItems(dataItemUri)

}

fun Context.notificationDismissSleepLocally(){

    getNotificationManager().cancel(NOTIFICATION_TAG, SLEEP_NOTIFICATION_ID)
}



*/
/********************************************************************************
 * GENERIC NOTIFICATION FUNCTIONS
 *********************************************************************************//*



*/
/**
 * A generic function for showing a specific notification remotely
 * @param taskData is the task data to send over
 * @param path is the item for which the Data Layer will action upon
 *//*

fun Context.notificationShowRemote(taskData:String, path:String){

    val putDataReq: PutDataRequest = PutDataMapRequest.create(path).run {
        dataMap.putString(KEY_TASK_DATA, taskData )

        //save the time
        val cal = Calendar.getInstance()
        cal.timeInMillis = System.currentTimeMillis()
        dataMap.putString(DataLayerConstant.KEY_TIME, "${cal.get(Calendar.HOUR)}:${cal.get(Calendar.MINUTE)}:${cal.get(Calendar.SECOND)}")

        asPutDataRequest()
    }
    putDataReq.setUrgent()
    val putDataTask = Wearable.getDataClient(this).putDataItem(putDataReq)
}
*/
fun determineOnGoingAbility(builder: NotificationCompat.Builder, dismissable:Boolean){

    if (!dismissable) {

        builder.apply {

            setCategory(Notification.CATEGORY_SERVICE)
            setAutoCancel(false)
            setOngoing(true)

            //set priority Level to stay on TOP of other notifications
            setChannelId("")
//            setChannelId(NOTIFICATION_CHANNEL_TWO)

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                priority = Notification.PRIORITY_MAX
            }
        }
    }else{
        builder.setCategory(Notification.CATEGORY_REMINDER)
    }
}
/*

//TODO ADD common functionality to remove notifications!
//TODO when switching between tasks, make notification priority low so it doesn't show up all the time

//clean
fun Context.notificationShowTimer(){
    val manager = getNotificationManager()

    _getBuilder().apply {

        setContentTitle(getHtml("Times up!"))
        setAutoCancel(true)
        setCategory(Notification.CATEGORY_REMINDER)

        manager.notify(
                NOTIFICATION_TAG,
                TIMER_NOTIFICATION_ID,
                build()
        )
    }

}


//clean
fun Context.notificationShowRest(){
    val manager = getNotificationManager()

    _getBuilder().apply {

        setContentTitle(getHtml("Rest!"))
//                setContentTitle(Html.fromHtml("All done! &#127769", Html.FROM_HTML_MODE_COMPACT))

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()

        setContentText("Time: ${calendar.get(Calendar.HOUR)}:${calendar.get(Calendar.MINUTE)}")

        setAutoCancel(true)
        setCategory(Notification.CATEGORY_REMINDER)

        manager.notify(
                NOTIFICATION_TAG,
                REST_NOTIFICATION_ID,
                build()
        )
    }

}

fun Context.notificationShowActivity(activity:String, int:Int){
    val manager = getNotificationManager()

    _getBuilder().apply {

        setContentTitle(getHtml("$activity! $int"))
//                setContentTitle(Html.fromHtml("All done! &#127769", Html.FROM_HTML_MODE_COMPACT))

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()

        setContentText("Time: ${calendar.get(Calendar.HOUR)}:${calendar.get(Calendar.MINUTE)}")

        setAutoCancel(true)
        setCategory(Notification.CATEGORY_REMINDER)

        manager.notify(
                NOTIFICATION_TAG,
                ACTIVITY_NOTIFICATION_ID,
                build()
        )
    }

}

*/
