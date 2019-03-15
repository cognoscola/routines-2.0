package com.gorillamoa.routines.fragment

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gorillamoa.routines.R
import com.gorillamoa.routines.adapter.TimePickerAdapter
import com.gorillamoa.routines.extensions.alarmSetRepeatWithCal
import com.gorillamoa.routines.extensions.setTimeToCalendarAndStore

import kotlinx.android.synthetic.main.fragment_timepicker.*
import java.util.*

class TimePickerFragment: OnboardFragment(){

    /**
     * If the value is -1 it means the user hasn't chosen yet or undid his choice
     * all Other values means that the user has picked a time
     */
    private var hour = -1
    private var minute = 0


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_timepicker,container,false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


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
    }

    /**
     * Prepare the button so that when it is clicked it will
     * set an alarm.
     */
    private fun readyButtonForClick(){

        backwardButton.visibility = View.VISIBLE
        timeTextView.visibility = View.VISIBLE
        timeTextView.setOnClickListener {
            forwardFunction?.invoke()

            val cal= Calendar.getInstance()
            context.setTimeToCalendarAndStore(cal,hour,minute)
            context.alarmSetRepeatWithCal(cal)

        }
    }

}