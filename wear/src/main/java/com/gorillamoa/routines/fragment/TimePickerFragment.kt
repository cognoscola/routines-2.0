package com.gorillamoa.routines.fragment

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class TimePickerFragment: Fragment(){

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return TextView(context).apply {
            text = "Time picker fragment It worked"
        }
    }
}