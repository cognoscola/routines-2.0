package com.gorillamoa.routines.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.gorillamoa.routines.notifications.impl.RoutinesNotificationBuilder
import com.gorillamoa.routines.notifications.impl._getBuilder
import com.gorillamoa.routines.notifications.impl._notificationShowWakeUp
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
                                   bigRemoteView:RemoteViews?= null) {


    _notificationShowWakeUp(
            tasks,
            mainPendingIntent,
            dismissPendingIntent,
            dismissable,
            smallRemoteView,
            bigRemoteView,
            NOTIFICATION_CHANNEL_ONE,
            NOTIFICATION_TAG,
            WAKE_UP_NOTIFICATION_ID)
}


/**
 * Notify other devices that they should build a notification of type WAKE UP
 */

fun Context.notificationShowWakeUpRemote(tasks: ArrayDeque<Long>){
//TODO complete function
}

/**
 * Builds a mirrored notification both on the Local device and on
 * other connected nodes. When either is ACTIONED, the same action occurs on both devices.
 * @param tasks is the task list to show
 */
fun Context.notificationShowWakeUpMirror(tasks:ArrayDeque<Long>){
//TODO complete function
}

/**
 * Builds a local notification
 * @param tasks is the string of tasks to display
 *//*

fun Context.notificationShowWakeUpLocal(tasks:List<Task>){
//TODO complete function
}

*/
/**
 * Notify remote nodes that they should remove their Wake Up Notifications
 * if they are displaying one
 *//*

fun Context.notificationDismissWakeUpRemote(){
//TODO complete function
}

*/
/**
 * Convenience
 * Cancels (removes) the Wake up Notification if there is one
 *//*

fun Context.notificationDismissWakeUp(){
//TODO complete function
}

*/
/**
 * Convenience method to dismiss all wake up notifications across devices
 *//*

fun Context.notificationDismissWakeUpMirror(){

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
 *//*

fun Context.notificationShowTaskMirror(task:Task,history:TaskHistory?= null){
//TODO complete function
}

fun Context.notificationShowTaskLocal(task:Task, history:TaskHistory? = null){
//TODO complete function
}

fun Context.removeAllNotificationsExceptSpecified(tid:Int){
//TODO complete function
}

*/
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
 *//*

fun Context.notificationShowRemote(taskData:String, path:String){
//TODO complete function
}

fun determineOnGoingAbility(builder:NotificationCompat.Builder, dismissable:Boolean){
//TODO complete function
}


//TODO ADD common functionality to remove notifications!
//TODO when switching between tasks, make notification priority low so it doesn't show up all the time

//clean
fun Context.notificationShowTimer(){

//TODO complete function
}


//clean
fun Context.notificationShowRest(){
//TODO complete function

}

fun Context.notificationShowActivity(activity:String, int:Int){

//TODO complete function
}


fun prepareBigTextStyle(tasks:String,title:Spanned):NotificationCompat.BigTextStyle{
    return NotificationCompat.BigTextStyle()
            .setBigContentTitle(title)
            .bigText(getHtml(tasks))
}
*/

//create an abstract class RoutinesNotificationBuilder
fun Context.getNotificationBuilder(channel:String,isWatch:Boolean): NotificationCompat.Builder{
    return _getBuilder(channel,isWatch)
}


/*

fun getHtml(htmlString:String): Spanned {
//TODO complete function
}

*/

/**
 * Get the notification manager
 * @receiver Context
 * @return NotificationManager
 */
fun Context.getNotificationManager(): NotificationManager {

    return getSystemService(NotificationManager::class.java) as NotificationManager
}
