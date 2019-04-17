package com.gorillamoa.routines.adapter

import android.graphics.Paint
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.gorillamoa.routines.R
import com.gorillamoa.routines.data.Task
import java.util.*

//TODO Comment this shit

const val MODE_DAILY = 0 //Showing the day's tasks
const val MODE_ALL = 1   //Showing all tasks

/**
 * Shows a list of tasks.
 * Usually there will be 2 different types of lists to display:
 * 1. a list of scheduled tasks
 * 2. a list of all tasks
 * @param itemClickedCallback is invoked when the user presses on the task
 * @param completionCallback is invoked when the user presses the item's status icon (to the left)
 * @param scheduledCallback is to notify the parent that the user requested to schedule or unschedule a task
 * @param addButtonCallback is when the user presses the + button on the item header
 */
class TaskListAdapter(
        private val itemClickedCallback:((Int)->Unit)? = null,
        private val completionCallback:((Int, Boolean)->Any?)? =null,
        private val scheduledCallback:((Int,Boolean)->Any?)? = null,
        private val addButtonCallback:(()->Any?)? = null): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val tag:String = TaskListAdapter::class.java.name

    private var tasks:List<Task>? = null          //All tasks to display
    private var done:ArrayDeque<Int>? = null      //A list of tasks to mark as done
    private var remaining:ArrayDeque<Int>? = null //A list of tasks to mark as not done


    private var mode = MODE_DAILY

    /**
     * Recieve task data
     * @param task is the list of tasks to display
     * @param unfinished all tasks not done
     * @param finished all tasks done
     */
    fun setTaskData(task:List<Task>, unfinished:ArrayDeque<Int>, finished:ArrayDeque<Int>){

        tasks = task
        remaining = unfinished
        done = finished

        notifyDataSetChanged()
    }


    companion object {
        const val VIEW_TYPE_TASK = 0  //A Task type item
        const val VIEW_TYPE_TITLE = 1 //A title type item
    }

    /**
     * Determine which Viewholder (Task or Title) to show
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return  if(viewType == VIEW_TYPE_TASK)
            TaskItemHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false))
        else {
            TitleViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_list_header, parent, false))
        }
    }

    fun setDailyMode(){
        mode = MODE_DAILY
        notifyDataSetChanged()
    }

    fun setAllMode(){
        mode = MODE_ALL
        notifyDataSetChanged()
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

                val titleString = when (mode) {
                    MODE_DAILY ->{
                        holder.headerTextView.context.getString(
                                if(tasks!!.isNotEmpty())
                                    R.string.task_list_title else R.string.task_list_empty)
                    }
                    MODE_ALL->{
                        holder.headerTextView.context.getString(R.string.task_all)
                    }
                    else -> {""}

                }

                holder.headerTextView.text = titleString
                holder.headerTextView.setTypeface(holder.headerTextView.typeface, Typeface.BOLD)
                holder.addButton.setOnClickListener { addButtonCallback?.invoke() }
            }

            if (holder is TaskItemHolder) {

                holder.apply {

                    val task =  tasks!![position - 1]
                    taskTextView.text = task.name

                    when(mode){
                        MODE_DAILY -> styleForDailyAppearance(holder,task,position)
                        MODE_ALL -> styleForAllAppearance(holder,task,position)

                    }

                    taskTextView.setOnClickListener {
                        itemClickedCallback?.invoke(task.id?:-1)
                    }
                }
            }
        }
    }

    private fun styleForAllAppearance(holder: TaskItemHolder, task: Task, position: Int) {


        if (isScheduled(task)) {

            holder.iconImageView.setImageResource(if(isScheduled(task))R.drawable.ic_schedule_black_24dp else R.drawable.ic_remove_black_24dp)
            holder.iconImageView.setOnClickListener {
                Toast.makeText(holder.iconImageView.context,"Unscheduling: ${task.name} [$position]",Toast.LENGTH_SHORT).show()
            }

        }else{
            holder.iconImageView.setImageResource(if(isScheduled(task))R.drawable.ic_schedule_black_24dp else R.drawable.ic_remove_black_24dp)
            holder.iconImageView.setOnClickListener {
                Toast.makeText(holder.iconImageView.context,"Scheduling: ${task.name} [$position]",Toast.LENGTH_SHORT).show()
//                completionCallback.invoke(task.id?:-1, false)
//                notifyItemChanged(position)
            }
        }


    }

    private fun isScheduled(task:Task):Boolean{


        return (done?.contains(task.id)?:false).or(remaining?.contains(task.id)?:false)

    }

    private fun changeToUnknown(holder:TaskItemHolder){
        holder.iconImageView.setImageResource(R.drawable.ic_priority_high_black_24dp)
    }

    private fun changeToDone(holder:TaskItemHolder){
        holder.iconImageView.setImageResource(R.drawable.ic_done_black_24dp)
        holder.taskTextView.paintFlags = holder.taskTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    }

    private fun changeToUndone(holder:TaskItemHolder){
        holder.iconImageView.setImageResource(R.drawable.ic_radio_button_unchecked_black_24dp)
        holder.taskTextView.paintFlags = holder.taskTextView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
    }

    private fun styleForDailyAppearance(holder:TaskItemHolder,task:Task,position: Int){
        if (done?.contains(task.id) == true) {
            changeToDone(holder)
            holder.iconImageView.setOnClickListener {

                if (done?.removeFirstOccurrence(task.id) == true) {
                    remaining!!.add(task.id)
                }
                Log.d("onBindViewHolder","Clicked Icon: ${task.id}, ${task.name}")
                completionCallback?.invoke(task.id?:-1, false)
                notifyItemChanged(position)
            }

        }else if (remaining?.contains(task.id)== true){
            changeToUndone(holder)
            holder.iconImageView.setOnClickListener {

                if (remaining?.removeFirstOccurrence(task.id)==true) {
                    done!!.add(task.id)
                }
                completionCallback?.invoke(task.id?:-1, true)
                notifyItemChanged(position)
            }

        }else{
            //we don't know if this task is done or not!
            changeToUnknown(holder)
        }

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
        var addButton = item.findViewById<ImageView>(R.id.addButton)

    }
}