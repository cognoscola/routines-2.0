package com.gorillamoa.routines.adapter

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gorillamoa.routines.R
import com.gorillamoa.routines.data.Task
import com.gorillamoa.routines.extensions.getCompletedTaskList
import com.gorillamoa.routines.extensions.getDayTaskList
import java.util.*

//TODO Comment this shit
class TaskListAdapter(
        context: Context,
        private val callback:(Int)->Unit,
        private val statusCallback:(Int,Boolean)->Any?): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

     var tasks:List<Task>? = null
        set(value) {
            field =value
            notifyDataSetChanged()
        }

    private val done:ArrayDeque<Int> =context.getCompletedTaskList()
    private val remaining:ArrayDeque<Int> = context.getDayTaskList()

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

                holder.apply {

                    val task =  tasks!![position - 1]
                    taskTextView.text = task.name

                    if (done.contains(task.id)) {
                        changeToDone(iconImageView,taskTextView)
                        iconImageView.setOnClickListener {

                            if (remaining.removeFirstOccurrence(task.id)) {
                                done.add(task.id)
                            }
                            statusCallback.invoke(task.id?:-1, false)
                            notifyItemChanged(position)
                        }

                    }else if (remaining.contains(task.id)){
                        changeToUndone(iconImageView,taskTextView)
                        iconImageView.setOnClickListener {

                            if (done.removeFirstOccurrence(task.id)) {
                                remaining.add(task.id)
                            }
                            statusCallback.invoke(task.id?:-1, true)
                            notifyItemChanged(position)
                        }

                    }else{
                        //we don't know if this task is done or not!
                        changeToUnknown(iconImageView,taskTextView)
                    }

                    taskTextView.setOnClickListener {
                        callback.invoke(task.id?:-1)
                    }
                }
            }
        }
    }

    private fun changeToUnknown(iv:ImageView,tv:TextView){
        iv.setImageResource(R.drawable.ic_priority_high_black_24dp)
    }

    private fun changeToDone(iv:ImageView, tv:TextView){
        iv.setImageResource(R.drawable.ic_done_black_24dp)
        tv.paintFlags = tv.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    }

    private fun changeToUndone(iv:ImageView, tv:TextView){
        iv.setImageResource(R.drawable.ic_radio_button_unchecked_black_24dp)
        tv.paintFlags = tv.paintFlags xor Paint.STRIKE_THRU_TEXT_FLAG
    }


    override fun getItemViewType(position: Int): Int {
        return if(position == 0 ) VIEW_TYPE_TITLE else VIEW_TYPE_TASK
    }

    inner class TaskItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
         var taskTextView: TextView = itemView.findViewById(R.id.taskNameTextView)
        var iconImageView = itemView.findViewById<ImageView>(R.id.iconTextView)
    }

    inner class TitleViewHolder(item:View):RecyclerView.ViewHolder(item){

        var headerTextView = item.findViewById<TextView>(R.id.headerTextView)

    }
}