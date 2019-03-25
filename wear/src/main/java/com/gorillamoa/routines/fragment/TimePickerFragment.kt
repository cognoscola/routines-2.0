package com.gorillamoa.routines.fragment

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gorillamoa.routines.R
import com.gorillamoa.routines.adapter.TimePickerAdapter

import kotlinx.android.synthetic.main.fragment_timepicker.*

class TimePickerFragment: Fragment(){

    /**
     * If the value is -1 it means the user hasn't chosen yet or undid his choice
     * all Other values means that the user has picked a time
     */
    private var hour = -1
    private var minute = 0

    companion object {
        public const val DISPLAY_TEXT = "DPLTEXT"
        public const val HOUR = "HOUR"
        public const val MIN = "MIN"

    }

    //TODO some how allow user to pick PM AM
    //TODO fix display text error (not showing up)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_timepicker,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        displayText?.text = arguments?.getString(DISPLAY_TEXT)

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

    private var callbackFunction:((Int,Int)->Any?)? = null

    public fun setCallbackFunction(callback:(Int, Int) ->Any?){
        callbackFunction = callback

    }

    /**
     * Prepare the button so that when it is clicked it will
     * set an alarm.
     */
    private fun readyButtonForClick(){

        backwardButton.visibility = View.VISIBLE
        timeTextView.visibility = View.VISIBLE
        timeTextView.setOnClickListener {

            callbackFunction?.invoke(hour,minute)

        }
    }

}