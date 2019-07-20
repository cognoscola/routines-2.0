package com.gorillamoa.routines.tools

import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.text.Html
import android.text.Spanned

/**
 * Get the html spannable for the given input string
 * @param htmlString String
 * @return Spanned
 */
fun getHtml(htmlString:String): Spanned {
    //24 and above
    if(Build.VERSION.SDK_INT > Build.VERSION_CODES.N){
        return Html.fromHtml(htmlString, Html.FROM_HTML_MODE_COMPACT)
    }
    //below
    else{
        return Html.fromHtml(htmlString)
    }
}

fun Context.isWatch():Boolean{

    val uiServie = getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
    return uiServie.currentModeType == Configuration.UI_MODE_TYPE_WATCH
}