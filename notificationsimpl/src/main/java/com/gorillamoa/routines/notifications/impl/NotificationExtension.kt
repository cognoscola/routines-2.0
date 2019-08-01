package com.gorillamoa.routines.notifications.impl

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.notificationsimpl.R
import java.util.*

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
}*/

