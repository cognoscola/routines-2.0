package com.gorillamoa.routines

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.gorillamoa.routines.config.alarm.AlarmConfigFragment

class BaseConfigActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.base_config_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, AlarmConfigFragment.newInstance())
                    .commitNow()
        }
    }

}
