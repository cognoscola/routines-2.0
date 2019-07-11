package com.gorillamoa.routines.notifications.impl

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.text.Html
import android.text.Spanned
import androidx.core.app.NotificationCompat
import com.example.notificationsimpl.R

/**
 * All of these methods return building blocks for making a notification
 */

/**
 * gets a builder object
 * @receiver Context
 * @param channel String
 * @return NotificationCompat.Builder
 */
fun Context.getBuilder(channel:String): NotificationCompat.Builder{

    //TODO UNCOMMENTAND DELETE ABOVE
    //TODO move channel to API
    return NotificationCompat.Builder(this,channel)

            //TODO show weather in one icon in the notification title
            .setContentTitle(getHtml("Good morning! &#127780"))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentText("See today's schedule")

//                                .setDeleteIntent(dismissPendingIntent)
}

/**
 * Prepare a style which the watch can use
 * @param tasks String
 * @param title Spanned
 * @return NotificationCompat.BigTextStyle
 */
fun prepareBigTextStyle(tasks:String,title:Spanned):NotificationCompat.BigTextStyle{
    return NotificationCompat.BigTextStyle()
            .setBigContentTitle(title)
            .bigText(getHtml(tasks))
}


/**
 * Gets the Notification manager
 * @receiver Context
 * @return NotificationManager
 */
fun Context.getNotificationManager(): NotificationManager {
    return getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
}

/**
 * Gets a spannable so that the notification can display special characters and emojis
 * @param htmlString String
 * @return Spanned
 */
fun getHtml(htmlString:String): Spanned {
    //24 and above
    if(Build.VERSION.SDK_INT > Build.VERSION_CODES.N){
        return Html.fromHtml(htmlString, Html.FROM_HTML_MODE_COMPACT)
    }
    //below
    else{
        return Html.fromHtml(htmlString)
    }
}