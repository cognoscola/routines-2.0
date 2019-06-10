package com.gorillamoa.routines

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import com.gorillamoa.routines.core.extensions.*
import com.gorillamoa.routines.core.viewmodels.TaskViewModel
import kotlinx.android.synthetic.main.activity_routine_runner.*


//TODO fetch functions from a STATIC class
class MobileConfigurationActivity : FragmentActivity() {

    lateinit var taskViewModel:TaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_routine_runner)

        taskViewModel = connectAndLoadViewModel()
        taskViewModel.tasks.observe(this, Observer {

            notificationShowWakeUp(
                    StringBuilder().stringifyTasks(it),
                    createNotificationMainIntentForWakeup(MobileConfigurationActivity::class.java.canonicalName!!))

        })

        notification_show?.setOnClickListener {



        }

        notification_hide?.setOnClickListener {



        }
    }
}
