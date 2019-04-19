package com.gorillamoa.routines.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.wear.ambient.AmbientModeSupport
import androidx.wear.widget.WearableLinearLayoutManager
import com.gorillamoa.routines.R
import com.gorillamoa.routines.adapter.DrawerAdapter
import com.gorillamoa.routines.adapter.TaskListAdapter
import com.gorillamoa.routines.extensions.*
import com.gorillamoa.routines.scheduler.TaskScheduler
import com.gorillamoa.routines.viewmodel.TaskViewModel
import kotlinx.android.synthetic.main.activity_task_list.*

//TODO the listview doesn't stretch out to the end and start edges of the activity. Make it so.
//TODO handle use case where user interacts with notification, while on this app. One option is to remove the notification
//TODO create a TASK EMPTY VIEW


class TaskListActivity : FragmentActivity(), AmbientModeSupport.AmbientCallbackProvider {

    private lateinit var taskViewModel: TaskViewModel

    private val preferenceListener= SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->

        if (key == getTaskListKey()) {
            (taskListWearableRecyclerView?.adapter as TaskListAdapter).updateRemainingList(getDayTaskList())
        }

        if (key == getTaskFinishedKey()) {
            (taskListWearableRecyclerView?.adapter as TaskListAdapter).updateFinishedList(getCompletedTaskList())
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        mAmbientController = AmbientModeSupport.attach(this@TaskListActivity)
        taskViewModel = ViewModelProviders.of(this@TaskListActivity).get(TaskViewModel::class.java)
        taskViewModel.loadTasks()
        taskViewModel.tasks.observe(this, Observer {

            //TODO fetch only scheduled tasks!!
            (taskListWearableRecyclerView?.adapter as TaskListAdapter).setTaskData(
                    it,
                    getDayTaskList(),
                    getCompletedTaskList()
            )
        })

        taskListWearableRecyclerView?.apply {
            isEdgeItemsCenteringEnabled = true
            adapter = TaskListAdapter(

                    itemClickedCallback = {

                        startActivity(Intent(this@TaskListActivity, TaskViewActivity::class.java))
                    },
                    completionCallback = { tid, isDone ->

                        //TODO REMOVE THE NOTIFICATION IF IT EXISTS
                        if (isDone) {
                            TaskScheduler.completeTask(context, tid)
                        } else {
                            TaskScheduler.uncompleteTask(context, tid)
                        }
                    },
                    scheduledCallback = { id, isScheduled ->

                        if (isScheduled) {
                            TaskScheduler.scheduleTask(this@TaskListActivity,id)
                        }else{
                            TaskScheduler.unscheduleTask(this@TaskListActivity,id)
                        }
                    },

                    addButtonCallback = { isExisting -> //pick a task from existing tasks

                        //add button call back
                        if (isExisting) {
                            (adapter as TaskListAdapter).setPickerMode()
                        }else{
                            startActivity(Intent(this@TaskListActivity, TaskAddActivity::class.java))
                        }
                    }
            )
            layoutManager = WearableLinearLayoutManager(this@TaskListActivity)


            //TODO make the navigation drawer open as the user finishes scrolling to the top
        }

        top_navigation_drawer.apply {

            setAdapter(DrawerAdapter(this@TaskListActivity))
            addOnItemSelectedListener { position ->

                when(position){
                    0 -> (taskListWearableRecyclerView?.adapter as TaskListAdapter).setDailyMode()
                    1 -> (taskListWearableRecyclerView?.adapter as TaskListAdapter).setAllMode()
                }
                Log.d("NavigationDrawer","Clicked Position: $position")
            }
            controller.peekDrawer()
        }


        getLocalSettings().registerOnSharedPreferenceChangeListener(preferenceListener)
        // Enables Always-on
        //setAmbientEnabled()
    }

    override fun onDestroy() {
        getLocalSettings().unregisterOnSharedPreferenceChangeListener(preferenceListener)
        super.onDestroy()
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
