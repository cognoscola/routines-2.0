package com.gorillamoa.routines

import android.content.Context
import android.os.Bundle
import android.util.Log

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import com.google.android.gms.wearable.*
import com.gorillamoa.routines.MobileNotificationBehaviourReceiver.Companion.ACTION_WAKEUP_EXPAND
import com.gorillamoa.routines.core.coroutines.Coroutines
import com.gorillamoa.routines.core.data.Task
import com.gorillamoa.routines.core.extensions.*

import com.gorillamoa.routines.core.scheduler.TaskScheduler
import com.gorillamoa.routines.core.viewmodels.TaskViewModel
import kotlinx.android.synthetic.main.activity_routine_runner.*
import java.util.*

const val EVENT_WAKEUP = "event.wakeup.visibility"

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

            if (it.isNotEmpty()) {
                Log.d("notificationRoutine","onCreate Observer")
                notificationShowWakeUpMirror(it)
            }else{
                notificationShowWakeUpMirror(TaskScheduler.generateEmptyVisibleList())
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
//        dataClient = Wearable.getDataClient(this)
//        dataClient.addListener(this)
    }


    override fun onPause() {
        super.onPause()
//        Wearable.getDataClient(this).removeListener(this)
    }

    override fun onDataChanged(dataEventBuffer: DataEventBuffer){

        Log.d("$tag onDataChanged","$dataEventBuffer")

        for (event in dataEventBuffer) {
            if (event.type == DataEvent.TYPE_CHANGED) {
                Log.d("$tag onDataChanged","Changed")
            } else if (event.type == DataEvent.TYPE_DELETED) {
                Log.d("$tag onDataChanged","Deleted")
                
            }
        }
    }


}
