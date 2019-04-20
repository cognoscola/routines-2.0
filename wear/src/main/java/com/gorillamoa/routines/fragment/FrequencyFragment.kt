package com.gorillamoa.routines.fragment

import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import com.gorillamoa.routines.R
import kotlinx.android.synthetic.main.fragment_name_picker.*

class FrequencyFragment : Fragment() {

    var submit:((Float)->Any?)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_frequency_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        submitButton.setOnClickListener {


            submit?.invoke(1.0f)
        }
    }


    companion object {

        @JvmStatic
        fun newInstance(callback:((Float)->Unit)?, editcallback:(()->Any?)? = null) = FrequencyFragment().apply {
            submit  = callback
        }
    }
}
