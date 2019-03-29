package com.gorillamoa.routines.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gorillamoa.routines.R


/**
 * Provides a binding from [NotificationCompat.Style] data set to views displayed within the
 * [WearableRecyclerView].
 */
class TaskListAdapter(val list: Array<String>, private val callback:()->Unit): RecyclerView.Adapter<TaskListAdapter.TaskItemHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskItemHolder {
        return TaskItemHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_task, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: TaskItemHolder, position: Int) {
        holder?.tastTextView?.text = list[position]
        holder?.tastTextView?.setOnClickListener {
            callback.invoke()
        }
    }

    inner class TaskItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        public var tastTextView: TextView = itemView.findViewById(R.id.taskNameTextView)
    }
}