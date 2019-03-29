package com.gorillamoa.routines.activity

import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import androidx.wear.widget.WearableLinearLayoutManager
import com.gorillamoa.routines.R
import com.gorillamoa.routines.adapter.TaskListAdapter
import kotlinx.android.synthetic.main.activity_task_list.*

class TaskListActivity : WearableActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        taskListWearableRecyclerView?.apply {
            isEdgeItemsCenteringEnabled = true
            adapter = TaskListAdapter(Array<String>(10){
                "Task $it"
            }){}
            layoutManager = WearableLinearLayoutManager(this@TaskListActivity)
        }

        // Enables Always-on
        setAmbientEnabled()
    }
}
