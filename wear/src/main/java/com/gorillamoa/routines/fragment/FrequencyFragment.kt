package com.gorillamoa.routines.fragment

import android.content.Context
import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper

import com.gorillamoa.routines.R
import com.gorillamoa.routines.adapter.FrequencyPickerAdapter
import com.gorillamoa.routines.adapter.OffsetItemDecoration
import kotlinx.android.synthetic.main.fragment_frequency_picker.*

class FrequencyFragment : Fragment() {

    var submit:((Float)->Any?)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_frequency_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val snapHelperAmount = LinearSnapHelper()
        val snapHelperTime = LinearSnapHelper()

        recyclerAmount?.apply {
            adapter = FrequencyPickerAdapter(arrayOf("Once", "Twice","3x","4x", "5x","10x" ,"Custom"))
            layoutManager =   GridLayoutManager(context, 1).apply {
                orientation = GridLayoutManager.HORIZONTAL
                scrollToPosition(0)
            }
            addItemDecoration(OffsetItemDecoration(getWindowManager()))
        }
        snapHelperAmount.attachToRecyclerView(recyclerAmount)

        recyclerTime?.apply {
            adapter = FrequencyPickerAdapter(arrayOf("Day", "2 Days","3 Days","Week","Month","2 Months","6 Month","Year" ))
            layoutManager =   GridLayoutManager(context, 1).apply {
                orientation = GridLayoutManager.HORIZONTAL
                scrollToPosition(0)
            }
            addItemDecoration(OffsetItemDecoration(getWindowManager()))
        }

        snapHelperTime.attachToRecyclerView(recyclerTime)

        submitButton.setOnClickListener {


            submit?.invoke(1.0f)
        }
    }

    private fun getWindowManager():WindowManager{
        return context!!.getSystemService(Context.WINDOW_SERVICE)  as WindowManager
    }



    companion object {

        @JvmStatic
        fun newInstance(callback:((Float)->Unit)?, editcallback:(()->Any?)? = null) = FrequencyFragment().apply {
            submit  = callback
        }
    }
}
