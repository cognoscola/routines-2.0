package com.gorillamoa.routines.onboard.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.gorillamoa.routines.onboard.R
import com.gorillamoa.routines.onboard.adapters.TimePickerAdapter
import kotlinx.android.synthetic.main.fragment_timepicker.*

class TimePickerFragment: Fragment(){

    /**
     * If the value is -1 it means the user hasn't chosen yet or undid his choice
     * all Splash values means that the user has picked a time
     */
    private var hour = -1
    private var minute = 0
    private var phase = 0 //0 means AM, 1 means PM

    companion object {
        const val DISPLAY_TEXT = "DPLTEXT"
        const val HOUR = "HOUR"
        const val MIN = "MIN"
        const val PHASE =  "PHS"

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_timepicker,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        displayText?.text = arguments?.getString(DISPLAY_TEXT)?: context?.getString(R.string.onboard_default_text)
        Log.d("onViewCreated","Found Display Text ${arguments?.getString(DISPLAY_TEXT)}")

        /** populate the recycler view*/
        (buttonRecyclerView as RecyclerView).apply {
            adapter = TimePickerAdapter(12)
            val gridLayoutManager = GridLayoutManager(context, 2)
            gridLayoutManager.orientation = GridLayoutManager.HORIZONTAL
            gridLayoutManager.scrollToPosition(4)
            layoutManager =  gridLayoutManager
            scrollToPosition(4)
            val snapHelper = LinearSnapHelper()
            snapHelper.attachToRecyclerView(this@apply)


            (adapter as TimePickerAdapter).apply {
                setHourClickedCallback {

                    /**
                     * if hour is -1 it means the user has not picked an hour,
                     * we'll have to force minutes to be -1 and change UI
                     * to show minutes
                     */
                    if (hour == -1) {
                        hour = it
                        minute = 0
                        setMinuteState()
                    }

                    updatePickedText()
                }

                setMinuteClickedCallback {

                    minute = it
                    setPhaseState(phase)

                    updatePickedText()
                }

                setPhaseClickedCallback {
                    phase = it
                    updatePickedText()
                }
            }
        }

        backwardButton.setOnClickListener {

            if (hour != -1) {
                hour = -1
                minute = 0
                timeTextView.visibility = View.INVISIBLE
                backwardButton.visibility = View.INVISIBLE
                ((buttonRecyclerView as RecyclerView).adapter as TimePickerAdapter).setHourState()
            }
        }
    }

    private fun updatePickedText(){
        val phase = if (phase == 0) "am" else "pm"

        val textToShow = if (minute == 0) {
            "$hour$phase"
        }else{
            "$hour:${String.format("%02d", minute)}$phase"
        }

        timeTextView.text =  textToShow
        readyButtonForClick()
    }

    private var callbackFunction:((Int,Int,Int)->Any?)? = null

    fun setCallbackFunction(callback:(hour:Int, minute:Int,phase:Int) ->Any?){
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

            callbackFunction?.invoke(hour,minute,phase)

        }
    }

}