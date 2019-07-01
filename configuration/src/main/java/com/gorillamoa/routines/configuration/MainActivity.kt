package com.gorillamoa.routines.configuration

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.util.Log
import com.gorillamoa.routines.onboard.activities.OnboardActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    @Suppress("unused")
    private val tag:String = MainActivity::class.java.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        onBoardStart?.setOnClickListener {
            Log.d("$tag onCreate",OnboardActivity::class.qualifiedName)
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
