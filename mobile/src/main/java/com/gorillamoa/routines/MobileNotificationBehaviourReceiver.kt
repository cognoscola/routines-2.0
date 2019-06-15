package com.gorillamoa.routines

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.gorillamoa.routines.core.extensions.*

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
    }

    override fun onReceive(context: Context, intent: Intent?) {

        intent?.let {

            val tid = intent.getIntExtra(com.gorillamoa.routines.core.extensions.TASK_ID,-1)

            Log.d("$tag onReceive","We received.. at least")

            when (intent.action) {
                /**
                 * Expand the currently showing notification
                 */
                ACTION_WAKEUP_EXPAND -> {

                    //Show bigger intent
                    Toast.makeText(context, "Expand", Toast.LENGTH_SHORT).show()

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
                                    getRemoteView().createExpandFunction(this,intent.getStringExtra(TASK_DATA)),
                                    getLargeRemoteView(intent.getStringExtra(TASK_DATA))
                                            .createCollapseFunction(this,intent.getStringExtra(TASK_DATA))
                            )
                        }
                    } else {
                        //TODO OPEN UP THE TASK VIEW ACTIVITY
                    }
                }
                ACTION_WAKEUP_COLLAPSE ->{

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
                                    getRemoteView().createExpandFunction(context,intent.getStringExtra(TASK_DATA)),
                                    null)

                        }
                    } else {
                        //TODO OPEN UP THE TASK VIEW ACTIVITY
                    }
                }

                else -> {
                    Log.d("onReceive","Unknown Action on Task ${intent.getIntExtra(com.gorillamoa.routines.core.extensions.TASK_ID,-1)}")
                }
            }
        }
    }
}
