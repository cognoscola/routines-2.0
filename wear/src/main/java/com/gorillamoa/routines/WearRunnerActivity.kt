package com.gorillamoa.routines

import android.os.Bundle
import android.support.wearable.activity.WearableActivity

class WearRunnerActivity : WearableActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wear_runner)



        // Enables Always-on
        setAmbientEnabled()





    }
}
