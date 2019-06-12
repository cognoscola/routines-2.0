package com.gorillamoa.routines.core.views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.widget.Switch
import com.gorillamoa.routines.core.R

import com.gorillamoa.routines.core.extensions.getLocalSettings

class SwitchPreference: Switch {

    private var key:String? = null
    private var readyToAcceptInput = false

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        setupAttributes(attrs)
    }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs){
        setupAttributes(attrs)
    }

    override fun setChecked(checked: Boolean) {
        super.setChecked(checked)

        if (readyToAcceptInput) {
            context.getLocalSettings().edit()
                    .putBoolean(key,checked)
                    .apply()
        }
    }

    private fun setupAttributes(attrs: AttributeSet?) {
        // 6
        // Obtain a typed array of attributes
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.SwitchPreference,
                0, 0)

        // Extract custom attributes into member variables
        key = typedArray.getString(R.styleable.SwitchPreference_key)?:""
        typedArray.recycle()
        Log.d("$tag setupAttributes","Key:$key")
        if (key.isNullOrEmpty()) {
            throw Exception("Key Must not be null!")
        }

        isChecked = context.getLocalSettings().getBoolean(key,false)
        readyToAcceptInput = true


    }
}