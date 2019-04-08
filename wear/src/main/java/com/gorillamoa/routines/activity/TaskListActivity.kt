package com.gorillamoa.routines.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.wear.ambient.AmbientModeSupport
import androidx.wear.widget.WearableLinearLayoutManager
import androidx.wear.widget.drawer.WearableNavigationDrawerView
import com.gorillamoa.routines.R
import com.gorillamoa.routines.adapter.DrawerAdapter
import com.gorillamoa.routines.adapter.TaskListAdapter
import com.gorillamoa.routines.extensions.getCompletedTaskList
import com.gorillamoa.routines.extensions.getDayTaskList
import com.gorillamoa.routines.scheduler.TaskScheduler
import com.gorillamoa.routines.viewmodel.TaskViewModel
import kotlinx.android.synthetic.main.activity_task_list.*
import java.util.*

//TODO the listview doesn't stretch out to the end and start edges of the activity. Make it so.
//TODO handle use case where user interacts with notification, while on this app. One option is to remove the notification

//TODO create a TASK EMPTY VIEW


class TaskListActivity : FragmentActivity(), AmbientModeSupport.AmbientCallbackProvider {

    private lateinit var taskViewModel: TaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        mAmbientController = AmbientModeSupport.attach(this@TaskListActivity)
        taskViewModel = ViewModelProviders.of(this@TaskListActivity).get(TaskViewModel::class.java)

        val unfinished = getDayTaskList()
        val finished = getCompletedTaskList()
        val combined = ArrayDeque<Int>()

        if(unfinished.isNotEmpty())unfinished.forEach { combined.add(it) }
        if(finished.isNotEmpty())finished.forEach { combined.add(it) }

        taskViewModel.loadTasks(combined)
        taskViewModel.tasks.observe(this, Observer {

            //TODO fetch only scheduled tasks!!
            (taskListWearableRecyclerView?.adapter as TaskListAdapter).setTaskData(
                    it,
                    unfinished,
                    finished
            )
        })

        taskListWearableRecyclerView?.apply {
            isEdgeItemsCenteringEnabled = true
            adapter = TaskListAdapter({

                Log.d("onCreate","Clicked task $it")
                startActivity(Intent(this@TaskListActivity,TaskViewActivity::class.java))
            },{tid, isDone ->

                //TODO REMOVE THE NOTIFICATION IF IT EXISTS
                if (isDone) {
                    TaskScheduler.completeTask(context,tid)
                }else{
                    TaskScheduler.uncompleteTask(context,tid)
                }
            }){

                //add button call back
                startActivity(Intent(this@TaskListActivity,TaskAddActivity::class.java))


            }
            layoutManager = WearableLinearLayoutManager(this@TaskListActivity)



           //TODO make the navigation drawer open as the user finishes scrolling to the top

        }

        top_navigation_drawer.apply {

            setAdapter(DrawerAdapter(this@TaskListActivity))
            addOnItemSelectedListener { position ->

                Log.d("NavigationDrawer","Clicked Position: $position")
            }
            controller.peekDrawer()
        }

        // Enables Always-on
        //setAmbientEnabled()
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
