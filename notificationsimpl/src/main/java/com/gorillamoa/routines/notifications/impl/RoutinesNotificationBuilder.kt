package com.gorillamoa.routines.notifications.impl

import android.content.Context
import androidx.core.app.NotificationCompat

class RoutinesNotificationBuilder(
        context: Context,
        channel:String,
        private val isWatch:Boolean
        ) :NotificationCompat.Builder(context,channel){



        fun setContent(title:String,content:String, taskCount:Int){

                if (isWatch) {
                        setContentText(content)
                        setContentTitle(title)

                }else{






                }

        }
}