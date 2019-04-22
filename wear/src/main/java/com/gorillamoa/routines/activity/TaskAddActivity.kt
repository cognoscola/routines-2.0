package com.gorillamoa.routines.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.wear.widget.BoxInsetLayout
import com.gorillamoa.routines.R
import com.gorillamoa.routines.data.TaskType
import com.gorillamoa.routines.data.TypeConverters
import com.gorillamoa.routines.fragment.DatePickerFragment
import com.gorillamoa.routines.fragment.FrequencyPickerFragment
import com.gorillamoa.routines.fragment.NamePickerFragment
import com.gorillamoa.routines.fragment.TypePickerFragment
import kotlinx.android.synthetic.main.activity_task_add.*
import java.util.*

class TaskAddActivity : FragmentActivity() {

    @Suppress("unused")
    private val tag:String = TaskAddActivity::class.java.name

    //TODO if its the first time opening this, explain show a fragment explaining what the symbols mean

    lateinit var type:TaskType
    lateinit var name:String
    var frequency:Float = 1.0f
    private var isDateSet = false
    lateinit var date:Calendar


    val editCallback:()->Any? = {

        Log.d("$tag ","Edit!")

    }

    companion object {
        val REQUEST_CODE = 10
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_add)

        setResult(Activity.RESULT_CANCELED)

        supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainerFrameLayout,TypePickerFragment().apply {

                    callback = { it ->
                        type = it
                        showNamePickFragment()
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
                            //Achieve by: a date
                            showDatePickerFragment()
                        }
                        TaskType.TYPE_HABIT -> {


                            //name
                            showFrequencyFragment()
                            //How often would you like to perform


                        }
                        TaskType.TYPE_UNKNOWN -> {

                            addTaskAndClose()
                        }
                    }

                    //now we

                } )
                .commit()
    }

    private fun showFrequencyFragment(){

        (fragmentContainerFrameLayout?.layoutParams as BoxInsetLayout.LayoutParams).boxedEdges = BoxInsetLayout.LayoutParams.BOX_NONE
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerFrameLayout,FrequencyPickerFragment.newInstance( {
                    frequency = it
                    Log.d("$tag showFrequencyFragment","Frequency: $it")
                    addTaskAndClose()



                },editCallback)).commit()
    }

    private fun showDatePickerFragment(){

        (fragmentContainerFrameLayout?.layoutParams as BoxInsetLayout.LayoutParams).boxedEdges = BoxInsetLayout.LayoutParams.BOX_NONE
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerFrameLayout,DatePickerFragment.newInstance(){
                   isDateSet = true
                    date = it
                    Log.d("$tag showFrequencyFragment","Date: ${it.get(Calendar.MONTH)} ${it.get(Calendar.DAY_OF_MONTH)}")

                    when (type) {
                        TaskType.TYPE_GOAL -> {
                            //How often would you like to pick
                            showFrequencyFragment()
                        }
                        else->{}
                    }
                }).commit()
    }

    private fun addTaskAndClose(){


        setResult(Activity.RESULT_OK, Intent().apply {

            putExtra("name",name)
            putExtra("type",TypeConverters().TypeToInt(this@TaskAddActivity.type))
            putExtra("frequency",frequency)
            putExtra("date", if(isDateSet)this@TaskAddActivity.date.timeInMillis else 0L)

        })
        finish()
    }
}
