package com.gorillamoa.routines.adapter

import android.graphics.Typeface
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gorillamoa.routines.R
import com.gorillamoa.routines.data.Task

class TaskListAdapter(private val callback:(Int)->Unit): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

     var tasks:List<Task>? = null
        set(value) {
            field =value
            notifyDataSetChanged()
        }


    val builder = StringBuilder()

    companion object {
        const val VIEW_TYPE_TASK = 0
        const val VIEW_TYPE_TITLE = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return  if(viewType == VIEW_TYPE_TASK)
            TaskItemHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false))
        else {
            TitleViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_list_header, parent, false))
        }
    }

    /**
     * get the number of items to display.
     * note that we show an extra for the title, hence the +1
     */
    override fun getItemCount(): Int {
        return (tasks?.size ?:0) + 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        tasks?.let {

            if (holder is TitleViewHolder) {
                holder.headerTextView.text = holder.headerTextView.context.getString(R.string.task_list_title)
                holder.headerTextView.setTypeface(holder.headerTextView.typeface, Typeface.BOLD)
            }

            if (holder is TaskItemHolder) {
                val task =  tasks!![position - 1]
                builder.clear()
                builder.append("&#9999;&nbsp;")
                builder.append(task.name)

                holder.tasKTextView.text = Html.fromHtml(builder.toString(),Html.FROM_HTML_MODE_COMPACT)
                holder.tasKTextView.setOnClickListener {
                    callback.invoke(task.id?:-1)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(position == 0 ) VIEW_TYPE_TITLE else VIEW_TYPE_TASK
    }


    inner class TaskItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
         var tasKTextView: TextView = itemView.findViewById(R.id.taskNameTextView)
    }

    inner class TitleViewHolder(item:View):RecyclerView.ViewHolder(item){
        var headerTextView = item.findViewById<TextView>(R.id.headerTextView)
    }
}