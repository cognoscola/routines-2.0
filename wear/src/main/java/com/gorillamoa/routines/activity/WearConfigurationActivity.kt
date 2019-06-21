package com.gorillamoa.routines.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.wear.ambient.AmbientModeSupport
import android.util.Log
import androidx.lifecycle.Observer
import com.google.android.gms.wearable.DataItem
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.PutDataRequest
import com.google.android.gms.wearable.Wearable

import com.gorillamoa.routines.R
import com.gorillamoa.routines.core.data.Task
import com.gorillamoa.routines.core.extensions.*
import com.gorillamoa.routines.core.services.DataLayerListenerService.Companion.EVENT_WAKEUP
import com.gorillamoa.routines.fragment.TimePickerFragment
import com.gorillamoa.routines.core.viewmodels.TaskViewModel
import kotlinx.android.synthetic.main.activity_service_controller.*
import java.util.*



/**
 * A few notes on this class.
 * We use support FragmentActivity instead of WearableActivity() because the
 * ViewModelProvider only supports these types of activities.
 * Note also that we must use  LiveData from android.arch.livecycle and NOT androidx
 */
class WearConfigurationActivity : FragmentActivity(), AmbientModeSupport.AmbientCallbackProvider {

    @Suppress("unused")
    private val tag = WearConfigurationActivity::class.java.name

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


    fun remoteNotifyWakeUpActioned(context: Context){

        val putDataReq: PutDataRequest = PutDataMapRequest.create("/day").run {
            dataMap.putBoolean(EVENT_WAKEUP, false)
            asPutDataRequest()
        }
        val putDataTask: com.google.android.gms.tasks.Task<DataItem> = Wearable.getDataClient(context).putDataItem(putDataReq)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_controller)

        mAmbientController = AmbientModeSupport.attach(this@WearConfigurationActivity)

        taskViewModel = connectAndLoadViewModel()
        taskViewModel.tasks.observe(this, Observer {
            if (it.isNotEmpty()) {
                notificationShowWakeUpMirror(it)
            }
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
                    alarmEnableWakeUpPersistent()
                } else {
                    alarmDisableWakePersistent()

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
                    alarmEnableSleepPersistent()
                }else{
                    alarmDisableSleepPersistent()
                }
            }
        }

        /**
         * Here we should what would happen when we receive a
         * broadcast with the WAKE UP COMMANDß
         */
        wakeNotificationButton?.setOnClickListener { broadcastShowWakeUp() }

        /**
         * What should happen when we receive a broadcast with the Sleep Command
         */
        sleepNotificationButton?.setOnClickListener { notificationShowSleep() }

        //clean truncate notificationShowTask function
        taskNotificationButton?.setOnClickListener {
            com.gorillamoa.routines.core.scheduler.TaskScheduler.getNextUncompletedTask(this) { task ->
                task?.let {
                    notificationShowTask(it)
                }
            }
        }

        /**
         * Here we show a sample of what should happen when we want to wake up.
         * There is no broadcasting happening
         */

        wakeUpButton.setOnClickListener{
            broadcastShowWakeUp()
        }

        sleepButton?.setOnClickListener {
            notificationShowSleep()
            com.gorillamoa.routines.core.scheduler.TaskScheduler.endDay(this)
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
            clearSavedArrays()
        }

        //We'll create several dummy tasks on to which we can test things
        dummy?.setOnClickListener { taskViewModel.dummy() }

        dataLayer?.setOnClickListener { view ->

            remoteNotifyWakeUpActioned(this)
        }

    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK){
            when (requestCode) {
                PICKER_CODE_WAKE -> {

                    //TODO truncate these codes
                    val cal = Calendar.getInstance()


                    setWakeTimeToCalendarAndStore(cal,
                            data?.getIntExtra(TimePickerFragment.HOUR,-1)?:-1,
                            data?.getIntExtra(TimePickerFragment.MIN,-1)?:-1,
                            data?.getIntExtra(TimePickerFragment.PHASE,-1)?:-1)

                    alarmSetRepeatWithCal(cal,true)
                    wakeUpAlarmToggle?.isChecked = true
                }
                PICKER_CODE_SLEEP -> {
                    val cal = Calendar.getInstance()

                    setSleepTimeToCalendarAndStore(cal,

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
