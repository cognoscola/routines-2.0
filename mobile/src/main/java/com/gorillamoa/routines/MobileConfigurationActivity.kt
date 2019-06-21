package com.gorillamoa.routines

import android.content.Context
import android.os.Bundle
import android.util.Log

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.*
import com.gorillamoa.routines.MobileNotificationBehaviourReceiver.Companion.ACTION_WAKEUP_EXPAND
import com.gorillamoa.routines.core.extensions.*

import com.gorillamoa.routines.core.scheduler.Functions
import com.gorillamoa.routines.core.scheduler.TaskScheduler
import com.gorillamoa.routines.core.viewmodels.TaskViewModel
import kotlinx.android.synthetic.main.activity_routine_runner.*

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

            notificationShowWakeUp(
                    StringBuilder().stringifyTasks(it),
                    createNotificationMainIntentForWakeup(MobileConfigurationActivity::class.java.canonicalName!!),
                    smallRemoteView = getWakeupRemoteView())
        })

        notification_show.setOnClickListener {


            TaskScheduler.schedule(this) {
                notificationShowWakeUp(
                        it,
                        null,
                        null,
                        false,
                        getWakeupRemoteView().createFunction(this,it,ACTION_WAKEUP_EXPAND),
                        null)

            }
        }

        notification_hide?.setOnClickListener { view ->

            notificationDissmissWakeUp()

            remoteNotifyWakeUpActioned(this)
        }

        dummy.setOnClickListener { taskViewModel.dummy() }
        clear.setOnClickListener { Functions.clearTasks(this, taskViewModel) }

        extra.setOnClickListener { notificationShowSleep() }


        configureDataLayer()
        sendDataButton?.setOnClickListener { view ->

            remoteWakeUp(this)
            //TODO Check if task completed etc...
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

    /**
     * Notify other nodes that an wake up event was triggered
     * @param context is any context
     */
    fun remoteWakeUp(context: Context){

        val putDataReq: PutDataRequest = PutDataMapRequest.create("/day").run {
            dataMap.putBoolean(EVENT_WAKEUP, true)
            asPutDataRequest()
        }
        val putDataTask: Task<DataItem> = Wearable.getDataClient(context).putDataItem(putDataReq)
    }

    /**
     * Notify other nodes that the wake up event was actioned. It will mean that
     * this node has to take an action (such as dismiss the wake up notification)
     * This is usually called when the user starts the day by clicking on the notification
     */
    fun remoteNotifyWakeUpActioned(context: Context){

        val putDataReq: PutDataRequest = PutDataMapRequest.create("/day").run {
            dataMap.putBoolean(EVENT_WAKEUP, false)
            asPutDataRequest()
        }
        val putDataTask: Task<DataItem> = Wearable.getDataClient(context).putDataItem(putDataReq)
    }
}
