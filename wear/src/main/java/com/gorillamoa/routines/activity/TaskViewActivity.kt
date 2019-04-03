package com.gorillamoa.routines.activity

import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import com.gorillamoa.routines.R

//TODO create pager for tasks
//TODO show some task history

class TaskViewActivity:WearableActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_view)
    }
}