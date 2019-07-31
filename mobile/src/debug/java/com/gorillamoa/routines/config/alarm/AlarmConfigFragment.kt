package com.gorillamoa.routines.config.alarm

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.gorillamoa.routines.R
import com.gorillamoa.routines.core.extensions.createAlarmIntent
import com.gorillamoa.routines.core.extensions.createWakeUpAlarmPendingIntent
import com.gorillamoa.routines.core.receiver.AlarmReceiver

class AlarmConfigFragment : Fragment() {

    companion object {
        fun newInstance() = AlarmConfigFragment()
    }

    private lateinit var viewModel: AlarmConfigViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.alarm_config_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.sendWakeUpBroadcastButton).setOnClickListener {

            val wakeUpIntent = context!!.createAlarmIntent()
            wakeUpIntent.action = AlarmReceiver.EVENT_WAKEUP
            activity?.sendBroadcast(wakeUpIntent)
        }

        view.findViewById<Button>(R.id.forceQuit).setOnClickListener {

            activity!!.finish()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }

}
