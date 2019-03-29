package com.gorillamoa.routines.activity

import android.os.Bundle
import android.support.wearable.activity.WearableActivity

class TaskListActivity : WearableActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        // Enables Always-on
        setAmbientEnabled()
    }
}
