package com.gorillamoa.routines.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.wear.ambient.AmbientModeSupport
import android.util.Log
import androidx.lifecycle.Observer
import com.google.android.gms.wearable.*

import com.gorillamoa.routines.R
import com.gorillamoa.routines.core.constants.DataLayerConstant
import com.gorillamoa.routines.core.data.Task
import com.gorillamoa.routines.core.extensions.*
import com.gorillamoa.routines.core.scheduler.TaskScheduler
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
class WearConfigurationActivity : FragmentActivity(), AmbientModeSupport.AmbientCallbackProvider,  DataClient.OnDataChangedListener {

    @Suppress("unused")
    private val tag = WearConfigurationActivity::class.java.name

    private lateinit var taskViewModel: TaskViewModel
    val calendar = Calendar.getInstance()

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

        mAmbientController = AmbientModeSupport.attach(this@WearConfigurationActivity)

        taskViewModel = connectAndLoadViewModel()
        taskViewModel.tasks.observe(this, Observer {
            /*if (it.isNotEmpty()) {
                notificationShowWakeUpMirror(it)
            }else{
                notificationShowWakeUpMirror(TaskScheduler.generateEmptyVisibleList())
            }*/

            TaskScheduler.schedule(this){ list->
                list?.let {
                    notificationShowWakeUpMirror(list)
                }
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
         * broadcast with the WAKE UP COMMANDß NT WAKEUP
         */
        wakeNotificationButton?.setOnClickListener {
//            broadcastShowWakeUp()

            taskViewModel.loadTasks()
//            notificationShowWakeUpMirror(taskViewModel.tasks.value!!)
        }

        /**
         * What should happen when we receive a broadcast with the Sleep Command
         */
        sleepNotificationButton?.setOnClickListener { notificationShowSleep() }


        taskNotificationButton?.setOnClickListener {
            TaskScheduler.getNextUncompletedTask(this) { task, history ->
                task?.let {


                    notificationShowTask(it,history)
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
            TaskScheduler.endDay(this)
        }


        createTask?.setOnClickListener {

            //TODO show on notification updated tasks
            val cal =Calendar.getInstance()
            cal.timeInMillis = System.currentTimeMillis()
//            taskViewModel.insertAndReturnList(Task(name = "Task:${cal.get(Calendar.HOUR)}:${cal.get(Calendar.MINUTE)}:${cal.get(Calendar.SECOND)}"))
//            taskViewModel.insert()
            getDataRepository().insertMirror(this, Task(name = "WEAR Task:${cal.get(Calendar.HOUR)}:${cal.get(Calendar.MINUTE)}:${cal.get(Calendar.SECOND)}"))

        }


        clearTask?.setOnClickListener {
            //TODO show better notification on empty tasks
            taskViewModel.clearReturnList()
            clearSavedArrays()
        }

        //We'll create several dummy tasks on to which we can test things
        dummy?.setOnClickListener { taskViewModel.dummy() }

        deleteLast?.setOnClickListener { view ->

//            remoteNotifyWakeUpActioned(this)
            taskViewModel.tasks.value?.lastOrNull()?.let {
                getDataRepository().deleteMirror(this, it)
            }
        }

        updateLast?.setOnClickListener {

            taskViewModel.tasks.value?.lastOrNull()?.let {

                val updatedTask = Task(
                        id = it.id,
                        name = "** ${it.name}",
                        description = it.description,
                        type = it.type,
                        frequency = it.frequency,
                        date = it.date
                )
                getDataRepository().updateMirror(this,updatedTask)
            }
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

    override fun onResume() {
        super.onResume()
        Wearable.getDataClient(this).addListener(this)
    }

    override fun onStop() {
        super.onStop()
        Wearable.getDataClient(this).removeListener(this)
    }


    override fun onDataChanged(dataEventBuffer: DataEventBuffer) {
        for (event in dataEventBuffer) {

            val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
            when (event.type) {
                DataEvent.TYPE_CHANGED -> {

                    if (DataLayerConstant.WAKE_UP_PATH.equals(event.dataItem.uri.path)) {
                        append("Wakeup")
                    } else if (DataLayerConstant.TASK_PATH.equals(event.dataItem.uri.path)) {
                        append("Task")
                    } else if (DataLayerConstant.SLEEP_PATH.equals(event.dataItem.uri.path)) {
                        append("Sleep")
                    } else if (DataLayerConstant.PROGRESS_PATH.equals(event.dataItem.uri.path)) {
                        append("Progress")
                    } else if (DataLayerConstant.DATA_TASK_WEAR_INSERT_PATH.equals(event.dataItem.uri.path)) {
                        append("Wear Insert")
                    } else if (DataLayerConstant.DATA_TASK_MOBILE_INSERT_PATH.equals(event.dataItem.uri.path)) {
                        append("Mobile Insert")
                    } else if (DataLayerConstant.DATA_TASK_WEAR_DELETE_PATH.equals(event.dataItem.uri.path)) {
                        append("Wear Delete")
                    } else if (DataLayerConstant.DATA_TASK_MOBILE_DELETE_PATH.equals(event.dataItem.uri.path)) {
                        append("Mobile Delete")
                    } else if (DataLayerConstant.DATA_TASK_WEAR_UPDATE_PATH.equals(event.dataItem.uri.path)) {
                        append("Wear Update")
                    } else if (DataLayerConstant.DATA_TASK_MOBILE_UPDATE_PATH.equals(event.dataItem.uri.path)) {
                        append("Mobile Update")
                    }
                }
                DataEvent.TYPE_DELETED -> {

                    if (DataLayerConstant.WAKE_UP_PATH.equals(event.dataItem.uri.path)) {
                        append("Delete Wake up")
                    } else if (DataLayerConstant.TASK_PATH.equals(event.dataItem.uri.path)) {
                        append("Delete Task")
                    } else if (DataLayerConstant.SLEEP_PATH.equals(event.dataItem.uri.path)){
                        append("Delete Sleep")
                    }
                }
                else -> {

                }
            }
        }

    }
    fun append(string:String){
        Log.d("$tag Data Layer Network","DataNetwork: ${calendar.get(Calendar.HOUR)}:${calendar.get(Calendar.MINUTE)}:${calendar.get(Calendar.MILLISECOND)} $string")

    }
}
