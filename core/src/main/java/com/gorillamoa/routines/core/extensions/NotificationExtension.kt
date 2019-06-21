package com.gorillamoa.routines.core.extensions

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

import com.gorillamoa.routines.core.data.Task
import com.gorillamoa.routines.core.receiver.NotificationActionReceiver.Companion.ACTION_DONE
import com.gorillamoa.routines.core.receiver.NotificationActionReceiver.Companion.ACTION_SKIP_SHORT
import com.gorillamoa.routines.core.receiver.NotificationActionReceiver.Companion.ACTION_SKIP_TODAY
import com.gorillamoa.routines.core.receiver.NotificationActionReceiver.Companion.ACTION_WAKE_START_DAY
import com.gorillamoa.routines.core.views.RemoteInjectorHelper

import java.util.*

const val WAKE_UP_NOTIFICATION_ID =1

const val SLEEP_NOTIFICATION_ID =65535
const val REST_NOTIFICATION_ID =65534
const val ACTIVITY_NOTIFICATION_ID = 65533
const val TIMER_NOTIFICATION_ID = 65532

public const val NOTIFICATION_CHANNEL_ONE  = "channel"
public const val NOTIFICATION_CHANNEL_TWO  = "channel_MAX"
const val NOTIFICATION_TAG = "routines"


/** Prepare the intent for when user dismisses the notification **/
//TODO obfuscate strings later using obfuscation library


/********************************************************************************
 * WAKE UP NOTIFICATION FUNCTIONS
 *********************************************************************************/

/**
 * Show a local notification to the user which displays ""WAKEUP" information
 * @param tasks is the task list as a strin
 * @param mainPendingIntent is the Main notification intent
 * @param dismissPendingIntent is what happens when the user dismisses
 */
fun Context.notificationShowWakeUp(tasks:List<Task>,
                                   mainPendingIntent: PendingIntent?,
                                   dismissPendingIntent:PendingIntent? = null,
                                   dismissable:Boolean = true,
                                   smallRemoteView:RemoteViews? = null,
                                   bigRemoteView:RemoteViews?= null) {

    val manager = getNotificationManager()
    getBuilder().apply {

        if (isWatch()) {
            //TODO bring Stringbuilder from dagger()
            setStyle(prepareBigTextStyle(StringBuilder().stringifyTasks(tasks), "Today's tasks &#128170;"))

            //TODO UNCOMMENT FOR WATCH
            addWakeUpAction(this@notificationShowWakeUp,"Start Day", ACTION_WAKE_START_DAY)
            //addWakeUpAction(this@notificationShowWakeUp,"Edit", ACTION_START_MODIFY, WAKE_UP_NOTIFICATION_ID!!)
        }else{
            setCategory(Notification.CATEGORY_SERVICE)

            //lets add RemoteView
            smallRemoteView?.let {
                setCustomContentView(smallRemoteView)
            }

            bigRemoteView?.let {
                setCustomBigContentView(bigRemoteView)
            }

        }

        determineOnGoingAbility(this@apply,dismissable)

        mainPendingIntent?.let { setContentIntent(mainPendingIntent) }
        //TODO make the dismiss action optional, as in let user decide how a dismiss behaviour works!
        //Give option to do nothing or to go forward or cancel
        dismissPendingIntent?.let { setDeleteIntent(it) }

        manager.notify(
                NOTIFICATION_TAG,
                WAKE_UP_NOTIFICATION_ID,
                build())
    }
}


/**
 * Notify other devices that they should build a notification of type WAKE UP
 */
fun Context.notificationShowWakeUpRemote(tasks:List<Task>){
    notificationShowRemote(StringBuilder().stringifyTasks(tasks),WAKE_UP_PATH)
}


/**
 * Builds a mirrored notification both on the Local device and on
 * other connected nodes. When either is ACTIONED, the same action occurs on both devices.
 * @param tasks is the task list to show
 */
fun Context.notificationShowWakeUpMirror(tasks:List<Task>){
    Log.d("notificationRoutine","notificationShowWakeUpMirror")

    //First lets build a local notification
    notificationShowWakeUpLocal(tasks)

    //Next lets build a remote notification
    notificationShowWakeUpRemote(tasks)
}

/**
 * Builds a local notification
 * @param tasks is the string of tasks to display
 */
fun Context.notificationShowWakeUpLocal(tasks:List<Task>){
    Log.d("notificationRoutine","notificationShowWakeUpLocal")
    notificationShowWakeUp(
            tasks,
            mainPendingIntent = null,
            dismissPendingIntent = createNotificationDeleteIntentForWakeUp(),
            //TODO CHECK IF WE SHOULD ALLOW DISMISSAL with stubborn settings
            dismissable = true,
            //TODO get the actual task length
            smallRemoteView = if(!isWatch())remoteGetSmallWakeUpView(tasks.size)else null,
            //TODO Get stringbuilder from dagger singleton
            bigRemoteView = if(!isWatch())remoteGetLargeWakeUpView(StringBuilder().stringifyTasks(tasks)) else null
    )
}

/**
 * Notify remote nodes that they should remove their Wake Up Notifications
 * if they are displaying one
 */
fun Context.notificationDismissWakeUpRemote(){

    val dataItemUri = Uri.Builder().scheme(WEAR_URI_SCHEME).path(WAKE_UP_PATH).build()
    Wearable.getDataClient(this).deleteDataItems(dataItemUri)
}

/**
 * Convenience
 * Cancels (removes) the Wake up Notification if there is one
 */
fun Context.notificationDismissWakeUp(){

    getNotificationManager().cancel(NOTIFICATION_TAG, WAKE_UP_NOTIFICATION_ID)
}

/**
 * Convenience method to dismiss all wake up notifications across devices
 */
fun Context.notificationDismissWakeUpMirror(){

    notificationDismissWakeUpRemote()
    notificationDismissWakeUp()
}

/**
 * Create an Action button for a task notification
 * Note: Dismiss Intent is fired if one of these actions is clicked. We should take care
 * to create dismiss funtions for custom actions on RemoteView
 * @param context is the application context
 * @param actionText is the text to display on the button
 * @param action is the Intent action that will redirect to the proper functoin
 */
fun NotificationCompat.Builder.addWakeUpAction(context: Context,actionText:String, action:String){

    addAction(NotificationCompat.Action.Builder(
            R.mipmap.ic_launcher,
            actionText,
            context.createNotificationActionPendingIntentForWakeUp(action)
    ).build())
}


/********************************************************************************
 * TASK NOTIFICATION FUNCTIONS
 *********************************************************************************/

fun Context.notificationShowTask(task: Task,
                                 mainPendingIntent: PendingIntent? = null,
                                 dismissPendingIntent: PendingIntent? = null,
                                 dismissable: Boolean = true,
                                 smallRemoteView: RemoteViews? = null,
                                 bigRemoteView: RemoteViews? = null) {

    val manager = getNotificationManager()

    getBuilder().apply {

        if (isWatch()) {
            setAutoCancel(true)
            setCategory(Notification.CATEGORY_REMINDER)

            addTaskAction(this@notificationShowTask,"Done      ", ACTION_DONE,task)
            addTaskAction(this@notificationShowTask,"Delay     ", ACTION_SKIP_SHORT,task)
            addTaskAction(this@notificationShowTask,"Skip Today", ACTION_SKIP_TODAY,task)
        }else{

            setCategory(Notification.CATEGORY_SERVICE)
            smallRemoteView?.let { setCustomContentView(smallRemoteView) }
            bigRemoteView?.let { setCustomBigContentView(bigRemoteView) }

        }

        setContentTitle(task.name)
        setContentText(task.description)
        setDeleteIntent(dismissPendingIntent)

        Log.d("schedule","Showing Notification id: ${task.id}")

        determineOnGoingAbility(this,dismissable)

        manager.notify(
                NOTIFICATION_TAG,
                task.id!!,
                build()
        )
    }
}

/**
 * Builds a mirrored notification both on the Local device and on
 * other connected nodes. When either is ACTIONED, the same action occurs on both devices.
 * @param task is the task to show
 */
fun Context.notificationShowTaskMirror(task:Task){
    Log.d("notificationRoutine","notificationShowTaskMirror")

    //lets show a local notification first
    notificationShowTaskLocal(task)

    //lets show a remote notification now
    notificationShowTaskRemote(task)
}

fun Context.notificationShowTaskLocal(task:Task){
    Log.d("notificationRoutine"," notificationShowTaskLocal Task:${task.id}")



    notificationShowTask(
            task,
            mainPendingIntent = null,
            dismissPendingIntent = createNotificationDeleteIntentForTask(task),
            //TODO check stubborn to see if we should dismiss
            dismissable = true,
            smallRemoteView = if(!isWatch())remoteGetSmallTaskView(task)else null,
            //TODO make a big remote view for tasks
            bigRemoteView= null
            )
}

/**
 * Show a task remotely
 */
fun Context.notificationShowTaskRemote(task:Task){
    Log.d("notificationRoutine","notificationShowTaskRemote")

    notificationShowRemote(getGson().toJson(task), TASK_PATH)
}

fun Context.notificationDismissTaskMirror(tid:Int){

    notificationDismissTask(tid)
    notificationDismissTaskRemote()
}

/**
 * Convenience
 * Cancels (removes) the Task  Notification if there is one
 * @param task is the task to remove
 */
fun Context.notificationDismissTask(tid:Int) {
    Log.d("notificationRoutine", "notificationDismissTask $tid")

    getNotificationManager().cancel(NOTIFICATION_TAG, tid)
}

/**
 * Notify remote nodes that they should remove their task Notifications
 * if they are displaying one
 */
fun Context.notificationDismissTaskRemote(){
    Log.d("notificationRoutine","notificationDismissTaskRemote")

    val dataItemUri = Uri.Builder().scheme(WEAR_URI_SCHEME).path(TASK_PATH).build()
    Wearable.getDataClient(this).deleteDataItems(dataItemUri)
}

/**
 * Create an Action button for a task notification
 * Note: Dismiss Intent is fired if one of these actions is clicked. We should take care
 * to create dismiss funtions for custom actions on RemoteView
 * @param context is the application context
 * @param actionText is the text to display on the button
 * @param action is the Intent action that will redirect to the proper functoin
 */
fun NotificationCompat.Builder.addTaskAction(context: Context,actionText:String, action:String, task:Task){
    Log.d("notificationRoutine","addTaskAction")

    addAction(NotificationCompat.Action.Builder(
            R.mipmap.ic_launcher,
            actionText,
            context.createNotificationActionPendingIntentForTask(task, action)
    ).build())
}

/********************************************************************************
 * GENERIC NOTIFICATION FUNCTIONS
 *********************************************************************************/


/**
 * A generic function for showing a specific notification remotely
 * @param taskData is the task data to send over
 * @param path is the item for which the Data Layer will action upon
 */
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

fun determineOnGoingAbility(builder:NotificationCompat.Builder, dismissable:Boolean){

    if (!dismissable) {

        builder.apply {

            setCategory(Notification.CATEGORY_SERVICE)
            setAutoCancel(false)
            setOngoing(true)

            //set priority Level to stay on TOP of other notifications
            setChannelId(NOTIFICATION_CHANNEL_TWO)

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                priority = Notification.PRIORITY_MAX
            }
        }
    }
}


//TODO ADD common functionality to remove notifications!
//TODO when switching between tasks, make notification priority low so it doesn't show up all the time

//clean
fun Context.notificationShowTimer(){
    val manager = getNotificationManager()

    getBuilder().apply {

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

    getBuilder().apply {

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

    getBuilder().apply {

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




fun Context.notificationShowSleep(dismissable: Boolean = true){

        val manager = getNotificationManager()
        getBuilder().apply {

                //TODO launch with alarm OR with task completion

                //TODO change text depending on above condition
                setContentTitle(getHtml("All done! &#127881"))
//                setContentTitle(Html.fromHtml("All done! &#127769", Html.FROM_HTML_MODE_COMPACT))
                //TODO change text depending on above conditions
                setContentText("See Today's Accomplishments ")

                //TODO MOVE THIS ELSEWHERE
                val completed = getCompletedTaskList()
                val uncompleted = getDayTaskList()
                val total = completed.size + uncompleted.size

                setStyle(prepareBigTextStyle("$completed/$total Tasks Completed!","Results:"))
                setAutoCancel(true)
                setCategory(Notification.CATEGORY_SOCIAL)
//                setDeleteIntent(dimissPendingIntent)

                determineOnGoingAbility(this@apply,dismissable)

                manager.notify(
                        NOTIFICATION_TAG,
                        SLEEP_NOTIFICATION_ID,
                        build()
                )
        }


}


fun prepareBigTextStyle(tasks:String,title:String):NotificationCompat.BigTextStyle{
        return NotificationCompat.BigTextStyle()
         .setBigContentTitle(getHtml(title))
         .bigText(getHtml(tasks))
}

fun Context.getBuilder():NotificationCompat.Builder{


    //TODO UNCOMMENTAND DELETE ABOVE
        return NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ONE)

                //TODO show weather in one icon in the notification title
                .setContentTitle(getHtml("Good morning! &#127780"))
                .setSmallIcon(com.gorillamoa.routines.core.R.mipmap.ic_launcher)
                .setContentText("See today's schedule")
                .setAutoCancel(true)
                .setCategory(Notification.CATEGORY_REMINDER)
//                                .setDeleteIntent(dismissPendingIntent)
}

fun getHtml(htmlString:String): Spanned {
    //24 and above
    if(Build.VERSION.SDK_INT > Build.VERSION_CODES.N){
        return Html.fromHtml(htmlString,Html.FROM_HTML_MODE_COMPACT)
    }
    //below
    else{
        return Html.fromHtml(htmlString)
    }
}

/**
 * retrieve the notifcation manager.
 */
fun Context.getNotificationManager():NotificationManager{
    return getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
}

