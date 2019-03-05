package com.gorillamoa.routines.fragment

import android.app.AlarmManager
import android.app.Fragment
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.gorillamoa.routines.R
import com.gorillamoa.routines.adapter.TimePickerAdapter

import com.gorillamoa.routines.receiver.WakeUpReceiver
import kotlinx.android.synthetic.main.fragment_timepicker.*

class TimePickerFragment: Fragment(){

    /**
     * If the value is -1 it means the user hasn't chosen yet or undid his choice
     * all other values means that the user has picked a time
     */
    private var hour = -1
    private var minute = 0

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

        /** populate the recycler view*/
        buttonRecyclerView.adapter = TimePickerAdapter(12)
        val gridLayoutManager = GridLayoutManager(context, 2)
        gridLayoutManager.orientation = GridLayoutManager.HORIZONTAL
        gridLayoutManager.scrollToPosition(4)
        buttonRecyclerView.layoutManager =  gridLayoutManager
        buttonRecyclerView.scrollToPosition(4)
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(buttonRecyclerView)


        (buttonRecyclerView.adapter as TimePickerAdapter).apply {
            setOptionClickedCallBack {

                /**
                 * if hour is -1 it means the user has not picked an hour,
                 * we'll have to force minutes to be -1 and change UI
                 * to show minutes
                 */
                if (hour == -1) {
                    hour = it
                    minute = 0
                    timeTextView.text = "$it"
                    setMinuteState()
                }

                /**
                 * Hour is picked, but minutes isn't, which means this
                 * incoming value is a minute value.
                 */
                 else if (hour != -1) {
                    minute = it
                }


                val textToShow = "$hour:${String.format("%02d", minute)}"
                timeTextView.text =  textToShow
                readyButtonForClick()
            }
        }

        backwardButton.setOnClickListener {

            if (hour != -1) {
                hour = -1
                minute = 0
                timeTextView.visibility = View.INVISIBLE
                backwardButton.visibility = View.INVISIBLE
                (buttonRecyclerView.adapter as TimePickerAdapter).setHourState()
            }
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

    private fun readyButtonForClick(){

        backwardButton.visibility = View.VISIBLE
        timeTextView.visibility = View.VISIBLE
        timeTextView.setOnClickListener {
            Toast.makeText(context,"Alarm set: $hour:$minute",Toast.LENGTH_SHORT).show()
        }
    }
}