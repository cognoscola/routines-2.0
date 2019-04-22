package com.gorillamoa.routines.other

import android.view.View
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView

class DetectableLinearSnapHelper(var recyclerView: RecyclerView, var snapCallback:((Int)->Any?)?): LinearSnapHelper(){

        var selectedPosition = -1

        override fun findSnapView(layoutManager: RecyclerView.LayoutManager): View? {
            val view = super.findSnapView(layoutManager)

            if (view != null) {
                val newPosition = layoutManager.getPosition(view)

                if ((newPosition != selectedPosition) && (recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE)) {
                    snapCallback?.invoke(newPosition)
                    selectedPosition = newPosition
                }
            }
            return view
        }
    }