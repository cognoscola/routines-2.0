package com.gorillamoa.routines.activity

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.wear.ambient.AmbientModeSupport
import android.util.Log
import androidx.lifecycle.Observer

import com.gorillamoa.routines.R
import com.gorillamoa.routines.data.Task
import com.gorillamoa.routines.extensions.*
import com.gorillamoa.routines.fragment.TimePickerFragment
import com.gorillamoa.routines.scheduler.TaskScheduler
import com.gorillamoa.routines.viewmodel.TaskViewModel
import kotlinx.android.synthetic.main.activity_service_controller.*
import java.lang.StringBuilder
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

    companion object {

        private const val PICKER_CODE_WAKE = 100
        private const val PICKER_CODE_SLEEP = 101
    }

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

            notificationShowWakeUp(
                    StringBuilder().stringifyTasks(it),
                    createNotificationMainIntentForWakeUp())
        })

        /**get the view model object */

       // taskViewModel = ViewModelProviders.of(this)

        wakePickerButton.setOnClickListener {

            startActivityForResult(
                    Intent(this,SettingsActivity::class.java).apply {
                        putExtra(TimePickerFragment.DISPLAY_TEXT,getString(R.string.onboard_wake_up_text))
            }, PICKER_CODE_WAKE)
        }

        /** get our local settings*/
        wakeUpAlarmToggle.apply {

            isChecked = isWakeAlarmSet()


            //CLEAN
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    alarmEnableWakeUp()
                } else {
                    alarmDisableWakeUp()
                }
            }
        }

        sleepPickerButton.setOnClickListener {
            startActivityForResult(
                    Intent(this,SettingsActivity::class.java).apply {
                        putExtra(TimePickerFragment.DISPLAY_TEXT,getString(R.string.onboard_sleep_text))
                    }, PICKER_CODE_SLEEP)
        }

        sleepAlarmToggle.apply {
            isChecked = isSleepAlarmSet()

            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    alarmEnableSleep()
                }else{
                    alarmDisableSleep()
                }
            }
        }

        wakeNotificationButton?.setOnClickListener {
            broadcastShowWakeUp()
        }

        //Show the sleep notification
        sleepNotificationButton?.setOnClickListener {
            notificationShowSleep()
        }

        //clean truncate notificationShowTask function
        taskNotificationButton?.setOnClickListener {
            TaskScheduler.getNextTask(this) { task ->
                task?.let {
                    notificationShowTask(it)
                }
            }
        }

        createTask?.setOnClickListener {

            //TODO show on notification updated tasks
            val cal =Calendar.getInstance()
            cal.timeInMillis = System.currentTimeMillis()
            taskViewModel.insertAndReturnList(Task(name = "Task:${cal.get(Calendar.HOUR)}:${cal.get(Calendar.MINUTE)}:${cal.get(Calendar.SECOND)}"))
//            taskViewModel.insert()
        }

        clearTask?.setOnClickListener {
            //TODO show better notification on empty tasks
            taskViewModel.clearReturnList()
        }

        dummy?.setOnClickListener {

            //We'll create several dummy tasks on to which we can test things
            taskViewModel.dummy()

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK){
            when (requestCode) {
                PICKER_CODE_WAKE -> {

                    //TODO truncate these codes
                    val cal = Calendar.getInstance()
                    setSleepTimeToCalendarAndStore(cal,
                            data?.getIntExtra(TimePickerFragment.HOUR,-1)?:-1,
                            data?.getIntExtra(TimePickerFragment.MIN,-1)?:-1,
                            data?.getIntExtra(TimePickerFragment.PHASE,-1)?:-1)
                    alarmSetRepeatWithCal(cal,true)
                    wakeUpAlarmToggle?.isChecked = true
                }
                PICKER_CODE_SLEEP -> {
                    val cal = Calendar.getInstance()
                    setWakeTimeToCalendarAndStore(cal,
                            data?.getIntExtra(TimePickerFragment.HOUR,-1)?:-1,
                            data?.getIntExtra(TimePickerFragment.MIN,-1)?:-1,
                    data?.getIntExtra(TimePickerFragment.PHASE,-1)?:-1)
                    sleepAlarmToggle?.isChecked = true
                    alarmSetRepeatWithCal(cal,false)
                }
            }
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
