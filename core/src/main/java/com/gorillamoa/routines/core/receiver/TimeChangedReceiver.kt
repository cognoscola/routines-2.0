package com.gorillamoa.routines.core.receiver

import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context


class TimeChangedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action

        if (action == Intent.ACTION_TIME_CHANGED || action == Intent.ACTION_TIMEZONE_CHANGED) {

            //change the alarm times

        }
    }
}