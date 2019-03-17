package com.gorillamoa.routines.fragment


import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.Fragment
import com.gorillamoa.routines.viewmodel.TaskViewModel

class LiveDataHeadless: Fragment(){

    override fun onStart() {
        super.onStart()

        ViewModelProviders.of(this).get(TaskViewModel::class.java)

    }


}