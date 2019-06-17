package com.gorillamoa.routines.adapter

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.gorillamoa.routines.R

class DoubleRowSelectAdapter(
        private val options:Array<String>,
        private val itemClickedCallback:((String)->Any?)? = null,
        private val style:Int = 0):RecyclerView.Adapter<DoubleRowSelectAdapter.DoubleRowItem>()
{

    companion object {
        public val STYLE_NUMBER = 0
        public val STYLE_LONGTEXT = 1 //choices with text > 6 characters
        public val STYLE_SHORTTEXT = 2 //3 or less characters

    }

    override fun getItemCount(): Int {
        return options.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoubleRowItem {
        val inflater = LayoutInflater.from(parent.context)
        //TODO change the layout to be more generic
        return DoubleRowItem(inflater.inflate(R.layout.item_timepicker, parent, false))
    }

    override fun onBindViewHolder(holder: DoubleRowItem, position: Int) {
        holder.button.text = options[position]
        holder.button.setOnClickListener {
            itemClickedCallback?.invoke(options[position])
        }



        when (style) {
            STYLE_LONGTEXT ->{

                holder.button.textSize = 15.0f
                holder.button.layoutParams.width = holder.button.context.dipToPixels(80.0f).toInt()


            }
            STYLE_NUMBER ->{}
            STYLE_SHORTTEXT ->{}
        }

        holder.button.startAnimation(AlphaAnimation(0.0f,1.0f).apply {
            duration = 500
            startOffset = (position * 100).toLong()
        })
    }

    class DoubleRowItem(view:View): RecyclerView.ViewHolder(view){

        var button: Button =view.findViewById(R.id.timeButton)

    }
}

fun Context.dipToPixels(dipValue: Float) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, resources.displayMetrics)