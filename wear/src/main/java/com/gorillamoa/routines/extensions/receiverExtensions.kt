package com.gorillamoa.routines.extensions

import android.app.NotificationManager

import android.content.Context

fun getNotificationManager(context:Context):NotificationManager{
    return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
}