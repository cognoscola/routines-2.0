package com.gorillamoa.routines.adapter

import android.graphics.Point
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.recyclerview.widget.RecyclerView

class OffsetItemDecoration(private val windowManager: WindowManager): RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val offset =  ((getScreenWidth() /  (2.0f)) - view.layoutParams.width / 2).toInt()
        val  lp:ViewGroup.MarginLayoutParams =  view.layoutParams as ViewGroup.MarginLayoutParams

        if (parent.getChildAdapterPosition(view) == 0) {
            lp.leftMargin = 0
            setupOutRect(outRect, offset, true)

        } else if (parent.getChildAdapterPosition(view) == state.itemCount - 1) {
            lp.rightMargin = 0
            setupOutRect(outRect, offset, false)
        }
    }

    private fun setupOutRect(rect:Rect, offset:Int, start:Boolean) {

        if (start) {
            rect.left = offset
        } else {
            rect.right = offset
        }
    }

    private fun  getScreenWidth():Int {


        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        return size.x
    }
}