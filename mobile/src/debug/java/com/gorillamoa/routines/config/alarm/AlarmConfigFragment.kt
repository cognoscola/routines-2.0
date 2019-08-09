package com.gorillamoa.routines.config.alarm

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.*
import androidx.fragment.app.Fragment
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

        view.findViewById<Button>(R.id.detailsButton).setOnClickListener {

            val activity = Class.forName("com.gorillamoa.routines.details.DetailsActivity")
            val newIntent  = Intent(context, activity).apply{
                putExtra("task_name","A test Task")
                putExtra("task_description", "This is a test quick task description")
                putExtra("task_pulse", 5L)
                putExtra("task_range", 7L)//week
                putExtra("task_type",  2)
            }

            startActivityForResult(newIntent,1000)
        }

        view.findViewById<Button>(R.id.forceQuit).setOnClickListener {

            activity!!.finish()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }


}
