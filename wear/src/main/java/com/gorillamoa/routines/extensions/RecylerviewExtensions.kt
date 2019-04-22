package com.gorillamoa.routines.extensions

import android.content.Context
import android.view.WindowManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gorillamoa.routines.adapter.OffsetItemDecoration
import com.gorillamoa.routines.adapter.SimplePickerAdapter
import com.gorillamoa.routines.other.DetectableLinearSnapHelper

fun RecyclerView.createSimplePicker(
        choices: Array<String>,
        choiceCallback:((Int)->Any)? = null
        ){

    val snapHelper = DetectableLinearSnapHelper(this,choiceCallback)
    layoutManager =   GridLayoutManager(context, 1).apply {
        orientation = GridLayoutManager.HORIZONTAL
        scrollToPosition(0)
    }
    snapHelper.attachToRecyclerView(this)
    adapter = SimplePickerAdapter(choices){view ->

        val snapDistance = snapHelper.calculateDistanceToFinalSnap(layoutManager!!, view)
        if (snapDistance!![0] != 0 || snapDistance[1] != 0) {
            this.smoothScrollBy(snapDistance[0], snapDistance[1])
        }
    }

    addItemDecoration(OffsetItemDecoration(context.getSystemService(Context.WINDOW_SERVICE) as WindowManager))


}