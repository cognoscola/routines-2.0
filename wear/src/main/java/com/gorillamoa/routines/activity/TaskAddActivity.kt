package com.gorillamoa.routines.activity

import android.os.Bundle
import android.support.wearable.activity.WearableActivity

class TaskAddActivity : WearableActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_add)

        // Enables Always-on
        setAmbientEnabled()
    }
}
