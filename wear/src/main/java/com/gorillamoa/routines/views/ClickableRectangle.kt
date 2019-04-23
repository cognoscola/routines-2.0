package com.gorillamoa.routines.views

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect

open class ClickableRectangle(
        val center_x:Int,
        val center_y:Int,
        val width:Int,
        val height:Int)
{

    var onClickListener:(()->Any?)? = null

    val dstRect = Rect()
    val debugPaint = Paint().apply {
        color = Color.CYAN
        style = Paint.Style.STROKE
        strokeWidth = 2.0f
        isAntiAlias = true
    }


    init{
        dstRect.set(
                (center_x - (width*0.5).toInt()),
                (center_y - (height*0.5).toInt()),
                (center_x + (width*0.5).toInt()),
                (center_y + (height*0.5).toInt())
        )


        //TODO supppor the author
//<div>Icons made by <a href="https://www.freepik.com/" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/" 			    title="Flaticon">www.flaticon.com</a> is licensed by <a href="http://creativecommons.org/licenses/by/3.0/" 			    title="Creative Commons BY 3.0" target="_blank">CC 3.0 BY</a></div>
    }

    open fun draw(canvas: Canvas) {
        if (ClickableRectangle.DEBUG) {
            canvas.drawRect(dstRect,debugPaint)
        }
    }

    companion object {
        var DEBUG = false

        fun enableDebug(){
            DEBUG = true
        }
        fun disableDebug(){
            DEBUG = false
        }

    }

    open fun isTouched(x:Int,y:Int):Boolean{

        return if (dstRect.contains(x, y)) {
            onClickListener?.invoke()
            true
        }else{
            false
        }
    }
}