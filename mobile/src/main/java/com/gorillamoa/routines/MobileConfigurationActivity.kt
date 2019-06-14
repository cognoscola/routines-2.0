package com.gorillamoa.routines

import android.os.Bundle
import android.widget.RemoteViews
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import com.gorillamoa.routines.core.extensions.*
import com.gorillamoa.routines.core.receiver.NotificationActionReceiver.Companion.ACTION_START_DAY
import com.gorillamoa.routines.core.scheduler.Functions
import com.gorillamoa.routines.core.scheduler.TaskScheduler
import com.gorillamoa.routines.core.scheduler.assignFunction
import com.gorillamoa.routines.core.viewmodels.TaskViewModel
import kotlinx.android.synthetic.main.activity_routine_runner.*

class MobileConfigurationActivity : FragmentActivity() {

    lateinit var taskViewModel: TaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_routine_runner)

        taskViewModel = connectAndLoadViewModel()
        taskViewModel.tasks.observe(this, Observer {

            notificationShowWakeUp(
                    StringBuilder().stringifyTasks(it),
                    createNotificationMainIntentForWakeup(MobileConfigurationActivity::class.java.canonicalName!!),
                    smallRemoteView = getRemoteView())
        })

        notification_show.setOnClickListener {
            TaskScheduler.schedule(this) {
                notificationShowWakeUp(
                        it,
                        null,
                        null,
                        false,
                        getRemoteView(),
                        getLargeRemoteView(it))

            }
        }


        notification_hide.assignFunction(Functions.dismissWakeUpNotificationFunction())
        dummy.setOnClickListener { taskViewModel.dummy() }
        clear.setOnClickListener { Functions.clearTasks(this, taskViewModel) }

        extra.setOnClickListener { notificationShowSleep() }
    }

    fun getRemoteView(): RemoteViews {

        val remoteViews = RemoteViews(packageName, R.layout.remote_wakeup)
        remoteViews.setTextViewText(R.id.title, getHtml(getString(R.string.wake_up_title)))
        remoteViews.setTextViewText(R.id.description, getHtml(getString(R.string.wake_up_description)))
        setStartFunction(remoteViews)
        return remoteViews
    }

    fun getLargeRemoteView(bigStringContent: String): RemoteViews {
        val remoteViews = RemoteViews(packageName, R.layout.remote_wakeup_large)
        remoteViews.setTextViewText(R.id.title, getHtml(getString(R.string.wake_up_large_title)))
        remoteViews.setTextViewText(R.id.bigContent, getHtml(bigStringContent))
        setStartFunction(remoteViews)
        return remoteViews
    }

    fun setStartFunction(remoteViews: RemoteViews) {

        remoteViews.setOnClickPendingIntent(R.id.start, createNotificationActionPendingIntent(-1, ACTION_START_DAY))

    }
}
