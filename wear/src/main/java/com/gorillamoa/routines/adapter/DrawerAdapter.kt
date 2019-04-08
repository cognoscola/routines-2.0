package com.gorillamoa.routines.adapter


import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.wear.widget.drawer.WearableNavigationDrawerView
import com.gorillamoa.routines.R

class DrawerAdapter internal constructor(private val mContext: Context) : WearableNavigationDrawerView.WearableNavigationDrawerAdapter() {

    override fun getItemText(index: Int): String {

        return when (index) {
            0 -> "Today"
            1 -> "All"
            else -> "Unknown"
        }
    }

    override fun getItemDrawable(index: Int): Drawable {
        return mContext.getDrawable(when(index) {
            0 -> R.drawable.ic_wb_sunny_black_24dp
            1 -> R.drawable.ic_format_list_bulleted_black_24dp
            else -> R.drawable.ic_extension_black_24dp
        })!!
    }

/*
    override fun onItemSelected(index: Int) {
        when (index) {
            0 -> Log.d("onItemSelected","Chose Today")
            1 -> Log.d("onItemSelected","Chose All")
        }
    }
*/

    override fun getCount(): Int {
        return 2
    }
}