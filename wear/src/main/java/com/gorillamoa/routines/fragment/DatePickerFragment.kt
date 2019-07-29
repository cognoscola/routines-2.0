package com.gorillamoa.routines.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.gorillamoa.routines.R
import com.gorillamoa.routines.adapter.DoubleRowSelectAdapter
import com.gorillamoa.routines.extensions.createDoubleRowPicker
//TODO
//import kotlinx.android.synthetic.main.fragment_timepicker.*
import java.util.*

class DatePickerFragment: Fragment(){

    /**
     * If the value is -1 it means the user hasn't chosen yet or undid his choice
     * all Other values means that the user has picked a time
     */
    private val cal = Calendar.getInstance()

    private lateinit var monthArray:Array<String>
    private var submit:((Calendar)->Any?)? = null


    companion object {

        @JvmStatic
        fun newInstance(callback:((Calendar)->Any?)) = DatePickerFragment().apply {
            submit  = callback
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        //TODO time picker for ear, layout
        return TextView(context)
//        return inflater.inflate(R.layout.fragment_timepicker,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        monthArray= resources.getStringArray(R.array.month_names)

        //TODO time picker for ear, layo
//        displayText?.text = context?.getString(R.string.picker_date_title)

        setAdapterToMonth()
        //TODO time picker for ear, layo
        /*backwardButton.setOnClickListener {

            timeTextView.visibility = View.INVISIBLE
            backwardButton.visibility = View.INVISIBLE
            setAdapterToMonth()

        }*/
    }

    private fun updatePickedText(){

        val textToShow = "${monthArray[cal.get(Calendar.MONTH)]} ${cal.get(Calendar.DAY_OF_MONTH)} "
//        timeTextView.text =  textToShow
        readyButtonForClick()
    }


    /**
     * Prepare the button so that when it is clicked it will
     * set an alarm.
     */
    private fun readyButtonForClick(){

//        backwardButton.visibility = View.VISIBLE
//        timeTextView.visibility = View.VISIBLE
        /*timeTextView.setOnClickListener {

            submit?.invoke(cal)
        }*/
    }

    private fun setAdapterToDaysOfMonth(month:Int){

        val array = Array(when (month) {
            0 -> 31
            1 -> 28
            2 -> 31
            3 -> 30
            4 -> 31
            5 -> 30
            6 -> 31
            7 -> 31
            8 -> 30
            9 -> 31
            10 -> 30
            11 -> 31
            else -> 30
        }){ position -> (position + 1).toString() }

        //TODO
        /*buttonRecyclerView.createDoubleRowPicker(
                array,
                DoubleRowSelectAdapter.STYLE_NUMBER){
            cal.set(Calendar.DAY_OF_MONTH, it.toInt() )
            updatePickedText()
        }*/
    }

    private fun setAdapterToMonth(){
        //Choices

        //TODO
        /*buttonRecyclerView.createDoubleRowPicker(
                monthArray,
                DoubleRowSelectAdapter.STYLE_LONGTEXT){

            val month = monthArray.indexOf(it)

            //Ideally, Jan = 0, Dec = 11
            cal.set(Calendar.MONTH,month)

            //Now we pick the day of the month
            setAdapterToDaysOfMonth(month)

            updatePickedText()
        }*/

    }
}