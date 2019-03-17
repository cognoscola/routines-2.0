package com.gorillamoa.routines.fragment


import androidx.lifecycle.ViewModelProviders
import androidx.fragment.app.Fragment
import com.gorillamoa.routines.viewmodel.TaskViewModel

class LiveDataHeadless: Fragment(){

    override fun onStart() {
        super.onStart()

        ViewModelProviders.of(this).get(TaskViewModel::class.java)

    }


}