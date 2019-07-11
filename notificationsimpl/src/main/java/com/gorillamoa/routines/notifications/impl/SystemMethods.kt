package com.gorillamoa.routines.notifications.impl

import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration

/**
 * Determine wether this device is a watch of a mobile device
 * @receiver Context
 * @return Boolean
 */
fun Context.isWatch():Boolean{

    val uiServie = getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
    return uiServie.currentModeType == Configuration.UI_MODE_TYPE_WATCH
}