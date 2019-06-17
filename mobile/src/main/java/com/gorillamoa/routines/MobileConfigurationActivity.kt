package com.gorillamoa.routines

import android.os.Bundle

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import com.gorillamoa.routines.MobileNotificationBehaviourReceiver.Companion.ACTION_WAKEUP_EXPAND
import com.gorillamoa.routines.core.extensions.*

import com.gorillamoa.routines.core.scheduler.Functions
import com.gorillamoa.routines.core.scheduler.TaskScheduler
import com.gorillamoa.routines.core.scheduler.assignFunction
import com.gorillamoa.routines.core.viewmodels.TaskViewModel
import kotlinx.android.synthetic.main.activity_routine_runner.*

class MobileConfigurationActivity : FragmentActivity() {

    lateinit var taskViewModel: TaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_routine_runner)

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


        notification_hide.assignFunction(Functions.dismissWakeUpNotificationFunction())
        dummy.setOnClickListener { taskViewModel.dummy() }
        clear.setOnClickListener { Functions.clearTasks(this, taskViewModel) }

        extra.setOnClickListener { notificationShowSleep() }
    }


}
