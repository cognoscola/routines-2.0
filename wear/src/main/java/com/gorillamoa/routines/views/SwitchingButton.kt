package com.gorillamoa.routines.views

import android.content.Context
import android.graphics.*
import androidx.core.content.ContextCompat
import com.gorillamoa.routines.R
import android.graphics.Bitmap
import java.util.*

//CLEAN this code

class SwitchingButton(
        centerX:Int,
        centerY:Int,
        width:Int,
        height:Int,
        val padding:Int = 0,
        val context: Context):ClickableRectangle(centerX,centerY,width,height){


    private var paint = Paint().apply {
        color = Color.BLUE
        style = Paint.Style.STROKE
        strokeWidth = 3.0f
        isAntiAlias = true

    }
    private var srcRect =Rect()
    private var drawingRect = Rect()
    private var bitmap:Bitmap? = null
    private var states=ArrayDeque<Pair<String,Int>>()
    private var currentState:String =""

    override fun draw(canvas: Canvas){
        super.draw(canvas)
        bitmap?.let {

            canvas.drawBitmap(bitmap!!,srcRect,dstRect,paint)
        }
    }

    /**
     * Add a state to this button. When pressed, the button switches to a different
     * state/icon
     */
    fun addState(state:String, icon:Int):String {

        //check if we can find an existing
        states.find { it.first == state }?.let { states.remove(it) }
        states.add(Pair(state, icon))

        if (currentState.isBlank()) {
            currentState = states.first.first

            if (bitmap == null) {
              setBitmap(icon)
            }
        }

        return currentState
    }

    fun setState(key:String){

        states.find {
            it.first == key
        }?.let {
            currentState = it.first
            setBitmap(it.second)
        }
    }

    private fun setBitmap(icon: Int) {
        bitmap = getBitmap(context, icon)
        val scale = width.toFloat() / bitmap!!.width.toFloat()
        bitmap = Bitmap.createScaledBitmap(bitmap!!,
                (bitmap!!.width * scale).toInt(),
                (bitmap!!.height * scale).toInt(), true)
        srcRect.set(0, 0, bitmap!!.width, bitmap!!.height)
    }


    fun getState():String{
        return currentState
    }

    /**
     * sets the next state
     */
    fun nextState():String{

        if (states.size > 0) {

            //remove the first and make it the last
            val current = states.removeFirst()
            states.add(current)

            //now we peek at the first
            val nextPair = states.peekFirst()

            //is it the same one as now?
            if (nextPair.first != currentState) {
                currentState = nextPair.first

                bitmap?.recycle()
                try {
                    setBitmap(nextPair.second)
                } catch (e: Exception) {
                    setBitmap(R.drawable.ic_warning_black_24dp)
                }
            }
        }else{

            return "Unknown"
        }

        return currentState

    }

    private fun getBitmap(context:Context,drawableRes: Int): Bitmap {
        val drawable = ContextCompat.getDrawable(context,drawableRes)
        val canvas = Canvas()
        val bitmap = Bitmap.createBitmap(drawable!!.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        canvas.setBitmap(bitmap)

        val edge = padding.div(2)

        drawable.setBounds(
                0 + edge,
                0 + edge,
                drawable.intrinsicWidth - edge,
                drawable.intrinsicHeight - edge)
        drawable.draw(canvas)

        return bitmap
    }


}