package com.gorillamoa.routines.activity

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.wear.ambient.AmbientModeSupport
import android.util.Log
import androidx.lifecycle.Observer

import com.gorillamoa.routines.R
import com.gorillamoa.routines.data.Task
import com.gorillamoa.routines.extensions.*
import com.gorillamoa.routines.viewmodel.TaskViewModel
import kotlinx.android.synthetic.main.activity_service_controller.*
import java.util.*

/**
 * A few notes on this class.
 * We use support FragmentActivity instead of WearableActivity() because the
 * ViewModelProvider only supports these types of activities.
 * Note also that we must use  LiveData from android.arch.livecycle and NOT androidx
 */
class ServiceControllerActivity : FragmentActivity(), AmbientModeSupport.AmbientCallbackProvider {


    @Suppress("unused")
    private val tag = ServiceControllerActivity::class.java.name

    private lateinit var taskViewModel: TaskViewModel

    /**
     * Ambient mode controller attached to this display. Used by Activity to see if it is in ambient
     * mode.
     */
    private var mAmbientController: AmbientModeSupport.AmbientController? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_controller)

        mAmbientController = AmbientModeSupport.attach(this@ServiceControllerActivity)

        taskViewModel = ViewModelProviders.of(this@ServiceControllerActivity).get(TaskViewModel::class.java)
        taskViewModel.loadTasks()
        taskViewModel.tasks.observe(this, Observer {


            it.forEach {
                Log.d("onCreate","A task ${it.name} was inserted")
            }
        })

        /**get the view model object */

       // taskViewModel = ViewModelProviders.of(this)

        /** get our local settings*/
        wakeUpAlarmToggle.apply {

            isChecked = isAlarmSet()

            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    alarmEnableWakeUp()
                } else {
                    alarmDisableWakeUp()
                }
            }
        }


        enableServiceButton?.setOnClickListener {
            broadcastShowWakeUp()
        }


        disableServiceButton?.setOnClickListener {
            broadcastShowRandomTask()
        }

        createTask?.setOnClickListener {

            val cal =Calendar.getInstance()
            cal.timeInMillis = System.currentTimeMillis()
            taskViewModel.insert(Task(name = "Task:${cal.get(Calendar.HOUR)}:${cal.get(Calendar.MINUTE)}:${cal.get(Calendar.SECOND)}"))
        }
    }



    override fun getAmbientCallback(): AmbientModeSupport.AmbientCallback {
        return MyAmbientCallback()
    }

    private class MyAmbientCallback : AmbientModeSupport.AmbientCallback() {

        override fun onEnterAmbient(ambientDetails: Bundle?) {
            // Handle entering ambient mode
            Log.d("onEnterAmbient","")
        }

        override fun onExitAmbient() {
            // Handle exiting ambient mode
            Log.d("onExitAmbient","")
        }

        override fun onUpdateAmbient() {
            // Update the content
            Log.d("onUpdateAmbient","")
        }
    }

}
