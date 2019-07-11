package com.gorillamoa.routines.notifications

import android.app.PendingIntent
import android.content.Context
import android.widget.RemoteViews
import android.widget.Toast
import com.gorillamoa.routines.notifications.impl._notificationShowWakeUp

/*

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.text.Spanned
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import java.util.*

const val WAKE_UP_NOTIFICATION_ID =1

const val SLEEP_NOTIFICATION_ID =65535
const val REST_NOTIFICATION_ID =65534
const val ACTIVITY_NOTIFICATION_ID = 65533
const val TIMER_NOTIFICATION_ID = 65532

const val NOTIFICATION_CHANNEL_ONE  = "channel"
const val NOTIFICATION_CHANNEL_TWO  = "channel_MAX"
const val NOTIFICATION_TAG = "routines"

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

fun Context.notificationShowWakeUp(tasks:List<Any>? = null,
                                   mainPendingIntent: PendingIntent?,
                                   dismissPendingIntent: PendingIntent? = null,
                                   dismissable:Boolean = true,
                                   smallRemoteView: RemoteViews? = null,
                                   bigRemoteView:RemoteViews?= null) {

    Toast.makeText(this, "Wake up", Toast.LENGTH_SHORT).show()
//    _notificationShowWakeUp(tasks,mainPendingIntent,dismissPendingIntent,dismissable,smallRemoteView,bigRemoteView)
}


/**
 * Notify other devices that they should build a notification of type WAKE UP
 *//*

fun Context.notificationShowWakeUpRemote(tasks: ArrayDeque<Long>){

}

*/
/**
 * Builds a mirrored notification both on the Local device and on
 * other connected nodes. When either is ACTIONED, the same action occurs on both devices.
 * @param tasks is the task list to show
 *//*

fun Context.notificationShowWakeUpMirror(tasks:ArrayDeque<Long>){

}

*/
/**
 * Builds a local notification
 * @param tasks is the string of tasks to display
 *//*

fun Context.notificationShowWakeUpLocal(tasks:List<Task>){
}

*/
/**
 * Notify remote nodes that they should remove their Wake Up Notifications
 * if they are displaying one
 *//*

fun Context.notificationDismissWakeUpRemote(){

}

*/
/**
 * Convenience
 * Cancels (removes) the Wake up Notification if there is one
 *//*

fun Context.notificationDismissWakeUp(){

}

*/
/**
 * Convenience method to dismiss all wake up notifications across devices
 *//*

fun Context.notificationDismissWakeUpMirror(){


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

}

*/
/**
 * Builds a mirrored notification both on the Local device and on
 * other connected nodes. When either is ACTIONED, the same action occurs on both devices.
 * @param task is the task to show
 *//*

fun Context.notificationShowTaskMirror(task:Task,history:TaskHistory?= null){
}

fun Context.notificationShowTaskLocal(task:Task, history:TaskHistory? = null){

}

fun Context.removeAllNotificationsExceptSpecified(tid:Int){

}

*/
/**
 * Show a task remotely
 *//*

fun Context.notificationShowTaskRemote(task:Task, history:TaskHistory?=null){

}

fun Context.notificationDismissTaskMirror(tid:Long){

}

*/
/**
 * Convenience
 * Cancels (removes) the Task  Notification if there is one
 * @param task is the task to remove
 *//*

fun Context.notificationDismissTask(tid:Int) {

}

*/
/**
 * Notify remote nodes that they should remove their task Notifications
 * if they are displaying one
 *//*

fun Context.notificationDismissTaskRemote(){

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

}

*/
/********************************************************************************
 * SLEEP NOTIFICATION FUNCTIONS
 *********************************************************************************//*


fun Context.notificationShowSleepMirror(){
}

fun Context.notificationShowSleepLocal(){


fun Context.notificationShowSleep(
        mainPendingIntent: PendingIntent? = null,
        dismissPendingIntent: PendingIntent? = null,
        dismissable: Boolean = true,
        smallRemoteView: RemoteViews? = null,
        bigRemoteView: RemoteViews? = null
){

}

fun Context.notificationDismissSleepRemote(){


}

fun Context.notificationDismissSleepLocally(){

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

}

fun determineOnGoingAbility(builder:NotificationCompat.Builder, dismissable:Boolean){

}


//TODO ADD common functionality to remove notifications!
//TODO when switching between tasks, make notification priority low so it doesn't show up all the time

//clean
fun Context.notificationShowTimer(){


}


//clean
fun Context.notificationShowRest(){


}

fun Context.notificationShowActivity(activity:String, int:Int){


}


fun prepareBigTextStyle(tasks:String,title:Spanned):NotificationCompat.BigTextStyle{
    return NotificationCompat.BigTextStyle()
            .setBigContentTitle(title)
            .bigText(getHtml(tasks))
}

fun Context.getBuilder(): NotificationCompat.Builder{


}

fun getHtml(htmlString:String): Spanned {

}

*/
/**
 * retrieve the notification manager.
 *//*

fun Context.getNotificationManager(): NotificationManager {
    `
}

*/
