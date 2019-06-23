package com.gorillamoa.routines.adapter

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.gorillamoa.routines.R
import com.gorillamoa.routines.core.data.Task

import java.util.*
import kotlin.collections.ArrayList

//TODO Comment this shit

const val MODE_DAILY = 0 //Showing the day's tasks
const val MODE_ALL = 1   //Showing all tasks
const val MODE_PICKER = 2 //pick an existing unscheduled task

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
        private val itemClickedCallback:((Long)->Unit)? = null,
        private val completionCallback:((Long, Boolean)->Any?)? =null,
        private val scheduledCallback:((Long,Boolean)->Any?)? = null,
        private val addButtonCallback:((Boolean)->Any?)? = null): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    @Suppress("unused")
    private val tag:String = TaskListAdapter::class.java.name

    private var tasks:List<Task>? = null          //All tasks to display
    private var finished:ArrayDeque<Long>? = null      //A list of tasks to mark as finished
    private var unfinished:ArrayDeque<Long>? = null //A list of tasks to mark as not finished
    private var order : ArrayList<Int> = ArrayList()

    private var mode = MODE_DAILY
    private var lastTimeTouch = 0L
    private var lastInteractedItemPosition = -1

    /**
     * Have we recently interacted with an item on this list?
     */
    private fun recentlyInteracted():Boolean = (SystemClock.uptimeMillis() - lastTimeTouch) < 1000

    /**
     * Recieve task data
     * @param task is the list of tasks to display
     * @param unfinished all tasks not finished
     * @param finished all tasks finished
     */
    fun setTaskData(task:List<Task>, unfinished:ArrayDeque<Long>, finished:ArrayDeque<Long>){

        tasks = task
        this.unfinished = unfinished
        this.finished = finished
        calculateOrder()
        notifyDataSetChanged()
    }

    /**
     * Update a list of finished tasks
     * @param finished is the new list of finished items
     * It is likely that this list will change when the user interacts with the Listview (and
     * not by other means) so that means only one item changed. But it its not the case then
     * just update all items.
     */
    fun updateFinishedList(finished: ArrayDeque<Long>){

        this.finished = finished
       // calculateOrder()

        if (recentlyInteracted()) {
            notifyItemChanged(lastInteractedItemPosition)
        }else{
            calculateOrder()
            notifyDataSetChanged()
        }
    }

    private fun calculateOrder(){
        order.clear()
        when (mode) {
            MODE_DAILY -> {
                tasks?.forEachIndexed { index, task ->

                    if (isScheduled(task)) {
                        order.add(index)
                    }
                }
            }
            else -> {tasks?.forEachIndexed { index, _ ->
                order.add(index)
            }}

        }
    }


    /**
     * Update a list of finished tasks
     * @param unfinished is the new list of finished items
     * It is likely that this list will change when the user interacts with the ListView (and
     * not by other means) so that means only one item changed. But it its not the case then
     * just update all items.
     */
    fun updateRemainingList(unfinished: ArrayDeque<Long>) {

        this.unfinished = unfinished
        if (recentlyInteracted()) {
            notifyItemChanged(lastInteractedItemPosition)
        }else {
            calculateOrder()
            notifyDataSetChanged()
        }
    }


    companion object {
        const val VIEW_TYPE_TASK = 0  //A Task type item
        const val VIEW_TYPE_TITLE = 1 //A title type item
        const val VIEW_TYPE_ADD = 2 // A a new task to this list
    }

    /**
     * Determine which Viewholder (Task or Title) to show
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_TASK -> TaskItemHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false))
            VIEW_TYPE_TITLE -> TitleViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_list_header, parent, false))
            VIEW_TYPE_ADD ->AddItemHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false))
            else ->{TaskItemHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false))}

        }

    }

    /**
     * Display items on this list as if viewing DAILY tasks
     */
    fun setDailyMode(){
        mode = MODE_DAILY
        calculateOrder()
        notifyDataSetChanged()
    }

    /**
     * Display items on this list as if viewing ALL tasks
     */
    fun setAllMode(){
        mode = MODE_ALL
        calculateOrder()
        notifyDataSetChanged()
    }

    fun setPickerMode(){
        mode = MODE_PICKER
        calculateOrder()
        notifyDataSetChanged()
    }


    /**
     * get the number of items to display.
     * note that we show an extra for the title, hence the +1
     * @return the number of items to display
     */
    override fun getItemCount(): Int {
        return order.size + 1 + 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        (tasks)?.let {

            if (holder is TitleViewHolder) {
                val titleString = when (mode) {
                    MODE_DAILY ->{
                        holder.headerTextView.context.getString(
                                if(order.isNotEmpty()) R.string.task_list_title else R.string.task_list_empty)
                    }

                    MODE_ALL->{
                        if(tasks!!.isNotEmpty())holder.headerTextView.context.getString(R.string.task_all) else holder.headerTextView.context.getString(R.string.task_all_empty)
                    }

                    MODE_PICKER->{
                        holder.headerTextView.context.getString(R.string.task_pick)
                    }
                    else -> {""}

                }

                holder.headerTextView.text = titleString
                holder.headerTextView.setTypeface(holder.headerTextView.typeface, Typeface.BOLD)

            }

            if (holder is TaskItemHolder) {

                holder.apply {

                    Log.d("$tag onBindViewHolder","order size:${order.size}")
                    Log.d("$tag onBindViewHolder","order size:${tasks!!.size}")

                    val task =  tasks!![order[position - 1]]
                    taskTextView.text = task.name

                    when(mode){
                        MODE_DAILY -> {
                            styleForDailyAppearance(holder,task,position)
                            taskTextView.setOnClickListener {
                                itemClickedCallback?.invoke(task.id?:-1L)
                            }
                        }
                        MODE_ALL -> {
                            styleForAllAppearance(holder,task,position)
                            taskTextView.setOnClickListener {
                                itemClickedCallback?.invoke(task.id?:-1)
                            }

                        }
                        MODE_PICKER -> styleForPicking(holder,task)
                    }
                }
            }

            if (holder is AddItemHolder) {

                when (mode) {
                    MODE_DAILY ->{
                        holder.taskTextView.apply {
                            text = context.getString(R.string.task_schedule)
                            addSchedulingAction(this)
                        }
                        holder.iconImageView.apply {
                          makeAddIcon(this)
                            addSchedulingAction(this)
                        }

                    }
                    else ->{
                        holder.taskTextView.apply {
                            text = context.getString(R.string.task_create)
                            addCreationAction(this)
                        }
                        holder.iconImageView.apply {
                            makeAddIcon(this)
                            addCreationAction(this)
                        }

                    }
                }

            }


        }
    }

    private fun makeAddIcon(iv: ImageView) {
        iv.setImageResource(R.drawable.ic_add_black_24dp)
        iv.setColorFilter(Color.WHITE)
    }

    private fun addSchedulingAction(view:View){
        view.setOnClickListener {
            addButtonCallback?.invoke(true)
        }
    }

    private fun addCreationAction(view:View){
        view.setOnClickListener {
            addButtonCallback?.invoke(false)
        }
    }

    private fun styleForPicking(holder: TaskItemHolder, task: Task) {
        uncrossLetters(holder.taskTextView)
        if (isScheduled(task)) {

            holder.iconImageView.setImageResource(R.drawable.ic_schedule_black_24dp )
            holder.iconImageView.setColorFilter(Color.DKGRAY)
            holder.taskTextView.setTextColor(Color.DKGRAY)

        }else{
            useDefaultColorFilters(holder)
            holder.iconImageView.setImageResource(R.drawable.ic_remove_black_24dp)
            holder.iconImageView.setOnClickListener {
                scheduleAndGoBack(task)
            }
            holder.taskTextView.setOnClickListener {
                scheduleAndGoBack(task)
            }
        }
    }

    private fun scheduleAndGoBack(task:Task){
        scheduledCallback?.invoke(task.id!!, true)
        setDailyMode()

    }

    private fun useDefaultColorFilters(holder: TaskItemHolder) {
        holder.iconImageView.setColorFilter(Color.WHITE)
        holder.taskTextView.setTextColor(Color.WHITE)
    }

    private fun styleForAllAppearance(holder: TaskItemHolder, task: Task, position: Int) {

        uncrossLetters(holder.taskTextView)
        useDefaultColorFilters(holder)

        if (isScheduled(task)) {

            holder.iconImageView.setImageResource(if(isScheduled(task))R.drawable.ic_schedule_black_24dp else R.drawable.ic_remove_black_24dp)
            holder.iconImageView.setOnClickListener {
                recordLastInteraction(position)
                scheduledCallback?.invoke(task.id!!,false)
            }

        }else{
            holder.iconImageView.setImageResource(if(isScheduled(task))R.drawable.ic_schedule_black_24dp else R.drawable.ic_remove_black_24dp)
            holder.iconImageView.setOnClickListener {
                recordLastInteraction(position)
                scheduledCallback?.invoke(task.id!!,true)
            }
        }
    }

    private fun recordLastInteraction(position: Int) {
        lastTimeTouch = SystemClock.uptimeMillis()
        lastInteractedItemPosition = position
    }

    private fun isScheduled(task:Task):Boolean{
        return (finished?.contains(task.id)?:false).or(unfinished?.contains(task.id)?:false)
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
        uncrossLetters(holder.taskTextView)
    }

    private fun uncrossLetters(textView: TextView) {
        textView.paintFlags = textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
    }

    /**
     * Style the item for display as scheduled task
     */
    private fun styleForDailyAppearance(holder:TaskItemHolder,task:Task,position: Int){

        useDefaultColorFilters(holder)

        if (isScheduled(task)) {

            if (finished?.contains(task.id) == true) {
                changeToDone(holder)

                holder.iconImageView.setOnClickListener {
                    recordLastInteraction(position)
                    completionCallback?.invoke(task.id?:-1, false)
                }

            }else if (unfinished?.contains(task.id)== true){
                changeToUndone(holder)
                holder.iconImageView.setOnClickListener {
                    recordLastInteraction(position)
                    completionCallback?.invoke(task.id?:-1, true)
                }
            }else{
                //we don't know if this task is finished or not!
                changeToUnknown(holder)
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> VIEW_TYPE_TITLE
            (order.size + 1 ) -> VIEW_TYPE_ADD
            else -> VIEW_TYPE_TASK

        }


    }

    inner class TaskItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
         var taskTextView: TextView = itemView.findViewById(R.id.taskNameTextView)
        var iconImageView:ImageView = itemView.findViewById(R.id.iconTextView)
    }

    inner class AddItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var taskTextView: TextView = itemView.findViewById(R.id.taskNameTextView)
        var iconImageView:ImageView = itemView.findViewById(R.id.iconTextView)
    }

    inner class TitleViewHolder(item:View):RecyclerView.ViewHolder(item){
        var headerTextView:TextView = item.findViewById(R.id.headerTextView)

    }


}