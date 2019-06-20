package com.gorillamoa.routines.core.scheduler

import android.content.Context
import android.view.View
import com.gorillamoa.routines.core.extensions.clearSavedArrays
import com.gorillamoa.routines.core.extensions.notificationDissmissWakeUp
import com.gorillamoa.routines.core.extensions.notificationShowWakeUp
import com.gorillamoa.routines.core.viewmodels.TaskViewModel



fun View.assignFunction(T:(Context)->Any?){

    setOnClickListener {
        T.invoke(it.context)
    }
}

class Functions {

    companion object {

        fun showWakeUpNotificationFunction(): (Context) -> Any {
            return { context: Context ->
                TaskScheduler.schedule(context) {
                    context.notificationShowWakeUp(
                            it,
                            null,
                            null,
                            false)

                }
            }
        }

        fun dismissWakeUpNotificationFunction(): (Context) -> Any {
            return { context: Context ->
                context.notificationDissmissWakeUp()


            }
        }

        /**
         * Keeping it simple
         */
        fun clearTasks(context: Context, taskViewModel: TaskViewModel) {

            taskViewModel.clearReturnList()
            context.clearSavedArrays()
        }

    }
}