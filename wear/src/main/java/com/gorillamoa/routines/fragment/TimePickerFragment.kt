package com.gorillamoa.routines.fragment

import android.app.AlarmManager
import android.app.Fragment
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gorillamoa.routines.R

import com.gorillamoa.routines.receiver.WakeUpReceiver
import kotlinx.android.synthetic.main.fragment_timepicker.*
import java.util.*

class TimePickerFragment: Fragment(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_timepicker,container,false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //        val timePicker = findViewById<TimePicker>(R.id.wakeuptTimePicker)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent:PendingIntent = Intent(context, WakeUpReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(context,
                    this.resources.getInteger(R.integer.wakeup_alarm_pendingintent_code),
                    intent,
                    0)
        }

        /** Get the wake up and sleep times of the user, via the time picker*/
/*
        nextButton.setOnClickListener {

            // Set the alarm to start at approximately the time the user indicated
            val calendar: Calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, 8)
                set(Calendar.MINUTE,15)
            }

            // With setInexactRepeating(), you have to use one of the AlarmManager interval
            // constants--in this case, AlarmManager.INTERVAL_DAY.
            alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    alarmIntent
            )
        }
*/

    }
}