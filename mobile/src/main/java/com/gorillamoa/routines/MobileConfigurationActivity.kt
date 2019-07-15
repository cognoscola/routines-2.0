package com.gorillamoa.routines

import android.os.Bundle
import android.util.Log

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import com.google.android.gms.wearable.*

import com.gorillamoa.routines.core.constants.DataLayerConstant
import com.gorillamoa.routines.core.data.Task
import com.gorillamoa.routines.core.extensions.*

import com.gorillamoa.routines.core.scheduler.TaskScheduler
import com.gorillamoa.routines.core.viewmodels.TaskViewModel
import kotlinx.android.synthetic.main.activity_routine_runner.*
import java.util.*


class MobileConfigurationActivity : FragmentActivity(),
        DataClient.OnDataChangedListener{
    @Suppress("unused")
    private val tag:String = MobileConfigurationActivity::class.java.name

    lateinit var taskViewModel: TaskViewModel

    /**
     * Data Layer stuff
     */

    lateinit var  dataClient:DataClient
    lateinit var dataMap: DataMapItem

    val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_routine_runner)

        /**
        * Event Buttons
        * */
        wakeUpEventButton?.setOnClickListener { view ->
            broadcastShowWakeUp()
        }

        taskViewModel = connectAndLoadViewModel()
        taskViewModel.tasks.observe(this, Observer {

            /*if (it.isNotEmpty()) {
                Log.d("notificationRoutine","onCreate Observer")

            }else{
                notificationShowWakeUpMirror(TaskScheduler.generateEmptyVisibleList())
            }*/

            TaskScheduler.schedule(this){ list->
                list?.let {

                    notificationShowWakeUpMirror(list)
                }
            }
        })

        notification_show.setOnClickListener {

            taskViewModel.loadTasks()
//            notificationShowWakeUpMirror(taskViewModel.tasks.value!!)
        }

        notification_hide?.setOnClickListener { view ->

        }

        dummy.setOnClickListener { taskViewModel.dummy() }
        clear.setOnClickListener {
            taskViewModel.clearReturnList()
            clearSavedArrays()
        }

        extra.setOnClickListener { notificationShowSleep() }

        configureDataLayer()
        sendDataButton?.setOnClickListener { view ->

            //TODO Check if task completed etc...
        }

        createTask.setOnClickListener {

            val cal = Calendar.getInstance()
            cal.timeInMillis = System.currentTimeMillis()
            getDataRepository().insertMirror(this, com.gorillamoa.routines.core.data.Task(name = "Mobile Task:${cal.get(Calendar.HOUR)}:${cal.get(Calendar.MINUTE)}:${cal.get(Calendar.SECOND)}"))
        }

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

    fun configureDataLayer(){

        /*Wearable.WearableOptions.Builder().setLooper(myLooper).build().let { options ->
            Wearable.getDataClient(this, options)
        }*/

    }

    override fun onResume() {
        super.onResume()
        dataClient = Wearable.getDataClient(this)
        dataClient.addListener(this)
    }


    override fun onPause() {
        super.onPause()
        Wearable.getDataClient(this).removeListener(this)
    }

    override fun onDataChanged(dataEventBuffer: DataEventBuffer) {

        Log.d("$tag onDataChanged", "$dataEventBuffer")

        for (event in dataEventBuffer) {

            Log.d("$tag onDataChanged", "Host: ${event.dataItem.uri.host}")

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
                        append("Progress ")
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
                    }else if (DataLayerConstant.SLEEP_PATH.equals(event.dataItem.uri.path)) {
                        append("Delete Sleep")
                    }
                }
                else -> {

                }
            }
        }
    }

    fun append(string:String){

        inDataTextview.text = "${inDataTextview.text} \n ${calendar.get(Calendar.HOUR)}:${calendar.get(Calendar.MINUTE)}:${calendar.get(Calendar.MILLISECOND)} $string"
    }
}
