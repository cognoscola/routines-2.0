package com.gorillamoa.routines.fragment


import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import com.gorillamoa.routines.activity.OnboardActivity

open class OnboardFragment: Fragment(){
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