package com.gorillamoa.routines.activity

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.wear.widget.BoxInsetLayout
import com.gorillamoa.routines.R
import com.gorillamoa.routines.data.TaskType
import com.gorillamoa.routines.fragment.FrequencyPickerFragment
import com.gorillamoa.routines.fragment.NamePickerFragment
import com.gorillamoa.routines.fragment.TypePickerFragment
import kotlinx.android.synthetic.main.activity_task_add.*

class TaskAddActivity : FragmentActivity() {

    @Suppress("unused")
    private val tag:String = TaskAddActivity::class.java.name

    //TODO if its the first time opening this, explain show a fragment explaining what the symbols mean

    private enum class State{
        Type,
        Name,
        Frequency
    }

    lateinit var type:TaskType
    lateinit var name:String
    var frequency:Float = 1.0f

    val editCallback:()->Any? = {

        Log.d("$tag ","Edit!")

    }

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
                                showNamePickFragment()
                                //Achieve by: a date

                                //How often would you like to pick
                            }
                            TaskType.TYPE_HABIT -> {

                                //name
                                showNamePickFragment()
                                //How often would you like to perform


                            }
                            TaskType.TYPE_UNKNOWN -> {

                                //name
                                showNamePickFragment()
                                //When should this happen?
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

                    when (type) {
                        TaskType.TYPE_GOAL -> {

                            //name
                            //Achieve by: a date

                            //How often would you like to pick
                            showFrequencyFragment()
                        }
                        TaskType.TYPE_HABIT -> {


                            //name
                            showFrequencyFragment()
                            //How often would you like to perform


                        }
                        TaskType.TYPE_UNKNOWN -> {

                            //TODO schedule the task immediately
                            //TODO close this window

                        }
                    }


                    //now we

                } )
                .commit()
    }

    private fun showFrequencyFragment(){

        (fragmentContainerFrameLayout?.layoutParams as BoxInsetLayout.LayoutParams).boxedEdges = BoxInsetLayout.LayoutParams.BOX_TOP
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerFrameLayout,FrequencyPickerFragment.newInstance( {
                    frequency = it
                    Log.d("$tag showFrequencyFragment","Frequency: $it")
                },editCallback)).commit()
    }
}
