package com.gorillamoa.routines.activity

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.wear.ambient.AmbientModeSupport
import androidx.wear.widget.WearableLinearLayoutManager
import com.gorillamoa.routines.R
import com.gorillamoa.routines.adapter.TaskListAdapter
import com.gorillamoa.routines.viewmodel.TaskViewModel
import kotlinx.android.synthetic.main.activity_task_list.*

//TODO the listview doesn't stretch out to the end and start edges of the activity. Make it so.
//TODO add Header Item for the list!

class TaskListActivity : FragmentActivity(), AmbientModeSupport.AmbientCallbackProvider {


    private lateinit var taskViewModel: TaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        mAmbientController = AmbientModeSupport.attach(this@TaskListActivity)

        taskViewModel = ViewModelProviders.of(this@TaskListActivity).get(TaskViewModel::class.java)
        taskViewModel.loadTasks()
        taskViewModel.tasks.observe(this, Observer {

            (taskListWearableRecyclerView?.adapter as TaskListAdapter).tasks = it

        })

        taskListWearableRecyclerView?.apply {
            isEdgeItemsCenteringEnabled = true
            adapter = TaskListAdapter{

                Log.d("onCreate","Clicked task $it")
            }
            layoutManager = WearableLinearLayoutManager(this@TaskListActivity)
        }

        // Enables Always-on
     //   setAmbientEnabled()
    }

    private var mAmbientController: AmbientModeSupport.AmbientController? = null

    override fun getAmbientCallback(): AmbientModeSupport.AmbientCallback {
        return TaskListActivity.TaskListAmbient()
    }

    private class TaskListAmbient : AmbientModeSupport.AmbientCallback() {

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
