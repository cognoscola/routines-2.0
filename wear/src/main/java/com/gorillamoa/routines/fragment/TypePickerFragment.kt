package com.gorillamoa.routines.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gorillamoa.routines.R
import com.gorillamoa.routines.core.data.TaskType
import kotlinx.android.synthetic.main.fragment_task_type.*

class TypePickerFragment:Fragment(){

    var callback:((TaskType)->Any?)? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_task_type,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        goalTextview.setOnClickListener {
            callback?.invoke(TaskType.TYPE_GOAL)
        }

        habitTextView.setOnClickListener {
            callback?.invoke(TaskType.TYPE_HABIT)

        }

        quickTextView.setOnClickListener {
            callback?.invoke(TaskType.TYPE_UNKNOWN)
        }
    }
}