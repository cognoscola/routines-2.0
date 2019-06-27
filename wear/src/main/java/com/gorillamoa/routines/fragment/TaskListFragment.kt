package com.gorillamoa.routines.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.wear.widget.WearableLinearLayoutManager
import com.gorillamoa.routines.R
import com.gorillamoa.routines.adapter.DrawerAdapter
import com.gorillamoa.routines.adapter.TaskListAdapter
import com.gorillamoa.routines.core.data.Task
import com.gorillamoa.routines.core.extensions.getCompletedTaskList
import com.gorillamoa.routines.core.extensions.getDayTaskList
import com.gorillamoa.routines.core.views.TaskListDisplayer
import kotlinx.android.synthetic.main.fragment_task_list.*

/**
* Displays the List of tasks to the user
*/
class TaskListFragment:Fragment(), TaskListDisplayer {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        taskListWearableRecyclerView.adapter = TaskListAdapter()
        taskListWearableRecyclerView.isEdgeItemsCenteringEnabled = true
        taskListWearableRecyclerView.layoutManager = WearableLinearLayoutManager(context)

        top_navigation_drawer.apply {

            setAdapter(DrawerAdapter(context))
            addOnItemSelectedListener { position ->

                when (position) {
                    0 -> (taskListWearableRecyclerView?.adapter as TaskListAdapter).setDailyMode()
                    1 -> (taskListWearableRecyclerView?.adapter as TaskListAdapter).setAllMode()
                }
                Log.d("NavigationDrawer", "Clicked Position: $position")
            }
            controller.peekDrawer()
        }
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_task_list, container, false)
    }


    override fun notifyNewTaskCreated(task: Task) {

        taskListWearableRecyclerView?.apply {
            scrollToPosition(adapter!!.itemCount - 1)
        }

    }

    override fun onListUpdated(list: List<Task>) {
        (taskListWearableRecyclerView?.adapter as TaskListAdapter).apply {

            context?.apply {
                setTaskData(
                        list,
                        getDayTaskList(),
                        getCompletedTaskList()
                )
            }
        }
    }

    override fun notifyTaskListsChanged() {

        (taskListWearableRecyclerView?.adapter as TaskListAdapter).updateRemainingList(context!!.getDayTaskList())
        (taskListWearableRecyclerView?.adapter as TaskListAdapter).updateFinishedList(context!!.getCompletedTaskList())
    }

    override fun attachCallbackFunctions(
            itemClickedCallback:((Long)->Unit)?,
            completionCallback:((Long, Boolean)->Any?)?,
            scheduledCallback:((Long,Boolean)->Any?)?,
            addButtonCallback:((Boolean)->Any?)?
    ) {
        (taskListWearableRecyclerView?.adapter as TaskListAdapter).attachCallbackFunctions(
                itemClickedCallback,
                completionCallback,
                scheduledCallback,
                addButtonCallback
        )
    }

}