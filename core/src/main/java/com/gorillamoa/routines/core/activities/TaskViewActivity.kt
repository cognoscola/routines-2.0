package com.gorillamoa.routines.core.activities

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.gorillamoa.routines.core.R
import com.gorillamoa.routines.core.extensions.getTaskViewFragment

class TaskViewActivity:FragmentActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_view)

         supportFragmentManager.beginTransaction()
                          .add(R.id.fragmentContainerFrameLayout, getTaskViewFragment())
                          .commit()
    }

}