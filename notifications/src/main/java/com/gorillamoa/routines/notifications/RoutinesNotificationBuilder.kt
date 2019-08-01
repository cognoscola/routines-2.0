package com.gorillamoa.routines.notifications

import android.content.Context
import androidx.core.app.NotificationCompat

class RoutinesNotificationBuilder(
        context: Context,
        channel:String,
        private val isWatch:Boolean
        ) :NotificationCompat.Builder(context,channel) {

        companion object {
                public var remoteNotificationApi:NotificationRemoteApi? = null
        }

        fun setContent(title: String, content: String, taskCount: Int) {

                if (isWatch) {
                        setContentText(content)
                        setContentTitle(title)
                } else {

                }
        }

        interface NotificationRemoteApi {
                fun notificationShowRemote(context: Context,task:String,path:String)
                fun notificationDismissWakeUpRemote(context: Context)
        }
}