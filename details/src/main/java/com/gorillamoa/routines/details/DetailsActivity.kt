package com.gorillamoa.routines.details

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import kotlinx.android.synthetic.main.activity_details.*

//TODO make title back transparent (invisible)
//TODO show a histoy graph
//TODO Add options to the spinners
class DetailsActivity : AppCompatActivity() {

    private var name:String = ""
    private var task_type:Int = 0
    private var pulse:Long = 0
    private var range:Long = 0  //1 means 1 day or 24 hours
    private var description:String = ""

    //remember equation, which is frequency / dayRange . So.. Twice per day is 2
    //since 2/1 = 2 .
    //Or 4 times per day is 4/1  = 4
    //4 times per week is 4/7 = 0.57, so, every day, we add this value until we get over one.
    //when we schedule it, we subtract 1 and save the remainder.

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.details_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        if(item?.itemId == R.id.save){

            //TODO close app
            finish()

            //TODO save task to DB
        }


        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        intent?.apply {

            name = getStringExtra("task_name")
            task_type = getIntExtra("task_type",0)
            description = getStringExtra("task_description")
            if(description.isEmpty()) description = getString(R.string.task_default_description)

            pulse = getLongExtra("task_pulse",0)
            range = getLongExtra("task_range" , 0)

        }
        nameTextView.text = name
        descriptionTextView.text = description
        amountEditText.setText("$pulse")

        configureTypeSpinner()
        configureRangeSpinner()

    }

    private fun configureTypeSpinner(){

        val dropdown = findViewById<Spinner>(R.id.typeSpinner)
        val items = arrayOf("Task","Habit", "Goal")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
        dropdown.adapter =adapter

            dropdown.setSelection(task_type)

        dropdown.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                task_type = p2
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                dropdown.setSelection(task_type)
            }
        }
    }

    fun configureRangeSpinner(){

        val dropdown = findViewById<Spinner>(R.id.rangeSpinner)
        val items = arrayOf("Never","Day", "Week", "Month", "2-Month","6-Month","Year")

        //1, 7, 30, 60,  30*6, 30*12
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
        dropdown.setAdapter(adapter)

            dropdown.setSelection(when(range.toInt()){
                0 -> 0
                1 -> 1
                7 -> 2
                30 -> 3
                60 -> 4
                180 -> 5
                365 -> 6
                else -> 0
            })

        dropdown.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                range = when(p2){

                    0 -> 0L
                    1 -> 1L
                    2 -> 7L
                    3 -> 30L
                    4 -> 60L
                    5 -> 180L
                    6 -> 365L
                    else -> 0L
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                dropdown.setSelection(0)
            }
        }
    }
}
