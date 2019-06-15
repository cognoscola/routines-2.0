package com.gorillamoa.routines

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import com.gorillamoa.routines.MobileNotificationBehaviourReceiver.Companion.ACTION_WAKEUP_COLLAPSE
import com.gorillamoa.routines.MobileNotificationBehaviourReceiver.Companion.ACTION_WAKEUP_EXPAND
import com.gorillamoa.routines.core.extensions.TASK_DATA
import com.gorillamoa.routines.core.extensions.createNotificationActionPendingIntent
import com.gorillamoa.routines.core.extensions.getHtml
import com.gorillamoa.routines.core.receiver.NotificationActionReceiver.Companion.ACTION_START_DAY

fun Context.getLargeRemoteView(bigStringContent: String): RemoteViews {
    val remoteViews = RemoteViews(packageName, R.layout.remote_wakeup_large)
    remoteViews.setTextViewText(R.id.title, getHtml(getString(R.string.wake_up_large_title)))
    remoteViews.setTextViewText(R.id.bigContent, getHtml(bigStringContent))

    //Give option to collapse
    remoteViews.setImageViewResource(R.id.behaviourImage, R.drawable.ic_expand_less_black_24dp)
    remoteViews.setTextViewText(R.id.behaviourText, getString(R.string.collapse))

    setStartFunction(remoteViews)
    return remoteViews
}

fun Context.setStartFunction(remoteViews: RemoteViews) {

        remoteViews.setOnClickPendingIntent(R.id.start, createNotificationActionPendingIntent(-1, ACTION_START_DAY))
}

//TODO CONFIGURE appearance for empty tasks
fun RemoteViews.createExpandFunction(context:Context,tasks:String?):RemoteViews{

     try {

        tasks?.let {
            val intent = Intent(context, MobileNotificationBehaviourReceiver::class.java)
            intent.action = ACTION_WAKEUP_EXPAND
            intent.putExtra(TASK_DATA, it)
            val pIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            setOnClickPendingIntent(R.id.behaviourButton, pIntent)

        }

    } catch (ignored: ClassNotFoundException) {
        Log.e("Unknown Activity Name",ignored.message)
    }
    return this
}

fun RemoteViews.createCollapseFunction(context: Context,tasks: String?):RemoteViews{

    try {

        tasks?.let {
            val intent = Intent(context, MobileNotificationBehaviourReceiver::class.java)
            intent.action = ACTION_WAKEUP_COLLAPSE
            intent.putExtra(TASK_DATA, it)
            val pIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            setOnClickPendingIntent(R.id.behaviourButton, pIntent)
        }

    } catch (ignored: ClassNotFoundException) {
        Log.e("Unknown Activity Name",ignored.message)
    }
    return this
}




fun Context.getRemoteView(): RemoteViews {

    val remoteViews = RemoteViews(packageName, R.layout.remote_wakeup)
    remoteViews.setTextViewText(R.id.title, getHtml(getString(R.string.wake_up_title)))
    remoteViews.setTextViewText(R.id.description, getHtml(getString(R.string.wake_up_description)))
    remoteViews.setImageViewResource(R.id.behaviourImage, R.drawable.ic_expand_more_black_24dp)
    remoteViews.setTextViewText(R.id.behaviourText, getString(R.string.expand))
    setStartFunction(remoteViews)
    return remoteViews
}