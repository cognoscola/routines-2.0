package com.gorillamoa.routines

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.gorillamoa.routines.core.constants.DataLayerConstant.Companion.KEY_TASK_DATA
import com.gorillamoa.routines.core.data.Task
import com.gorillamoa.routines.core.extensions.*
import com.gorillamoa.routines.notifications.NOTIFICATION_TAG
import com.gorillamoa.routines.notifications.getNotificationManager
import com.gorillamoa.routines.notifications.notificationShowTask

/**
 * This class exists because we wish to modify how to the noficiation will behave on
 *  mobile. Notifications behave differently on Mobile than on Wear, hence why this class
 *  is existing in the Mobile Module
 *
 *  Particularly, we want to only show Small (collapsed) notification until the user clicks
 *  the expand button, at which point we'll just show a regular custom notification
 */
class MobileNotificationBehaviourReceiver: BroadcastReceiver(){
    @Suppress("unused")
    private val tag:String = MobileNotificationBehaviourReceiver::class.java.name

    companion object {

        /**
         * The user has the option of keeping the notification expanded or collapsed
         */
        const val ACTION_WAKEUP_EXPAND = "wakeup.expand"

        /**
         * The user has the option of keeping the notification only collapsed
         */
        const val ACTION_WAKEUP_COLLAPSE = "wakeup.collapse"

        /**
         * Action to take when the user clicks on the notification
         */
        const val ACTION_TASK_EXPAND = "task.expand"

        /**
         * Action to take when the user clicks on the collapse button
         */
        const val ACTION_TASK_COLLAPSE = "task.collapse"

    }

    override fun onReceive(context: Context, intent: Intent?) {

        intent?.let {

            val tid = intent.getIntExtra(TASK_ID,-1)

            Log.d("$tag Mobile onReceive","We received.. at least")

            when (intent.action) {
                /**
                 * Expand the currently showing notification
                 */
                ACTION_WAKEUP_EXPAND -> {

                    //Show bigger intent
                    Toast.makeText(context, "Expand", Toast.LENGTH_SHORT).show()

                    //TODO FIX THIS LATER Not important now
/*
                    if (intent.hasExtra(TASK_DATA)) {
                        context.apply {
                            getNotificationManager().cancel(NOTIFICATION_TAG, WAKE_UP_NOTIFICATION_ID)
                            notificationShowWakeUp(
                                    intent.getStringExtra(TASK_DATA),
                                    //TODO MAKE notification click go to task list
                                    null,
                                    //TODO record the dismissal
                                    null,
                                    false,
//                                    createWakeUpRemoteView().createFunction(this,intent.getStringExtra(TASK_DATA)),
                                    bigRemoteView = getLargeWakeUpRemoteView(intent.getStringExtra(TASK_DATA))
                                            .createCollapseFunction(this,intent.getStringExtra(TASK_DATA))
                            )
                        }
                    } else {
                        //TODO OPEN UP THE TASK VIEW ACTIVITY
                    }
*/
                }
                ACTION_WAKEUP_COLLAPSE ->{

                    //TODO do this later
/*
                    Toast.makeText(context, "Collapse", Toast.LENGTH_SHORT).show()
                    if (intent.hasExtra(TASK_DATA)) {
                        context.apply {
                            getNotificationManager().cancel(NOTIFICATION_TAG, WAKE_UP_NOTIFICATION_ID)

                            notificationShowWakeUp(
                                    intent.getStringExtra(TASK_DATA),
                                    //TODO MAKE notification click go to task list
                                    null,
                                    //TODO record the dismissal
                                    null,
                                    false,
                                    createWakeUpRemoteView(0).createFunction(context,intent.getStringExtra(TASK_DATA), ACTION_WAKEUP_EXPAND),
                                    null)
                        }
                    } else {
                        //TODO OPEN UP THE TASK VIEW ACTIVITY
                    }
*/
                }
                ACTION_TASK_EXPAND->{
                    if (intent.hasExtra(KEY_TASK_DATA)) {
                        context.apply {

                            getNotificationManager().cancel(NOTIFICATION_TAG,tid)


                            val task = context.getGson().fromJson(intent.getStringExtra(KEY_TASK_DATA), Task::class.java)

                            notificationShowTask(
                                    null,
                                    mainPendingIntent = null,
                                    dismissPendingIntent = null,
                                    dismissable = false,
                                    //TODO just delete this and return null
                                    //TODO replace the task with a stringified version it it
                                    smallRemoteView = null,
                                    bigRemoteView = null
                            )
                        }
                    }else{
                        //TODO OPEN UP TASK VIEW ACTIVITY
                    }
                }
                ACTION_TASK_COLLAPSE ->{

                }

                else -> {
                    Log.d("onReceive","Unknown Action on Task ${intent.getIntExtra(TASK_ID,-1)}")
                }
            }
        }
    }
}
