package com.gorillamoa.routines.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView

import com.gorillamoa.routines.R
import com.gorillamoa.routines.adapter.SimplePickerAdapter
import com.gorillamoa.routines.adapter.OffsetItemDecoration
import kotlinx.android.synthetic.main.fragment_frequency_picker.*




class FrequencyPickerFragment : Fragment() {

    var submit:((Float)->Any?)? = null

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

        recyclerAmount?.apply {
            val snapHelperAmount = DetectableLinearSnapHelper(this){ position ->
                Log.d("$tag onViewCreated","Snapped To Pos:$position (${amountArray[position]})")

                amountValue = getAmount(amountArray[position])
                frequency = findFrequency(amountValue, timeValue)
                Log.d("$tag onViewCreated","Frequency:$frequency")
                frequency
            }


            layoutManager =   GridLayoutManager(context, 1).apply {
                orientation = GridLayoutManager.HORIZONTAL
                scrollToPosition(0)
            }
            snapHelperAmount.attachToRecyclerView(this)
            adapter = SimplePickerAdapter(amountArray){view ->
                val snapDistance = snapHelperAmount.calculateDistanceToFinalSnap(layoutManager!!, view)
                if (snapDistance!![0] != 0 || snapDistance[1] != 0) {
                    this.smoothScrollBy(snapDistance[0], snapDistance[1])
                }
            }

            addItemDecoration(OffsetItemDecoration(getWindowManager()))

        }

        //TODO Truncate these functions with above code
        val timeArray = arrayOf("Day", "2 Days","3 Days","Week","Month","2 Months","6 Months","Year" )
        recyclerTime?.apply {
            adapter = SimplePickerAdapter(timeArray)
            layoutManager =   GridLayoutManager(context, 1).apply {
                orientation = GridLayoutManager.HORIZONTAL
                scrollToPosition(0)
            }
            addItemDecoration(OffsetItemDecoration(getWindowManager()))
            val snapHelper = DetectableLinearSnapHelper(this){position ->
                Log.d("$tag onViewCreated","Snapped To Pos:$position (${timeArray[position]})")
                timeValue = getTimeSpan(timeArray[position])
                frequency = findFrequency(amountValue, timeValue)
                Log.d("$tag onViewCreated","Frequency:$frequency")
                frequency
            }
            snapHelper.attachToRecyclerView(this)
        }

        submitButton.setOnClickListener {
            submit?.invoke(frequency)
        }

        //by default our frequency is once per day
        frequency =  findFrequency(amountValue,timeValue)
        Log.d("$tag onViewCreated","Frequency:$frequency")
    }

    private fun getWindowManager():WindowManager{
        return context!!.getSystemService(Context.WINDOW_SERVICE)  as WindowManager
    }

    companion object {

        @JvmStatic
        fun newInstance(callback:((Float)->Unit)?, editcallback:(()->Any?)? = null) = FrequencyPickerFragment().apply {
            submit  = callback
        }
    }

    inner class DetectableLinearSnapHelper(var recyclerView: RecyclerView, var snapCallback:((Int)->Any?)?):LinearSnapHelper(){

        var selectedPosition = -1

        override fun findSnapView(layoutManager: RecyclerView.LayoutManager): View? {
            val view = super.findSnapView(layoutManager)

            if (view != null) {
                val newPosition = layoutManager.getPosition(view)

                if ((newPosition != selectedPosition) && (recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE)) {
                    snapCallback?.invoke(newPosition)
                    selectedPosition = newPosition
                }
            }
            return view
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
//        arrayOf("Day", "2 Days","3 Days","Week","Month","2 Months","6 Month","Year" )

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
