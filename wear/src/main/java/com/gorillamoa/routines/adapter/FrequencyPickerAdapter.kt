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
class FrequencyPickerAdapter(
        private val options:List<String>): RecyclerView.Adapter<FrequencyPickerAdapter.TimeItem>(){


    override fun onBindViewHolder(holder: TimeItem, position: Int) {

        holder.button.text = options[position]
        holder.button.setOnClickListener {

        }


        /** for this button we'll animate alpha randomly **/
        holder.button.startAnimation(AlphaAnimation(0.0f,1.0f).apply {
            duration = 500
            startOffset = (position * 100).toLong()
        })

    }

    override fun getItemCount(): Int {
        return options.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeItem {

        val inflater = LayoutInflater.from(parent.context)
        return TimeItem(inflater.inflate(R.layout.item_timepicker,parent,false))
    }


    class TimeItem(view:View): RecyclerView.ViewHolder(view){

        var button:Button =view.findViewById(R.id.timeButton)
    }

}