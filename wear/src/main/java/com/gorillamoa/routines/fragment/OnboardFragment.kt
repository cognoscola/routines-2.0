package com.gorillamoa.routines.fragment

import android.app.Fragment
import android.content.Context
import android.util.Log
import com.gorillamoa.routines.activity.OnboardActivity
import java.lang.Exception

open class OnboardFragment:Fragment(){
    open var forwardFunction:(()->Unit)?=null

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        try {
            if (context is OnboardActivity) {
                forwardFunction = context.getForwardFunction()
            }

        } catch (e: Exception) {
            Log.e("OnBoardFragment","Could not get forward function",e)
        }
    }
}