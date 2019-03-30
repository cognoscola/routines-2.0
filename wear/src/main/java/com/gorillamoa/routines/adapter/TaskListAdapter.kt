package com.gorillamoa.routines.adapter

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gorillamoa.routines.R
import com.gorillamoa.routines.data.Task

class TaskListAdapter(private val callback:(Int)->Unit): RecyclerView.Adapter<TaskListAdapter.TaskItemHolder>() {

     var tasks:List<Task>? = null
        set(value) {
            field =value
            notifyDataSetChanged()
        }


    val builder = StringBuilder()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskItemHolder {
        return TaskItemHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false))
    }

    override fun getItemCount(): Int {
        return tasks?.size ?:0
    }

    override fun onBindViewHolder(holder: TaskItemHolder, position: Int) {
        tasks?.let {

            val task =  tasks!![position]
            builder.clear()
            builder.append("&#9999;&nbsp;")
            builder.append(task.name)

            holder.tasKTextView.text = Html.fromHtml(builder.toString(),Html.FROM_HTML_MODE_COMPACT)
            holder.tasKTextView.setOnClickListener {
                callback.invoke(task.id?:-1)
            }
        }
    }

    inner class TaskItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
         var tasKTextView: TextView = itemView.findViewById(R.id.taskNameTextView)
    }
}