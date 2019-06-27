
package com.gorillamoa.routines.core.extensions

import com.gorillamoa.routines.core.data.Task
import com.gorillamoa.routines.core.data.TaskHistory
import java.util.*


const val MAX_NOTIFICATION_LINE_LENGTH = 23
fun StringBuilder.stringifyTasks(tasks:List<Task>?):String{

    tasks?.let {
        if(tasks.isEmpty()){
            addTaskLine("There are no tasks","yea")
        } else{
            tasks.forEach {
                addTaskLine(it.name,"${it.id}")
            }
        }
        return toString()
    }

    addTaskLine("There are no tasks","yea")
    return toString()
}

fun StringBuilder.stringifyHistory(tasks:TaskHistory?):String{
//fun StringBuilder.stringifyHistory(tasks:List<TaskHistory>?):String{


    tasks?.let{
        val completionTime = Calendar.getInstance()
        completionTime.timeInMillis = it.timeCompleted.time
        addTaskLine("Date: ${completionTime.get(Calendar.DAY_OF_MONTH)}/" +
                "${completionTime.get(Calendar.MONTH)}/" +
                "${completionTime.get(Calendar.YEAR)} - " +
                "${completionTime.get(Calendar.HOUR)}:" +
                "${completionTime.get(Calendar.MINUTE)}:"+
                "${completionTime.get(Calendar.SECOND)}" +
                "Done: ${it.completed}",
                "")
    }

/*
    tasks?.let {
        if(tasks.isEmpty()){
            addTaskLine("There are no task history for this task!","yea")
        } else{
            tasks.forEach {
                val completionTime = Calendar.getInstance()
                completionTime.timeInMillis = it.timeCompleted.time
                addTaskLine("Date: ${completionTime.get(Calendar.DAY_OF_MONTH)}/" +
                        "${completionTime.get(Calendar.MONTH)}/" +
                        "${completionTime.get(Calendar.YEAR)} - " +
                        "${completionTime.get(Calendar.HOUR)}:" +
                        "${completionTime.get(Calendar.MINUTE)}:"+
                        "${completionTime.get(Calendar.SECOND)}" +
                        "Done: ${it.completed}",
                        "")
            }
        }
        return toString()
    }
*/

    addTaskLine("There are no task histories","yea")
    return toString()
}



/**
 * Adds an extra line to the text that will be displayed in the notification
 * We're going to build a line such that it only has 25 characters.
 * @param task the showing detail, it can a time length or time specfied or task detail
 * @param extra is additional details to show to the user
 */
fun StringBuilder.addTaskLine(task:String,extra:String){
    //detail should only be 5 chars max

    //eg. Meditate
    if (task.isEmpty() or task.isBlank()) {
        return
    }

    //TODO predict relevant emoji for each task
    append("&#9999;&nbsp;")
    if (task.length > MAX_NOTIFICATION_LINE_LENGTH) {

        append(task,0,(MAX_NOTIFICATION_LINE_LENGTH - 3 - Math.min(extra.length,5)))
        append("...")

    }else{
        append(task)
        for (i in 0..(MAX_NOTIFICATION_LINE_LENGTH - task.length - Math.min(extra.length,5))) {
            append("&nbsp")
        }
    }

    append("<br>")

}

/**
 * Add a line that show to the user how many extra tasks to show
 * @param remaining the number of remaining Tasks to show
 */
fun StringBuilder.buildEndLine(remaining:Int) {

    append("<i>")
    if (remaining < 10) {
        append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;")
        append("+$remaining more")
        append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;")
    } else if (remaining < 100) {
        append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;")
        append("+$remaining more")
    } else if (remaining < 1000) {
        append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;")
        append("+$remaining more")
    }
    append("</i>")
}