package com.gorillamoa.routines.activity

import android.app.Activity
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
import com.gorillamoa.routines.data.Task
import com.gorillamoa.routines.data.TaskType
import com.gorillamoa.routines.data.TypeConverters
import com.gorillamoa.routines.extensions.*
import com.gorillamoa.routines.scheduler.TaskScheduler
import com.gorillamoa.routines.viewmodel.TaskViewModel
import kotlinx.android.synthetic.main.activity_task_list.*
import java.lang.Exception

//TODO the listview doesn't stretch out to the end and start edges of the activity. Make it so.
//TODO handle use case where user interacts with notification, while on this app. One option is to remove the notification
//TODO create a TASK EMPTY VIEW


class TaskListActivity : FragmentActivity(), AmbientModeSupport.AmbientCallbackProvider {
    @Suppress("unused")
    private val tag:String = TaskListActivity::class.java.name

    private lateinit var taskViewModel: TaskViewModel
    private var isCreating = false

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
            (taskListWearableRecyclerView?.adapter as TaskListAdapter).apply {
                setTaskData(
                        it,
                        getDayTaskList(),
                        getCompletedTaskList()
                )
            }
            if (isCreating) {
                isCreating = false
                try {
                    taskListWearableRecyclerView?.scrollToPosition(it.size -1 )

                } catch (e: Exception) {
                    Log.d("$tag onCreate","Can't scroll for some reason",e)
                }
            }


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
                            startActivityForResult(
                                    Intent(this@TaskListActivity, TaskAddActivity::class.java),
                                    TaskAddActivity.REQUEST_CODE)
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == TaskAddActivity.REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                isCreating = true
                data?.apply {
                    val type:TaskType = TypeConverters().IntToType(getIntExtra("type", 0))!!
                    taskViewModel.insertAndReturnList(
                            Task(
                                    name = getStringExtra("name"),
                                    type = type,
                                    description = "",
                                    frequency = getFloatExtra("frequency",1.0f),
                                    date = getLongExtra("date",0L)
                            )
                    )
                }
            }
        }
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
