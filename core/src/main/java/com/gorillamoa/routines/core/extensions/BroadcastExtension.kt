package com.gorillamoa.routines.core.extensions

import android.content.Context
import android.content.Intent
import android.util.Log
import com.gorillamoa.routines.core.receiver.AlarmReceiver


/**
 * Send a broadcast to the Wake up Receiver.
 * the intent indicates a DEFAULT WAKE UP
 */
fun Context.broadcastShowWakeUp(){

    /** We indicate that the receiver should treat the intent as
     * part of a normal Wake up
     */

    sendBroadcast(createWakeUpRecieverIntent().setAction(AlarmReceiver.EVENT_WAKEUP))
}

/**
 * Send a broadcast to the Wake up Receiver.
 * the intent indicates a ONBOARD WAKE UP
 */
fun Context.broadcastShowWakeUpTest(){
    /** We indicate that the receiver should treat the intent as
     * part of the on-board process
     */
    sendBroadcast(Intent(createWakeUpRecieverIntent()).setAction(AlarmReceiver.ACTION_ONBOARD))

}
//TODO Launch task notification from UI
fun Context.broadcastShowRandomTask(){





    Log.d("broadcastShowRandomTask","")
}