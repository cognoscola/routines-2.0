package com.gorillamoa.routines.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.Button
import com.gorillamoa.routines.R

/**
 * An adapter which will hold maxNumber of items
 * @param maxNumber the number of buttons to create
 */
class TimePickerAdapter(private val maxNumber:Int): RecyclerView.Adapter<TimePickerAdapter.TimeItem>(){

    enum class TimeState{
        Minute,
        Hour
    }


    private var pickerState = TimeState.Hour

    lateinit var optionClickedCallback:(Int)->Unit

    override fun onBindViewHolder(holder: TimeItem, position: Int) {

        val value:Int
        if (pickerState == TimeState.Hour) {

            value = when (position) {
                0 -> 2
                1 -> 4
                2 -> 3
                3 -> 5
                4 -> 6
                5 -> 8
                6 -> 7
                7 -> 9
                8 -> 10
                9 -> 12
                10 -> 11
                11 -> 1
                else -> 0
            }
        }else{

            value = when (position) {

                0 -> 50
                1 -> 55
                2 -> 35
                3 -> 40
                4 -> 0
                5 -> 15
                6 -> 30
                7 -> 45
                8 -> 5
                9 -> 10
                10 -> 20
                11 -> 25
                else -> 0
            }
        }

        holder.button.text = "$value"
        holder.button.setOnClickListener {
            optionClickedCallback.invoke(value)

        }


        /** for this button we'll animate alpha randomly **/
        holder.button.startAnimation(AlphaAnimation(0.0f,1.0f).apply {
            duration = 500
            startOffset = (position * 100).toLong()
        })


        //TODO move number across screen when clicked
    }

    fun setHourState(){
        pickerState = TimeState.Hour
        notifyDataSetChanged()
    }

    fun setMinuteState(){

        pickerState = TimeState.Minute
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        return maxNumber
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeItem {

        val inflater = LayoutInflater.from(parent.context)
        return TimeItem(inflater.inflate(R.layout.item_timepicker,parent,false))
    }

    //TODO make sure no memory leak
    class TimeItem(view:View): RecyclerView.ViewHolder(view){

        var button:Button =view.findViewById(R.id.timeButton)
    }

    fun setOptionClickedCallBack(callback:(Int)->Unit){

        this.optionClickedCallback = callback
    }



}