package com.gorillamoa.routines.core.views

import com.gorillamoa.routines.core.data.Task


interface TaskListDisplayer {

    fun onListUpdated(list:List<Task>)
    fun notifyNewTaskCreated(task:Task)
    fun notifyTaskListsChanged()
    fun attachCallbackFunctions(itemClickedCallback:((Long)->Unit)? = null,
                                completionCallback:((Long, Boolean)->Any?)? =null,
                                scheduledCallback:((Long,Boolean)->Any?)? = null,
                                addButtonCallback:((Boolean)->Any?)? = null)

}