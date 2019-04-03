package com.gorillamoa.routines.adapter

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gorillamoa.routines.R
import com.gorillamoa.routines.data.Task
import java.util.*

//TODO Comment this shit
class TaskListAdapter(
        context: Context,
        private val callback:(Int)->Unit,
        private val statusCallback:(Int,Boolean)->Any?): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var tasks:List<Task>? = null
    private var done:ArrayDeque<Int>? = null
    private var remaining:ArrayDeque<Int>? = null

    fun setTaskData(task:List<Task>, unfinished:ArrayDeque<Int>, finished:ArrayDeque<Int>){

        tasks = task
        remaining = unfinished
        done = finished
        notifyDataSetChanged()
    }


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

                    if (done?.contains(task.id) == true) {
                        changeToDone(iconImageView,taskTextView)
                        iconImageView.setOnClickListener {

                            if (done?.removeFirstOccurrence(task.id) == true) {
                                remaining!!.add(task.id)
                            }
                            Log.d("onBindViewHolder","Clicked Icon: ${task.id}, ${task.name}")
                            statusCallback.invoke(task.id?:-1, false)
                            notifyItemChanged(position)
                        }

                    }else if (remaining?.contains(task.id)== true){
                        changeToUndone(iconImageView,taskTextView)
                        iconImageView.setOnClickListener {

                            if (remaining?.removeFirstOccurrence(task.id)==true) {
                                done!!.add(task.id)
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
        tv.paintFlags = tv.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
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