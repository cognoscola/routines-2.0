package com.gorillamoa.routines

import android.app.Activity
import com.gorillamoa.routines.onboard.activities.OnboardActivity

import android.os.Bundle
import android.content.Intent
import android.util.Log
import kotlinx.android.synthetic.main.config_layout.*


class ConfigurationActivity : Activity() {
    @Suppress("unused")
    private val tag:String = ConfigurationActivity::class.java.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.config_layout)

        onBoardStart?.setOnClickListener {
            Log.d("$tag onCreate", OnboardActivity::class.qualifiedName)
            var intent: Intent? = null
            try {
                intent = Intent(this,
                        Class.forName("com.gorillamoa.routines.onboard.activities.OnboardActivity"))
                startActivity(intent)
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }
        }


    }
}
