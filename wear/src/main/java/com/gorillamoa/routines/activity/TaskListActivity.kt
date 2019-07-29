package com.gorillamoa.routines.activity

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.wear.ambient.AmbientModeSupport
import com.gorillamoa.routines.R
import com.gorillamoa.routines.core.activities.TaskViewActivity
import com.gorillamoa.routines.core.data.Task
import com.gorillamoa.routines.core.data.TaskType
import com.gorillamoa.routines.core.data.TypeConverters
import com.gorillamoa.routines.core.extensions.*

import com.gorillamoa.routines.core.viewmodels.TaskViewModel
import com.gorillamoa.routines.core.views.TaskListDisplayer

//TODO the listview doesn't stretch out to the end and start edges of the activity. Make it so.
//TODO handle use case where user interacts with notification, while on this app. One option is to remove the notification
//TODO create a TASK EMPTY VIEW

/**
 * This a generic activity for displaying the Tasks to the user. The actually layout is handled by
 * either the mobile or wear side independently.
 */
class TaskListActivity : FragmentActivity(), AmbientModeSupport.AmbientCallbackProvider {
    @Suppress("unused")
    private val tag:String = TaskListActivity::class.java.name

    private lateinit var taskViewModel: TaskViewModel
    private var isCreating = false

    var taskListFragment:TaskListDisplayer? = null

    val onItemClickedCallback:((Long)->Unit)? = {
        startActivity(Intent(this@TaskListActivity, TaskViewActivity::class.java))
    }

    val completionCallback:((Long, Boolean)->Any?)? = { tid, isDone ->
        //TODO REMOVE THE NOTIFICATION IF IT EXISTS
        if (isDone) {
            com.gorillamoa.routines.core.scheduler.TaskScheduler.completeTask(this, tid)
        } else {
            com.gorillamoa.routines.core.scheduler.TaskScheduler.uncompleteTask(this, tid)
        }
    }

    val scheduleCallback:((Long,Boolean)->Any?)? ={ id, isScheduled ->
        if (isScheduled) {
            com.gorillamoa.routines.core.scheduler.TaskScheduler.scheduleTask(this@TaskListActivity, id)
        } else {
            com.gorillamoa.routines.core.scheduler.TaskScheduler.unscheduleTask(this@TaskListActivity, id)
        }
    }

    private val addButtonCallback:((Boolean)->Any?)? = { isExisting ->
        //pick a task from existing tasks
        //add button call back
        if (!isExisting) {
            startActivityForResult(
                    Intent(this@TaskListActivity, TaskAddActivity::class.java),
                    TaskAddActivity.REQUEST_CODE)

        }
    }

    private val preferenceListener= SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->

        if ((key == getTaskListKey()).or(key == getTaskFinishedKey())) {
            (taskListFragment as TaskListDisplayer).notifyTaskListsChanged()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        taskListFragment = getTaskListFragment() as TaskListDisplayer

        supportFragmentManager.beginTransaction()
                          .add(R.id.fragmentContainerFrameLayout,taskListFragment as Fragment )
                          .commit()

        if (isWatch()) {
            mAmbientController = AmbientModeSupport.attach(this@TaskListActivity)
        }

        taskViewModel = ViewModelProviders.of(this@TaskListActivity).get(TaskViewModel::class.java)
        taskViewModel.loadTasks()
        //TODO fix this
        /*taskViewModel.tasks.observe(this, Observer {
            (taskListFragment as TaskListDisplayer).onListUpdated(it)
        })
*/

        getLocalSettings().registerOnSharedPreferenceChangeListener(preferenceListener)
        // Enables Always-on
        //setAmbientEnabled()
    }

    override fun onDestroy() {
        getLocalSettings().unregisterOnSharedPreferenceChangeListener(preferenceListener)
        super.onDestroy()
    }

    override fun onResumeFragments() {
        super.onResumeFragments()

        taskListFragment?.attachCallbackFunctions(onItemClickedCallback,completionCallback,scheduleCallback,addButtonCallback)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == TaskAddActivity.REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {


                data?.apply {

                    val type: TaskType = TypeConverters().IntToType(getIntExtra("type", 0))!!
                    val task =
                            Task(
                                    name = getStringExtra("name"),
                                    type = type,
                                    description = "",
                                    frequency = getFloatExtra("frequency",1.0f),
                                    date = getLongExtra("date",0L)
                            )

                    taskViewModel.insertAndReturnList(task)
                    (taskListFragment as TaskListDisplayer).notifyNewTaskCreated(task)
                }
            }
        }
    }


    private var mAmbientController: AmbientModeSupport.AmbientController? = null

    override fun getAmbientCallback(): AmbientModeSupport.AmbientCallback {
        return TaskListAmbient()
    }


}
