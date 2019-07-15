package com.gorillamoa.routines.notifications

import android.content.Context
import android.widget.RemoteViews

fun Context.remoteGetSmallTaskView(task: String): RemoteViews {
    return (applicationContext as RemoteInjectorHelper.RemoteGraphProvider).remoteViewGraph.remoteGetSmallTaskView(task)
}

fun Context.remoteGetLargeTaskView(history:String): RemoteViews {
    return (applicationContext as RemoteInjectorHelper.RemoteGraphProvider).remoteViewGraph.remoteGetLargeTaskView(history)
}

fun Context.remoteGetSmallWakeUpView(taskLength:Int): RemoteViews {
    return (applicationContext as RemoteInjectorHelper.RemoteGraphProvider).remoteViewGraph.remoteGetSmallWakeUpView(taskLength)
}

fun Context.remoteGetLargeWakeUpView(tasks:String): RemoteViews {
    return (applicationContext as RemoteInjectorHelper.RemoteGraphProvider).remoteViewGraph.remoteGetLargeWakeUpView(tasks)
}

fun Context.remoteGetSmallSleepView(): RemoteViews {
    return (applicationContext as RemoteInjectorHelper.RemoteGraphProvider).remoteViewGraph.remoteGetSmallSleepView()
}

fun Context.remoteGetLargeSleepView(): RemoteViews {
    return (applicationContext as RemoteInjectorHelper.RemoteGraphProvider).remoteViewGraph.remoteGetLargeSleepView()
}