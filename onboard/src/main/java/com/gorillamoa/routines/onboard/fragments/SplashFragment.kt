package com.gorillamoa.routines.onboard.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gorillamoa.routines.onboard.R

class SplashFragment:androidx.fragment.app.Fragment(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_splash,container,false)
    }

    //TODO created fancy splash where the Words ROutines are drawn out like caligraphy

}