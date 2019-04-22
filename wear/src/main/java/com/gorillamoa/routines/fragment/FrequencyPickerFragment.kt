package com.gorillamoa.routines.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import com.gorillamoa.routines.R
import com.gorillamoa.routines.extensions.createSimplePicker
import kotlinx.android.synthetic.main.fragment_frequency_picker.*

class FrequencyPickerFragment : Fragment() {

    private var submit:((Float)->Any?)? = null

    private var amountValue:Float = 1.0f
    private var timeValue:Float = 1.0f
    private var frequency = 0.0f

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_frequency_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val amountArray = arrayOf("Once", "Twice","3x","4x", "5x","10x")
        recyclerAmount?.createSimplePicker(amountArray){
            amountValue = getAmount(amountArray[it])
            frequency = findFrequency(amountValue, timeValue)
            frequency
        }

        val timeArray = arrayOf("Day", "2 Days","3 Days","Week","Month","2 Months","6 Months","Year" )
        recyclerTime?.createSimplePicker(timeArray){

            timeValue = getTimeSpan(timeArray[it])
            frequency = findFrequency(amountValue, timeValue)
            frequency
        }

        submitButton.setOnClickListener {
            submit?.invoke(frequency)
        }

        //by default our frequency is once per day
        frequency =  findFrequency(amountValue,timeValue)
    }


    companion object {

        @JvmStatic
        fun newInstance(callback:((Float)->Unit)?, editcallback:(()->Any?)? = null) = FrequencyPickerFragment().apply {
            submit  = callback
        }
    }

    private fun findFrequency(amount:Float, timeSpan:Float):Float = amount.div(timeSpan)

    private fun getAmount(amountString:String):Float{

        return when(amountString){

            "Once" -> 1.0f
            "Twice" -> 2.0f
            "3x" -> 3.0f
            "4x" -> 4.0f
            "5x" -> 5.0f
            "10x" -> 10.0f
            else -> 1.0f

        }
    }

    private fun getTimeSpan(timeString:String):Float{

        return when (timeString) {
            "Day" -> 1.0f
            "2 Days" -> 2.0f
            "3 Days" -> 3.0f
            "4 Days" -> 4.0f
            "Week" -> 7.0f
            "Month" -> 30.0f
            "2 Months" -> 60.0f
            "6 Months" -> (30.0*6.0f).toFloat()
            "Year" -> 365.0f
            else -> 1.0f
        }
    }

}
