package com.gorillamoa.routines.notifications

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.notificationsimpl.R
import java.util.*

/*
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.text.Spanned
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
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
 * Show a local notification to the user which displays ""WAKEUP" information
 * @param tasks is the task list as a strin
 * @param mainPendingIntent is the Main notification intent
 * @param dismissPendingIntent is what happens when the user dismisses
 */

fun Context.notificationShowWakeUp(tasks:String? = null,
                                   mainPendingIntent: PendingIntent?,
                                   dismissPendingIntent: PendingIntent? = null,
                                   dismissable:Boolean = true,
                                   smallRemoteView: RemoteViews? = null,
                                   bigRemoteView:RemoteViews?= null,
                                   isWatch: Boolean = false) {


 /*   _notificationShowWakeUp(
            tasks,
            mainPendingIntent,
            dismissPendingIntent,
            dismissable,
            smallRemoteView,
            bigRemoteView,
            NOTIFICATION_CHANNEL_ONE,
            NOTIFICATION_TAG,
            WAKE_UP_NOTIFICATION_ID)*/

    val manager = getNotificationManager()

    getBuilder(NOTIFICATION_CHANNEL_ONE,false).apply {

        if (isWatch) {

            setStyle(prepareBigTextStyle(
                    tasks = tasks ?: getString(R.string.notification_missing_data),
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
                NOTIFICATION_TAG,
                WAKE_UP_NOTIFICATION_ID,
                build())
    }
}



/**
 * Notify other devices that they should build a notification of type WAKE UP
 */

fun Context.notificationShowWakeUpRemote(tasks: ArrayDeque<Long>,path:String){
    notificationShowRemote(tasks.joinToString(","),path)
}

/**
 * Builds a mirrored notification both on the Local device and on
 * other connected nodes. When either is ACTIONED, the same action occurs on both devices.
 * @param tasks is the task list to show
 */
fun Context.notificationShowWakeUpMirror(tasks:ArrayDeque<Long>,path:String){
    notificationShowWakeUpRemote(tasks,path)
}

/**
 * Builds a local notification
 * @param tasks is the string of tasks to display
 */
fun Context.notificationShowWakeUpLocal(tasks:String,size:Int,isWatch:Boolean){
   Log.d("notificationRoutine","notificationShowWakeUpLocal")

    removeAllNotificationsExceptSpecified(WAKE_UP_NOTIFICATION_ID)
    notificationShowWakeUp(
            tasks,
            mainPendingIntent = null,
            dismissPendingIntent = null,
//            dismissPendingIntent = createNotificationDeleteIntentForWakeUp(),
            //TODO CHECK IF WE SHOULD ALLOW DISMISSAL with stubborn settings
            dismissable = false,
            //TODO get the actual task length
            smallRemoteView = if(!isWatch)remoteGetSmallWakeUpView(size)else null,
            //TODO Get stringbuilder from dagger singleton
            bigRemoteView = if(!isWatch)remoteGetLargeWakeUpView(tasks) else null
    )
}

/**
 * Notify remote nodes that they should remove their Wake Up Notifications
 * if they are displaying one
 */

fun Context.notificationDismissWakeUpRemote(){
    RoutinesNotificationBuilder.remoteNotificationApi?.notificationDismissWakeUpRemote(this)
}


/**
 * Convenience
 * Cancels (removes) the Wake up Notification if there is one
 */
fun Context.notificationDismissWakeUp() {
    getNotificationManager().cancel(NOTIFICATION_TAG, WAKE_UP_NOTIFICATION_ID)
}

/**
 * Convenience method to dismiss all wake up notifications across devices
 */

fun Context.notificationDismissWakeUpMirror() {

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
 *//*

fun NotificationCompat.Builder.addWakeUpAction(context: Context,actionText:String, action:String){
//TODO complete function
}


*/
/********************************************************************************
 * TASK NOTIFICATION FUNCTIONS
 *********************************************************************************/

fun Context.notificationShowTask(task: String?= null,
                                 history:String? = null,
                                 mainPendingIntent: PendingIntent? = null,
                                 dismissPendingIntent: PendingIntent? = null,
                                 dismissable: Boolean = true,
                                 smallRemoteView: RemoteViews? = null,
                                 bigRemoteView: RemoteViews? = null) {
//TODO complete function
}


/**
 * Builds a mirrored notification both on the Local device and on
 * other connected nodes. When either is ACTIONED, the same action occurs on both devices.
 * @param task is the task to show
 */
/*
fun Context.notificationShowTaskMirror(task:Task,history:TaskHistory?= null){
//TODO complete function
}

fun Context.notificationShowTaskLocal(task:Task, history:TaskHistory? = null){
//TODO complete function
}
*/

fun Context.removeAllNotificationsExceptSpecified(tid:Int){

//we need to make sure to only show this one TASK
    val manager = getNotificationManager()
    (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).activeNotifications.forEach { notification ->
        if (notification.id != tid) {
            manager.cancel(NOTIFICATION_TAG,notification.id)
        }
    }
}

/**
 * Show a task remotely
 *//*

fun Context.notificationShowTaskRemote(task:Task, history:TaskHistory?=null){
//TODO complete function
}

fun Context.notificationDismissTaskMirror(tid:Long){
//TODO complete function
}

*/
/**
 * Convenience
 * Cancels (removes) the Task  Notification if there is one
 * @param task is the task to remove
 *//*

fun Context.notificationDismissTask(tid:Int) {
//TODO complete function
}

*/
/**
 * Notify remote nodes that they should remove their task Notifications
 * if they are displaying one
 *//*

fun Context.notificationDismissTaskRemote(){
//TODO complete function
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
//TODO complete function
}

*/
/********************************************************************************
 * SLEEP NOTIFICATION FUNCTIONS
 *********************************************************************************/


fun Context.notificationShowSleepMirror(){
//TODO complete function
}

fun Context.notificationShowSleepLocal() {
//TODO complete function

}
fun Context.notificationShowSleep(
        mainPendingIntent: PendingIntent? = null,
        dismissPendingIntent: PendingIntent? = null,
        dismissable: Boolean = true,
        smallRemoteView: RemoteViews? = null,
        bigRemoteView: RemoteViews? = null
){

    //TODO complete function

}

fun Context.notificationDismissSleepRemote(){

//TODO complete function
}

fun Context.notificationDismissSleepLocally(){
//TODO complete function
}


/********************************************************************************
 * GENERIC NOTIFICATION FUNCTIONS
 *********************************************************************************//*

*/
/**
 * A generic function for showing a specific notification remotely
 * @param taskData is the task data to send over
 * @param path is the item for which the Data Layer will action upon
 */
fun Context.notificationShowRemote(taskData:String, path:String){
    RoutinesNotificationBuilder.remoteNotificationApi?.notificationShowRemote(this,taskData,path)
}

fun determineOnGoingAbility(builder:NotificationCompat.Builder, dismissable:Boolean){

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


//TODO ADD common functionality to remove notifications!
//TODO when switching between tasks, make notification priority low so it doesn't show up all the time

//clean
fun Context.notificationShowTimer(isWatch: Boolean){

    val manager = getNotificationManager()

    getBuilder(NOTIFICATION_CHANNEL_ONE,isWatch).apply {

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
fun Context.notificationShowRest(isWatch: Boolean){

    val manager = getNotificationManager()

    getBuilder(NOTIFICATION_CHANNEL_ONE, isWatch).apply {

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

fun Context.notificationShowActivity(activity:String, int:Int,isWatch: Boolean){

    val manager = getNotificationManager()
    getBuilder(NOTIFICATION_CHANNEL_ONE,isWatch).apply {

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



