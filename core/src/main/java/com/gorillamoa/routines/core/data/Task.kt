package com.gorillamoa.routines.core.data


import androidx.room.Entity
import androidx.room.PrimaryKey

const val MAX_NOTIFICATION_LINE_LENGTH = 23
const val MAX_NOTIFICATION_HISTORY_LINE_LENGTH = 26


@Entity(tableName = "Task")
data class Task(

        @PrimaryKey(autoGenerate = true)
        var id:Long? = null,

        val type: TaskType = TaskType.TYPE_TASK,
        val name:String,
        val description:String? = null,
        val frequency:Float = 1.0f, // pulse/range
        val date:Long = 0L,
        val range:Long = 0L,//how often
        val pulse:Long = 0L //how many times

        //TODO add addittional options
        //TODO add history


) {
        fun toPrettyString(): String {
                return "Task: ID${id}, Name:${name}, description:${description}, type:${type}"
        }

        //TODO this will part of the TASK class
        fun stringifyTasks(tasks:List<Task>?,builder:StringBuilder):String{

                 tasks?.let {
                     if(tasks.isEmpty()){
                         builder.addTaskLine("There are no tasks","yea")
                     } else{
                         tasks.forEach {
                             builder.addTaskLine(it.name,"${it.id}")
                         }
                     }
                     return toString()
                 }

                 builder.addTaskLine("There are no tasks","yea")
                 return toString()
                return toString()
        }

     companion object{

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
     }
}

/*
*
* //TODO this will be part of the task class
fun StringBuilder.stringifyHistory(tasks:Any?):String{
//fun StringBuilder.stringifyHistory(tasks:List<TaskHistory>?):String{

/*
    tasks?.let{

        //TODO limit the amount of lines
        val completionTime = Calendar.getInstance()
        completionTime.timeInMillis = it.timeCompleted.time
        addHistoryLine("${completionTime.get(Calendar.DAY_OF_MONTH)}/" +
                "${completionTime.get(Calendar.MONTH)}/" +
                "${completionTime.get(Calendar.YEAR)}",
                "${completionTime.get(Calendar.HOUR)}:"+
                "${completionTime.get(Calendar.MINUTE)}:"+
                "${completionTime.get(Calendar.SECOND)}",
                it.completed)

        addTotalLine(1,1 )

    }?:run{
        addTaskLine("There are no task histories","yea")
    }*/

    return toString()
}

fun StringBuilder.addTotalLine(completed:Int, total:Int){

    append("Total Points Accumulated: 1")

}

fun StringBuilder.addHistoryLine(historyDate:String, historyTime:String, completed:Boolean){

    if(historyDate.isEmpty() or historyDate.isBlank()){
        return
    }

    append(historyDate)

    if(completed){
        append(" At ${historyTime}")
    }
    else{
        append(" Skipped")
    }

    append("<br>")

}*/
