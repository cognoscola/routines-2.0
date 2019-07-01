package com.gorillamoa.routines.onboard.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gorillamoa.routines.onboard.R
import kotlinx.android.synthetic.main.activity_onboard_configuration.*

class OnboardConfigurationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboard_configuration)

        showBeginingButton.setOnClickListener {
            val intent = Intent(this, OnboardActivity::class.java)
            startActivity(intent)

        }
    }
}
