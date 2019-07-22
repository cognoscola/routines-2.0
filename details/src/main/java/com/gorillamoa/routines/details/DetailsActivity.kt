package com.gorillamoa.routines.details

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_details.*

//TODO make title back transparent (invisible)
//TODO show a histoy graph
//TODO Add options to the spinners
class DetailsActivity : AppCompatActivity() {

    private var name:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        intent?.apply {

            name = getStringExtra("task_name")
        }


        nameTextView.text = name
    }
}
