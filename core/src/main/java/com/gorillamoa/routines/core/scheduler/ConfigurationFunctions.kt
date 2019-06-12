package com.gorillamoa.routines.core.scheduler

import android.content.Context
import android.view.View
import com.gorillamoa.routines.core.extensions.createNotificationMainIntentForWakeup
import com.gorillamoa.routines.core.extensions.notificationShowWakeUp

fun View.assignFunction(T:(Context,String)->Any?, className:String){

    setOnClickListener {
        T.invoke(it.context,className)
    }
}

class ConfigurationFunctions{

    companion object {

        fun showWakeUpNotificationFunction():(Context,String)->Any{
            return { context:Context, className:String ->
                TaskScheduler.schedule(context){
                    context.notificationShowWakeUp(
                            it,
                            className.let { context.createNotificationMainIntentForWakeup(className) },
                            null,
                            false)

                }
            }
        }
    }
}