package com.gorillamoa.routines.activity

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.gorillamoa.routines.R
import com.gorillamoa.routines.data.TaskType
import com.gorillamoa.routines.fragment.NamePickerFragment
import com.gorillamoa.routines.fragment.TypePickerFragment

class TaskAddActivity : FragmentActivity() {

    //TODO if its the first time opening this, explain show a fragment explaining what the symbols mean

    private enum class State{
        Type,
        Name,
        Frequency
    }

    lateinit var type: TaskType
    lateinit var name:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_add)

        supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainerFrameLayout,TypePickerFragment().apply {

                    callback = { it ->

                        type = it
                        when (it) {
                            TaskType.TYPE_GOAL -> {

                                //name
                                //Achieve by: a date
                                //How often would you like to pick
                                showNamePickFragment()
                            }
                            TaskType.TYPE_HABIT -> {

                                //name
                                //How often would you like to perform
                                showNamePickFragment()

                            }
                            TaskType.TYPE_UNKNOWN -> {

                                //name
                                //When should this happen?
                                showNamePickFragment()
                            }
                        }
                    }
                })
                .commit()
    }

    private fun showNamePickFragment(){

        supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerFrameLayout,NamePickerFragment.newInstance {
                    name = it
                    Log.d("showNamePickFragment","Name is: $it")

                } )
                .commit()

    }

}
